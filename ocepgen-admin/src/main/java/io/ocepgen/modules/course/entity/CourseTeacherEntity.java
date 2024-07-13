package io.ocepgen.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 课程-教师关系表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-04-09
 */
@Data
@TableName("course_teacher")
public class CourseTeacherEntity {

    /**
     * ID
     */
	private Long id;
    /**
     * 课程ID
     */
	private Long courseId;
    /**
     * 教师ID
     */
	private Long teacherId;
}