package io.ocepgen.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 试卷表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
@TableName("ep_exam_paper")
public class EpExamPaperEntity {

    /**
     * 试卷ID
     */
	private Long id;
    /**
     * 试卷标题
     */
	private String title;
    /**
     * 课程ID
     */
	private Long courseId;
    /**
     * 试卷类型，0 手动组卷；1 智能组卷
     */
	private Integer type;
    /**
     * 试卷总分
     */
	private Float score;
    /**
     * 题目数量
     */
	private Integer questionCount;

    /**
     * 题目平均难度
     */
    private Float difficulty;
    /**
     * 试卷介绍
     */
	private String remark;
    /**
     * 试卷适用范围 0公共 1私有
     */
    private Integer scope;
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
     * 课程名称
     */
    @TableField(exist = false)
    private String courseName;
}
