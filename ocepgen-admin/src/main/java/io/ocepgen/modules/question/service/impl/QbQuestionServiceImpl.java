package io.ocepgen.modules.question.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.ocepgen.common.constant.Constant;
import io.ocepgen.common.exception.OcepgenException;
import io.ocepgen.common.page.PageData;
import io.ocepgen.common.service.impl.CrudServiceImpl;
import io.ocepgen.modules.exam.utils.ExportWordUtil;
import io.ocepgen.modules.question.dao.QbQuestionDao;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.ocepgen.modules.question.dto.QbQuestionOptionDTO;
import io.ocepgen.modules.question.entity.QbQuestionEntity;
import io.ocepgen.modules.question.enums.QuestionScopeEnum;
import io.ocepgen.modules.question.enums.QuestionTypeEnum;
import io.ocepgen.modules.question.service.QbQuestionOptionService;
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
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 题库_题目表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Service
@Slf4j
public class QbQuestionServiceImpl extends CrudServiceImpl<QbQuestionDao, QbQuestionEntity, QbQuestionDTO> implements QbQuestionService {


    @Resource
    QbQuestionOptionService qbQuestionOptionService;

    @Resource
    SysUserService sysUserService;
    @Resource
    QbQuestionDao qbQuestionDao;

    @Override //摆设
    public QueryWrapper<QbQuestionEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        QueryWrapper<QbQuestionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);
        return wrapper;
    }


    @Override
    public QbQuestionDTO get(Long id) {

        QbQuestionDTO dto = super.get(id);
        // 查询填充选项列表
        List<QbQuestionOptionDTO> options = qbQuestionOptionService.getOptionsByQuestionId(id).stream().map(option -> {
            QbQuestionOptionDTO optionDTO = new QbQuestionOptionDTO();
            BeanUtils.copyProperties(option, optionDTO);
            return optionDTO;
        }).collect(Collectors.toList());
        dto.setOptions(options);

        return dto;
    }


    @Override
    public PageData<QbQuestionDTO> page(Map<String, Object> params) {

        //分页
        IPage<QbQuestionEntity> page = getPage(params, Constant.CREATE_DATE, false);

        return new PageData<>(getList(params), page.getTotal());
    }

    @Override
    public List<QbQuestionDTO> list(Map<String, Object> params) {
        return getList(params);
    }

    @Override // 查询题目列表以及关联的所有选项。下边还有一个getList是用来查分页的，所以它不需要填充选项列表。
    public List<QbQuestionDTO> getListAndOptions(Map<String, Object> params) {
        //普通管理员，只能查询所属学校的用户
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            String schoolId = user.getSchoolId().toString();
            params.put("userId", user.getId());
            params.put("schoolId", schoolId);
        }

        List<QbQuestionEntity> list = baseDao.getList(params);
        List<QbQuestionDTO> dtoList = new ArrayList<>(list.size());

        list.forEach(entity -> {
            // 如果idList == null的话，就是试卷模块来取题目，那么就不需要过滤题目是否是私有的。
            // 如果当前题目是账号私有的，并且创建者又不是当前用户，那么将这条数据过滤掉，也就是跳出本次循环，不添加到dtoList。
            if (params.get("idList") != null && entity.getScope() == 1 && !user.getId().equals(entity.getCreator())) {
                return;
            }

            QbQuestionDTO qbQuestionDTO = new QbQuestionDTO();
            BeanUtils.copyProperties(entity, qbQuestionDTO);

            // 查询填充选项列表
            List<QbQuestionOptionDTO> options =
                    qbQuestionOptionService.getOptionsByQuestionId(entity.getId()).stream().map(option -> {
                QbQuestionOptionDTO optionDTO = new QbQuestionOptionDTO();
                BeanUtils.copyProperties(option, optionDTO);
                return optionDTO;
            }).collect(Collectors.toList());
            qbQuestionDTO.setOptions(options);

            dtoList.add(qbQuestionDTO);
        });
        return dtoList;
    }

    private List<QbQuestionDTO> getList(Map<String, Object> params) {
        //普通管理员，只能查询所属学校的用户
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            String schoolId = user.getSchoolId().toString();
            params.put("userId", user.getId());
            params.put("schoolId", schoolId);
        }

        List<QbQuestionEntity> list = baseDao.getList(params);
        List<QbQuestionDTO> dtoList = new ArrayList<>(list.size());

        list.forEach(entity -> {
            // 如果当前题目是账号私有的，并且创建者又不是当前用户，那么将这条数据过滤掉，也就是跳出本次循环，不添加到dtoList
            if (entity.getScope() == QuestionScopeEnum.PRIVATE.value() && !user.getId().equals(entity.getCreator())) {
                return;
            }

            QbQuestionDTO qbQuestionDTO = new QbQuestionDTO();
            // 设置创建者名称（如果不为空的话）
            if (entity.getCreator() != null) {
                qbQuestionDTO.setCreatorName(sysUserService.getUsernameAndRealNameByUserId(entity.getCreator()));
            }
            BeanUtils.copyProperties(entity, qbQuestionDTO);
            dtoList.add(qbQuestionDTO);
        });
        return dtoList;
    }


    @Override
    @Transactional
    public void update(QbQuestionDTO dto) {
        super.update(dto);
        List<QbQuestionOptionDTO> options = dto.getOptions();
        // 待删除选项id列表
        List<Long> optionIds = qbQuestionOptionService.getOptionIdsByQuestionId(dto.getId());
        options.forEach(option -> {
            if (option.getId() == null) { // 没有ID即为新增选项
                // 设置题目ID
                option.setQuestionId(dto.getId());
                qbQuestionOptionService.save(option);
            } else {
                qbQuestionOptionService.update(option);
            }

            // 如果当前选项id未被删除（即存在于请求发送来的选项列表），那么将其从待删除列表中移除
            optionIds.remove(option.getId());
        });
        if (optionIds.size() > 0) {
            // 如果还有剩，那么剩下的id即为已经被移除的选项，删除掉
            qbQuestionOptionService.delete(optionIds.toArray(new Long[0]));
        }
    }

    @Override
    @Transactional
    public void save(QbQuestionDTO dto) {

        super.save(dto);
        List<QbQuestionOptionDTO> options = dto.getOptions();
        options.forEach(option -> {
            // 填充题目ID
            option.setQuestionId(dto.getId());
            qbQuestionOptionService.save(option);
        });
    }

    @Override
    @Transactional
    public void delete(Long[] ids) {
        for (Long id : ids) {
            // 先删除当前题目的选项
            qbQuestionOptionService.deleteByQuestionId(id);
            qbQuestionDao.deleteById(id);
        }
    }

    @Override
    public void exportQuestionListToWord(Map<String, Object> params, HttpServletResponse response) {
        List<QbQuestionDTO> list = getListAndOptions(params);
        try {
            // 导出word
            ExportWordUtil.exportQuestionDTO("题目列表", list, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OcepgenException("导出题目列表失败");
        }

    }

//    @Override
//    public List<Integer> getQuestionCountByCreateTime(String date) {
//        List<Integer> countList = new ArrayList<>();
//        log.info("date:{}", date);
//        // 将目标日期字符串转换为LocalDate对象
//        LocalDate dateTime = LocalDate.parse(date);
//        // 获取目标日期的起始时间和结束时间
//        LocalDateTime startDateTime = dateTime.atStartOfDay();//2024-04-20 00:00:00
//        LocalDateTime endDateTime = dateTime.plusDays(1).atStartOfDay().minusSeconds(1);//2024-04-20 23:59:59
//        // 遍历题目类型，进行查找
//        for (QuestionTypeEnum type : QuestionTypeEnum.values()) {
//            // 构造查询条件
//            LambdaQueryWrapper<QbQuestionEntity> lqw = new LambdaQueryWrapper<>();
//            lqw.eq(QbQuestionEntity::getType, type.value())
//                    .between(QbQuestionEntity::getCreateDate, startDateTime, endDateTime);
//            long count = baseDao.selectCount(lqw);
//            countList.add((int) count);
//        }
//        return countList;
//    }

    @Override
    public List<Integer> getMonthQuestionCountByCreateDate(Map<String, Object> params) {
        List<Integer> countList = new ArrayList<>();
        // 将目标日期字符串转换为LocalDate对象
        LocalDate formatDate = LocalDate.parse(params.get("date").toString());
        // 获取该月的起始日期和结束日期
        LocalDate startDateOfMonth = formatDate.with(TemporalAdjusters.firstDayOfMonth()); //2024-03-01    00:00:00
        LocalDate endDateOfMonth = formatDate.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1); //2024-04-01   00:00:00


        // 遍历题目类型，进行查找
        for (QuestionTypeEnum type : QuestionTypeEnum.values()) {
            // 构造查询条件
//            LambdaQueryWrapper<QbQuestionEntity> lqw = new LambdaQueryWrapper<>();
//            lqw.eq(QbQuestionEntity::getType, type.value())
//                    .between(QbQuestionEntity::getCreateDate, startDateOfMonth, endDateOfMonth);
            params.put("type", String.valueOf(type.value()));
            params.put("startDate", startDateOfMonth.toString());
            params.put("endDate", endDateOfMonth.toString());
            Integer count = baseDao.getQuestionCountByCondition(params);

            countList.add(count);
        }
        return countList;
    }

    @Override
    public Integer getQuestionCountByCondition(Map<String, Object> params) {

        return baseDao.getQuestionCountByCondition(params);
    }
}
