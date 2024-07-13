package io.ocepgen.modules.course.service;

import io.ocepgen.common.service.CrudService;
import io.ocepgen.modules.course.dto.CourseTeacherDTO;
import io.ocepgen.modules.exam.entity.CourseTeacherEntity;

import java.util.List;

/**
 * 课程-教师关系表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-04-09
 */
public interface CourseTeacherService extends CrudService<CourseTeacherEntity, CourseTeacherDTO> {

    /**
     * 根据课程id查询关系列表
     */
    List<CourseTeacherEntity> getByCourseId(Long id);

    void deleteByCourseId(Long id);
}
