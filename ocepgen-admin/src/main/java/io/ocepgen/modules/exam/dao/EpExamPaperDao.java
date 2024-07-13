package io.ocepgen.modules.exam.dao;

import io.ocepgen.common.dao.BaseDao;
import io.ocepgen.modules.exam.entity.EpExamPaperEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 试卷表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Mapper
public interface EpExamPaperDao extends BaseDao<EpExamPaperEntity> {

    List<EpExamPaperEntity> getList(Map<String, Object> params);

    /**
     * 获取试卷数量
     * @param params 参数
     * @return Integer
     */
    Integer getExamPaperCountByCondition(Map<String, Object> params);
}
