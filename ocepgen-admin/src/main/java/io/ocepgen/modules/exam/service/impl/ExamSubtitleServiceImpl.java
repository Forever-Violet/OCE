package io.ocepgen.modules.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.ocepgen.common.service.impl.CrudServiceImpl;
import io.ocepgen.modules.exam.dao.ExamSubtitleDao;
import io.ocepgen.modules.exam.dto.ExamSubtitleDTO;
import io.ocepgen.modules.exam.dto.ExamSubtitleQuestionDTO;
import io.ocepgen.modules.exam.entity.ExamSubtitleEntity;
import io.ocepgen.modules.exam.service.ExamSubtitleQuestionService;
import io.ocepgen.modules.exam.service.ExamSubtitleService;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 试卷小标题表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-24
 */
@Service
public class ExamSubtitleServiceImpl extends CrudServiceImpl<ExamSubtitleDao, ExamSubtitleEntity, ExamSubtitleDTO> implements ExamSubtitleService {

    @Resource
    ExamSubtitleQuestionService examSubtitleQuestionService;


    @Override
    public QueryWrapper<ExamSubtitleEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<ExamSubtitleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public List<ExamSubtitleDTO> getListByExamPaperId(Long id) {
        LambdaQueryWrapper<ExamSubtitleEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ExamSubtitleEntity::getExamPaperId, id);
        lqw.orderByAsc(ExamSubtitleEntity::getSort);

        return baseDao.selectList(lqw).stream().map(e -> {
            ExamSubtitleDTO dto = new ExamSubtitleDTO();
            BeanUtil.copyProperties(e, dto);

            // 查询小标题下的题目
            List<QbQuestionDTO> questions = examSubtitleQuestionService.getQuestionListBySubtitleId(e.getId());
            dto.setQuestions(questions);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void save(ExamSubtitleDTO dto) {
        // 先保存小标题，否则dto会没有id，即小标题对象会没有id， mp-plus会自动返回插入记录行的id
        super.save(dto);
        dto.getQuestions().forEach(question -> {
            // 保存小标题与题目关系
            ExamSubtitleQuestionDTO relation = new ExamSubtitleQuestionDTO();
            relation.setExamSubtitleId(dto.getId());
            relation.setQuestionId(question.getId());
            examSubtitleQuestionService.save(relation);
        });

    }

    @Override
    @Transactional
    public void update(ExamSubtitleDTO dto) {
        // 先根据小标题id查询题目id列表  待删除
        List<Long> questionIds = examSubtitleQuestionService
                .getQuestionListBySubtitleId(dto.getId()).stream().map(QbQuestionDTO::getId).collect(Collectors.toList());
        // 遍历小标题下的题目列表
        dto.getQuestions().forEach(
                question -> {
                    // 首先去掉待删除题目id列表中 仍然存在于小标题下的题目id, 如果存在跳出本次循环
                    if (questionIds.contains(question.getId())) {
                        questionIds.remove(question.getId());
                        return; //跳出本次循环
                    }
                    // 如果不存在，那么说明是新增的小标题-题目关系, 新增
                    ExamSubtitleQuestionDTO relation = new ExamSubtitleQuestionDTO();
                    relation.setExamSubtitleId(dto.getId());
                    relation.setQuestionId(question.getId());
                    examSubtitleQuestionService.save(relation);
                }
        );
        if (questionIds.size() > 0) {
            // 删除已经被删除的小标题-题目关系
            examSubtitleQuestionService.deleteBySubtitleIdAndQuestions(dto.getId(), questionIds);
        }
        super.update(dto);
    }

    @Override
    public void delete(Long[] ids) {

        if (ids.length > 0) { //如果有才删除
            // 删除小标题与题目的关联关系
            examSubtitleQuestionService.deleteBySubtitleIds(Arrays.asList(ids));
        }
        super.delete(ids);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void deleteByExamPaperId(Long[] examPaperIds) {

        for (Long examPaperId : examPaperIds){
            //  先删除试卷关联的 小标题与题目 的关联关系
            LambdaQueryWrapper<ExamSubtitleEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ExamSubtitleEntity::getExamPaperId, examPaperId);
            lqw.select(ExamSubtitleEntity::getId);
            baseDao.selectList(lqw);
            // 查询试卷关联的小标题id列表
            List<Long> subtitleIds =
                    baseDao.selectList(lqw).stream().map(ExamSubtitleEntity::getId).collect(Collectors.toList());
            if (subtitleIds.size() > 0) { //如果有才删除
                // 删除小标题与题目的关联关系
                examSubtitleQuestionService.deleteBySubtitleIds(subtitleIds);

                // 再 根据小标题id列表 删除小标题
                super.delete(subtitleIds.toArray(new Long[0]));
            }

        }

    }


}
