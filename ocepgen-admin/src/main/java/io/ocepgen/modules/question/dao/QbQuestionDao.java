package io.ocepgen.modules.question.dao;

import io.ocepgen.common.dao.BaseDao;
import io.ocepgen.modules.question.entity.QbQuestionEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 题库_题目表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Mapper
public interface QbQuestionDao extends BaseDao<QbQuestionEntity> {

    List<QbQuestionEntity> getList(Map<String, Object> params);

    Integer getQuestionCountByCondition(Map<String, Object> params);

}
