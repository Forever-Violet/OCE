package io.ocepgen.modules.course.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 课程-教师关系表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-04-09
 */
@Data
@ApiModel(value = "课程-教师关系表")
public class CourseTeacherDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	private Long id;

	@ApiModelProperty(value = "课程ID")
	private Long courseId;

	@ApiModelProperty(value = "教师ID")
	private Long teacherId;


}
