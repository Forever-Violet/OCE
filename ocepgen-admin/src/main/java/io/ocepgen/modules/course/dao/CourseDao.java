package io.ocepgen.modules.course.dao;

import io.ocepgen.common.dao.BaseDao;
import io.ocepgen.modules.course.entity.CourseEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 课程信息表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Mapper
public interface CourseDao extends BaseDao<CourseEntity> {

    /**
     * 获取班级列表
     * @param params
     * @return
     */
    List<CourseEntity> getList(Map<String, Object> params);

    Integer getCourseCountByCondition(Map<String, Object> params);
}
