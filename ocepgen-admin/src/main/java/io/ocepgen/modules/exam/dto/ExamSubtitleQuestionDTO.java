package io.ocepgen.modules.exam.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 试卷小标题-题目关系表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-24
 */
@Data
@ApiModel(value = "试卷小标题-题目关系表")
public class ExamSubtitleQuestionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	private Long id;

	@ApiModelProperty(value = "小标题ID")
	private Long examSubtitleId;

	@ApiModelProperty(value = "题目ID")
	private Long questionId;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;


}
