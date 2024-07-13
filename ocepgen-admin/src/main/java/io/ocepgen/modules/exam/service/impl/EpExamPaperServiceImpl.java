package io.ocepgen.modules.exam.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.ocepgen.common.constant.Constant;
import io.ocepgen.common.exception.OcepgenException;
import io.ocepgen.common.page.PageData;
import io.ocepgen.common.service.impl.CrudServiceImpl;
import io.ocepgen.modules.data.vo.AutoAndManualPaperCountVo;
import io.ocepgen.modules.exam.autogenpaper.ExamPaper;
import io.ocepgen.modules.exam.autogenpaper.GenExamPaperService;
import io.ocepgen.modules.exam.autogenpaper.vo.GenExamPaperRuleVo;
import io.ocepgen.modules.exam.dao.EpExamPaperDao;
import io.ocepgen.modules.exam.dto.EpExamPaperDTO;
import io.ocepgen.modules.exam.dto.ExamGenerateParamsDTO;
import io.ocepgen.modules.exam.dto.ExamSubtitleDTO;
import io.ocepgen.modules.exam.entity.EpExamPaperEntity;
import io.ocepgen.modules.exam.enums.ExamPaperScopeEnum;
import io.ocepgen.modules.exam.service.EpExamPaperService;
import io.ocepgen.modules.exam.service.ExamGenerateParamsService;
import io.ocepgen.modules.exam.service.ExamSubtitleService;
import io.ocepgen.modules.exam.utils.ExportWordUtil;
import io.ocepgen.modules.exam.utils.Word2PdfUtil;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.ocepgen.modules.question.enums.QuestionTypeEnum;
import io.ocepgen.modules.question.service.QbQuestionService;
import io.ocepgen.modules.security.user.SecurityUser;
import io.ocepgen.modules.security.user.UserDetail;
import io.ocepgen.modules.sys.enums.SuperAdminEnum;
import io.ocepgen.modules.sys.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 试卷表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Slf4j
@Service
public class EpExamPaperServiceImpl extends CrudServiceImpl<EpExamPaperDao, EpExamPaperEntity, EpExamPaperDTO> implements EpExamPaperService {

    @Resource
    ExamSubtitleService examSubtitleService;
    @Resource
    QbQuestionService questionService;
    @Resource
    SysUserService sysUserService;
    @Resource
    ExamGenerateParamsService examGenerateParamsService;

