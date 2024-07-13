package io.ocepgen.modules.question.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.ocepgen.common.service.impl.CrudServiceImpl;
import io.ocepgen.modules.question.dao.QbQuestionOptionDao;
import io.ocepgen.modules.question.dto.QbQuestionOptionDTO;
import io.ocepgen.modules.question.entity.QbQuestionOptionEntity;
import io.ocepgen.modules.question.service.QbQuestionOptionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 题库_题目选项+答案表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Service
public class QbQuestionOptionServiceImpl extends CrudServiceImpl<QbQuestionOptionDao, QbQuestionOptionEntity, QbQuestionOptionDTO> implements QbQuestionOptionService {


    @Resource
    private QbQuestionOptionDao qbQuestionOptionDao;

    @Override
    public QueryWrapper<QbQuestionOptionEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<QbQuestionOptionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public List<QbQuestionOptionEntity> getOptionsByQuestionId(Long id) {
        LambdaQueryWrapper<QbQuestionOptionEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(QbQuestionOptionEntity::getQuestionId, id);
        return qbQuestionOptionDao.selectList(lqw);
    }


    @Override
    public void deleteByQuestionId(Long id) {
        LambdaQueryWrapper<QbQuestionOptionEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(QbQuestionOptionEntity::getQuestionId, id);
        qbQuestionOptionDao.delete(lqw);
    }

    @Override
    @SuppressWarnings("unchecked") //忽略这个警告
    public List<Long> getOptionIdsByQuestionId(Long id) {
        LambdaQueryWrapper<QbQuestionOptionEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(QbQuestionOptionEntity::getQuestionId, id)
                .select(QbQuestionOptionEntity::getId); //只查询id  （Unchecked generics array creation for varargs parameter ） 警告是因为在使用Lambda表达式时，Java编译器无法推断出泛型类型

        // 转为List<Long>
        return qbQuestionOptionDao.selectList(lqw)
                .stream()
                .map(QbQuestionOptionEntity::getId)
                .collect(Collectors.toList());
    }
}
