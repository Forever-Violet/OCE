package io.ocepgen.modules.question.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 题库_题目选项+答案表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
@ApiModel(value = "题库_题目选项+答案表")
public class QbQuestionOptionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "题目选项ID")
	private Long id;

	@ApiModelProperty(value = "对于选择题此字段即为选项内容，对于填空、判断、填空、主观题此字段为空")
	private String content;

	@ApiModelProperty(value = "对于选择题此字段不为空，默认true即为答案，对于填空、判断（正确/错误）、主观题此字段内容即为选项答案")
	private String answer;

	@ApiModelProperty(value = "题目ID")
	private Long questionId;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "更新时间")
	private Date updateDate;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("content", this.content);
		map.put("answer", this.answer);
		return map;
	}
}
