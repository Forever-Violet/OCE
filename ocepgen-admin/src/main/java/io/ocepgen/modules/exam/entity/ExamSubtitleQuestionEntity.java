package io.ocepgen.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 试卷小标题-题目关系表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-24
 */
@Data
@TableName("exam_subtitle_question")
public class ExamSubtitleQuestionEntity {

    /**
     * ID
     */
	private Long id;
    /**
     * 小标题ID
     */
	private Long examSubtitleId;
    /**
     * 题目ID
     */
	private Long questionId;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
	private Date createDate;
}
