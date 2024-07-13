package io.ocepgen.modules.question.dao;

import io.ocepgen.common.dao.BaseDao;
import io.ocepgen.modules.question.entity.QbQuestionOptionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题库_题目选项+答案表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Mapper
public interface QbQuestionOptionDao extends BaseDao<QbQuestionOptionEntity> {
	
}