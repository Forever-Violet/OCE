package io.ocepgen.modules.course.service;

import io.ocepgen.common.service.CrudService;
import io.ocepgen.modules.course.dto.CourseDTO;
import io.ocepgen.modules.course.entity.CourseEntity;

import java.util.Map;

/**
 * 课程信息表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
public interface CourseService extends CrudService<CourseEntity, CourseDTO> {

    /**
     * 根据条件获取课程数量
     */
    Integer getCourseCountByCondition(Map<String, Object> params);
}
