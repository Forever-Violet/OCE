package io.ocepgen.modules.course.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 课程信息表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
@ApiModel(value = "课程信息表")
public class CourseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "课程ID")
	private Long id;

	@ApiModelProperty(value = "课程名称")
	private String courseName;

	@ApiModelProperty(value = "年级ID")
	private Long gradeId;

	@ApiModelProperty(value = "课程介绍")
	private String remark;

	@ApiModelProperty(value = "课程总课时")
	private Integer hours;

	@ApiModelProperty(value = "状态 0正常 1结课")
	private Integer status;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "年级名称 （额外字段）")
	private String gradeName;

	@ApiModelProperty(value = "创建者名称，额外字段")
	private String creatorName;

	@ApiModelProperty(value = "教师列表，额外字段")
	private List<Long> teacherIdList;

	@ApiModelProperty(value = "教师名称列表，额外字段")
	private List<String> teacherNameList;


}
