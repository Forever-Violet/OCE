package io.ocepgen.modules.exam.dao;

import io.ocepgen.common.dao.BaseDao;
import io.ocepgen.modules.exam.entity.ExamSubtitleQuestionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试卷小标题-题目关系表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-24
 */
@Mapper
public interface ExamSubtitleQuestionDao extends BaseDao<ExamSubtitleQuestionEntity> {
	
}