package io.ocepgen.modules.question.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题库_题目选项+答案表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
@TableName("qb_question_option")
public class QbQuestionOptionEntity implements Serializable {

    /**
     * 题目选项ID
     */
    //@TableId(value="id" ,type= IdType.AUTO) //配置文件里配置了雪花算法id，这里用注解不管用
	private Long id;
    /**
     * 对于选择题此字段即为选项内容，对于填空、判断、填空、主观题此字段为空
     */
	private String content;
    /**
     * 对于选择题此字段不为空（"1"）即为答案，对于填空、判断（正确/错误）、填空、主观题此字段内容即为选项答案
     */
	private String answer;
    /**
     * 题目ID
     */
	private Long questionId;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
	private Date createDate;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateDate;
}