    @Override
    public QueryWrapper<EpExamPaperEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<EpExamPaperEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);

        return wrapper;
    }

    private List<EpExamPaperDTO> getList(Map<String, Object> params) {
        //普通管理员，只能查询所属学校的用户
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            String schoolId = user.getSchoolId().toString();
            params.put("userId", user.getId());
            params.put("schoolId", schoolId);
        }

        List<EpExamPaperEntity> list = baseDao.getList(params);
        List<EpExamPaperDTO> dtoList = new ArrayList<>(list.size());

        list.forEach(entity -> {
            // 如果当前试卷是账号私有的，并且创建者又不是当前用户，那么将这条数据过滤掉，也就是跳出本次循环，不添加到dtoList
            if (entity.getScope() == ExamPaperScopeEnum.PRIVATE.value() && !user.getId().equals(entity.getCreator())) {
                return;
            }

            EpExamPaperDTO examPaperDTO = new EpExamPaperDTO();
            // 设置创建者名称（如果不为空的话）
            if (entity.getCreator() != null) {
                examPaperDTO.setCreatorName(sysUserService.getUsernameAndRealNameByUserId(entity.getCreator()));
            }
            BeanUtils.copyProperties(entity, examPaperDTO);
            dtoList.add(examPaperDTO);
        });
        return dtoList;
    }


    @Override
    public PageData<EpExamPaperDTO> page(Map<String, Object> params) {

        //分页
        IPage<EpExamPaperEntity> page = getPage(params, Constant.CREATE_DATE, false);

        return new PageData<>(getList(params), page.getTotal());
    }

    @Override
    public List<EpExamPaperDTO> list(Map<String, Object> params) {
        return getList(params);
    }

    @Override
    public EpExamPaperDTO generateExamPaper(GenExamPaperRuleVo rule) {
        // 查询题目
        Map<String, Object> queryQuestionParams = new HashMap<>();
        queryQuestionParams.put("courseId", String.valueOf(rule.getCourseId()));
        // 只查询用户设定过的题型分布
        List<Integer> typeList = rule.getQuestionTypeWeightMap()
                .keySet().stream().map(QuestionTypeEnum::value).collect(Collectors.toList());
        queryQuestionParams.put("typeList", typeList);
        List<QbQuestionDTO> questions = questionService.getListAndOptions(queryQuestionParams); //选项也要加上
        log.warn("智能组卷查询的题目数量:{}", questions.size());
        log.info("queryQuestionParams:{}", queryQuestionParams);
        if (questions.size() < rule.getExpectedQuestionCount()) {
            throw new OcepgenException("题目数量不足，请前往题库管理页面添加题目");
        }
        // 对题型分布进行处理，保证题型总权重为1
        float totalWeight = (float) rule.getQuestionTypeWeightMap().values().stream().mapToDouble(v -> v).sum();
        rule.getQuestionTypeWeightMap().forEach((type, weight) -> {
            //  重新计算每个题型的权重比例
            rule.getQuestionTypeWeightMap().put(type, weight / totalWeight);
        });
        // 初始化生成服务
        GenExamPaperService genExamPaperService;
        ExamGenerateParamsDTO generatePaperConfigDto = null;
        if (rule.getConfigId() != null && rule.getConfigId() != 0) { //用户自定义配置
            // 查询生成配置
            generatePaperConfigDto = examGenerateParamsService.get(rule.getConfigId());
            if (generatePaperConfigDto == null) {
                throw new OcepgenException("组卷配置不存在");
            }
            genExamPaperService = new GenExamPaperService(rule, generatePaperConfigDto, questions);
        } else { //默认配置
            genExamPaperService = new GenExamPaperService(rule, questions);
        }
        ExamPaper paper = genExamPaperService.generate();
        // 处理生成的试卷
        EpExamPaperDTO examPaperDTO = new EpExamPaperDTO();
        examPaperDTO.setScore((float)paper.getScore());
        examPaperDTO.setQuestionCount(paper.getQuestionSize());
        examPaperDTO.setCourseId(rule.getCourseId());
        examPaperDTO.setDifficulty((float)paper.getDifficulty());
        examPaperDTO.setType(1); //类型为智能组卷
        // 将题目装进小标题列表
        List<ExamSubtitleDTO> subtitles = new ArrayList<>();
        typeList.forEach(type -> {
            // 当前类型的题目列表
            List<QbQuestionDTO> questionsOfType = new ArrayList<>();
            // 使用迭代器来遍历题目列表，已经添加过的就通过iterator安全地把它删了，防止重复遍历和重复删除
            Iterator<QbQuestionDTO> iterator = paper.getQuestions().iterator();
            while (iterator.hasNext()) {
                QbQuestionDTO question = iterator.next();
                if (Objects.equals(question.getType(), type)) {
                    // 加入当前题型的题目列表
                    questionsOfType.add(question);
                    // 从题目列表中删除，防止重复遍历
                    iterator.remove();
                }
            }

            ExamSubtitleDTO subtitle = new ExamSubtitleDTO();
            // 设置小标题名称
            subtitle.setExamSubtitleName(QuestionTypeEnum.getByValue(type).label()
                    + "（ 共" + questionsOfType.size() + "题 ）");
            // 设置排序
            subtitle.setSort(type);
            // 设置当前类型的小标题题目列表
            subtitle.setQuestions(questionsOfType);
            // 加入小标题列表
            subtitles.add(subtitle);
        });
        // 将小标题列表根据排序字段进行排序
        examPaperDTO.setSubtitles(subtitles.stream().sorted(Comparator.comparingInt(ExamSubtitleDTO::getSort))
                .collect(Collectors.toList()));

        // 更新用户自定义配置使用频率和准确率
        if (generatePaperConfigDto != null) {
            updateGeneratePaperConfig(paper, generatePaperConfigDto);
        }
        return examPaperDTO;
    }

    private void updateGeneratePaperConfig(ExamPaper paper, ExamGenerateParamsDTO generatePaperConfigDto) {
        log.warn("本次组卷获得个体适应度为：{}", paper.getFitness());
        // 计算本次组卷准确度，准确度最高为1，适应度越接近0，准确率越高
        float accuracy = (float) (1 / (1 + paper.getFitness()));
        // 第一次使用，直接设置准确率为本次准确度
        if (generatePaperConfigDto.getUseTimes() == 0) {
            generatePaperConfigDto.setAccuracy(accuracy);
        } else {
            // 更新准确率，新准确度 = (旧准确度 * 使用次数 + 本次准确度) / (使用次数 + 1)
            generatePaperConfigDto.setAccuracy(
                    (generatePaperConfigDto.getAccuracy() * generatePaperConfigDto.getUseTimes() + accuracy)
                            / (generatePaperConfigDto.getUseTimes() + 1)
            );
        }
        // 更新使用频率
        generatePaperConfigDto.setUseTimes(generatePaperConfigDto.getUseTimes() + 1);
        examGenerateParamsService.update(generatePaperConfigDto);
    }

    @Override
    public EpExamPaperDTO get(Long id) {
        EpExamPaperDTO examPaperDTO = super.get(id);

        // 填充小标题（包含题目）列表
        examPaperDTO.setSubtitles(examSubtitleService.getListByExamPaperId(id));

        return examPaperDTO;
    }

    @Override
    @Transactional
    public void save(EpExamPaperDTO dto) {
        // 校验小标题是否为空
        if (dto.getSubtitles().size() == 0) {
            throw new OcepgenException(500, "小标题不能为空");
        }
        // 先保存试卷，返回试卷id到dto
        super.save(dto);
        // 保存小标题列表
        dto.getSubtitles().forEach(
                subtitle -> {
                    subtitle.setExamPaperId(dto.getId());
                    examSubtitleService.save(subtitle);
                });
    }

    @Override
    @Transactional
    public void update(EpExamPaperDTO dto) {
        // 先查询 待删除 的小标题id列表
        List<Long> deleteSubtitleIds =
                examSubtitleService.getListByExamPaperId(dto.getId())
                        .stream()
                        .map(ExamSubtitleDTO::getId)
                        .collect(Collectors.toList());

        // 更新小标题列表
        dto.getSubtitles().forEach(
                subtitle -> {
                    // 过滤 待删除小标题列表
                    if (deleteSubtitleIds.contains(subtitle.getId())) {
                        // 过滤
                        deleteSubtitleIds.remove(subtitle.getId());
                        // 更新
                        examSubtitleService.update(subtitle);
                        return; //跳出本次循环
                    }
                    // 如果当前小标题不存在于数据库，那么说明是新增。
                    subtitle.setExamPaperId(dto.getId()); //填充试卷id
                    examSubtitleService.save(subtitle);
                });
        // 删除 待删除 的小标题
        if (deleteSubtitleIds.size() > 0) {
            examSubtitleService.delete(deleteSubtitleIds.toArray(new Long[0]));
        }

        super.update(dto);
    }

    @Override
    @Transactional
    public void delete(Long[] ids) {

        examSubtitleService.deleteByExamPaperId(ids);
        super.delete(ids);
    }

    @Override
    public void exportWord(Map<String, Object> params, HttpServletResponse response) {
        if (params.get("id") == null) {
            throw new OcepgenException("请选择需要导出为Word的试卷");
        }
        EpExamPaperDTO paperDTO = get(Long.parseLong((String)params.get("id")));

        try {
            ExportWordUtil.exportPaperDTO(params, paperDTO, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void exportPDF(Map<String, Object> params, HttpServletResponse response) {
        if (params.get("id") == null) {
            throw new OcepgenException("请选择需要导出为PDF的试卷");
        }
        EpExamPaperDTO paperDTO = get(Long.parseLong((String)params.get("id")));
        try {
            // 首先生成Word文档并获取其路径
            String path = ExportWordUtil.exportPaperDTOToTempFile(params, paperDTO);
            // 构建完整路径
//            org.springframework.core.io.Resource resource = new ClassPathResource("wordFilePath");
//            String absolutePath = resource.getFile().getAbsolutePath();

            // 将Word文档转换为PDF并通过HttpServletResponse返回
            boolean success = Word2PdfUtil.convertDoc2PdfAndSend(path, response);
            if (!success) {
                // 处理转换失败的情况
                throw new OcepgenException("导出PDF失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportAnswer(Map<String, Object> params, HttpServletResponse response) {
        if (params.get("id") == null) {
            throw new OcepgenException("请选择需要导出答案的试卷");
        }
        EpExamPaperDTO paperDTO = get(Long.parseLong((String) params.get("id")));

        try {
            ExportWordUtil.exportPaperAnswer(paperDTO, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public AutoAndManualPaperCountVo getAutoAndManualPaperCount(Map<String, Object> params) {

        AutoAndManualPaperCountVo countVo = new AutoAndManualPaperCountVo();
        params.put("type", "1");
        countVo.setAutoPaperCount(baseDao.getExamPaperCountByCondition(params));
        params.put("type", "0");
        countVo.setManualPaperCount(baseDao.getExamPaperCountByCondition(params));
        return countVo;
    }


    @Override
    public Integer getExamPaperCountByCondition(Map<String, Object> params) {
        return baseDao.getExamPaperCountByCondition(params);
    }
}
