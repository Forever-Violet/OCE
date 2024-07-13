package io.ocepgen.modules.course.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 课程信息表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
@TableName("course")
public class CourseEntity {

    /**
     * 课程ID
     */
	private Long id;
    /**
     * 课程名称
     */
	private String courseName;
    /**
     * 年级ID
     */
	private Long gradeId;
    /**
     * 课程介绍
     */
	private String remark;
    /**
     * 课程总课时
     */
	private Integer hours;
    /**
     * 状态 0正常 1结课
     */
	private Integer status;
    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
	private Long creator;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
	private Date createDate;

    /**
     * 年级名称 （额外字段）
     */
    @TableField(exist = false)
    private String gradeName;
}
