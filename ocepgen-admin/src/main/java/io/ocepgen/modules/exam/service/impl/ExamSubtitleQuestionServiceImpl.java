package io.ocepgen.modules.exam.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.ocepgen.common.service.impl.CrudServiceImpl;
import io.ocepgen.modules.exam.dao.ExamSubtitleQuestionDao;
import io.ocepgen.modules.exam.dto.ExamSubtitleQuestionDTO;
import io.ocepgen.modules.exam.entity.ExamSubtitleQuestionEntity;
import io.ocepgen.modules.exam.service.ExamSubtitleQuestionService;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.ocepgen.modules.question.service.QbQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 试卷小标题-题目关系表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-24
 */
@Service
public class ExamSubtitleQuestionServiceImpl extends CrudServiceImpl<ExamSubtitleQuestionDao, ExamSubtitleQuestionEntity, ExamSubtitleQuestionDTO> implements ExamSubtitleQuestionService {

    @Resource
    private QbQuestionService qbQuestionService;

    @Override
    public QueryWrapper<ExamSubtitleQuestionEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<ExamSubtitleQuestionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<QbQuestionDTO> getQuestionListBySubtitleId(Long id) {
        //根据小标题id查询题目列表id
        LambdaQueryWrapper<ExamSubtitleQuestionEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ExamSubtitleQuestionEntity::getExamSubtitleId, id)
                .select(ExamSubtitleQuestionEntity::getQuestionId);
        List<ExamSubtitleQuestionEntity> list = baseDao.selectList(lqw);
        List<Long> questionIds = list.stream().map(ExamSubtitleQuestionEntity::getQuestionId).collect(Collectors.toList());
        if (questionIds.size() == 0) {
            return new ArrayList<>();
        }

        // 根据题目列表id查询题目列表
        Map<String, Object> params = new HashMap<>();
        params.put("idList", questionIds);
        return qbQuestionService.getListAndOptions(params);
    }

    @Override
    @Transactional
    public void deleteBySubtitleIds(List<Long> subtitleIds) {
        if (subtitleIds.size() > 0) {
            baseDao.delete(new LambdaQueryWrapper<ExamSubtitleQuestionEntity>()
                    .in(ExamSubtitleQuestionEntity::getExamSubtitleId, subtitleIds));
        }
    }

    @Override
    @Transactional
    public void deleteBySubtitleIdAndQuestions(Long id, List<Long> questionIds) {
        if (questionIds.size() > 0) {
            LambdaQueryWrapper<ExamSubtitleQuestionEntity> lqw = new LambdaQueryWrapper<>();

            lqw.eq(ExamSubtitleQuestionEntity::getExamSubtitleId, id)
                    .in(ExamSubtitleQuestionEntity::getQuestionId, questionIds);
            baseDao.delete(lqw);
        }
    }

//    Spring框架的@Transactional注解提供了事务管理，其中事务传播行为是一个关键特性。当一个事务方法内部调用另一个事务方法时，
//    根据@Transactional注解的propagation属性值，会有不同的处理方式：
//
//    REQUIRED（默认）：如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。
//    REQUIRES_NEW：总是新建一个事务，如果当前存在事务，则将当前事务挂起。
//    SUPPORTS：如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务方式执行。
//    NOT_SUPPORTED：以非事务方式执行，如果当前存在事务，则把当前事务挂起。
//    MANDATORY：如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常。
//    NEVER：以非事务方式执行，如果当前存在事务，则抛出异常。
//    NESTED：如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则与REQUIRED类似。
//
//    所以，如果你在@Transactional的方法A中调用另一个@Transactional的方法B，并且希望方法B能够在一个新的事务中执行，
//    那么需要将方法B的事务传播行为设置为REQUIRES_NEW。否则，如果两个方法都在同一个事务中，那么方法B的异常可能会导致方法A的事务回滚。
}
