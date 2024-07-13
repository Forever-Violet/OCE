package io.ocepgen.modules.question.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.ocepgen.common.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 题库_题目表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
@TableName("qb_question")
public class QbQuestionEntity extends BaseEntity {

    /**
     * 题目内容，限制2000字
     */
	private String content;
    /**
     * 题目类型   0：单选   1：多选   2：判断   3：填空   4：主观
     */
	private Integer type;
    /**
     * 题目解析，限制8000字
     */
	private String analysis;
    /**
     * 题目难度   1 ~ 5
     */
	private Integer difficulty;
    /**
     * 题目适用范围   0：学校公共题库   1：账号私有题库
     */
	private Integer scope;
    /**
     * 题目分值
     */
	private Float score;
    /**
     * 课程ID
     */
	private Long courseId;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateDate;

    /**
     * 课程名称，额外字段
     */
    @TableField(exist = false)
    private String courseName;
}
