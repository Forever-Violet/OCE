package io.ocepgen.modules.exam.dao;

import io.ocepgen.common.dao.BaseDao;
import io.ocepgen.modules.exam.entity.ExamGenerateParamsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 组卷参数配置表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-05-10
 */
@Mapper
public interface ExamGenerateParamsDao extends BaseDao<ExamGenerateParamsEntity> {

    List<ExamGenerateParamsEntity> getList(Map<String, Object> params);
}
