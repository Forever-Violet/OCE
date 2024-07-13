package io.ocepgen.modules.course.dao;

import io.ocepgen.common.dao.BaseDao;
import io.ocepgen.modules.exam.entity.CourseTeacherEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程-教师关系表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-04-09
 */
@Mapper
public interface CourseTeacherDao extends BaseDao<CourseTeacherEntity> {

}
