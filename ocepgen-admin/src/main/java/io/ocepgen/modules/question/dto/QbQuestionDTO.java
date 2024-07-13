package io.ocepgen.modules.question.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 题库_题目表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
@ApiModel(value = "题库_题目表")
public class QbQuestionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "题目ID")
	private Long id;

	@ApiModelProperty(value = "题目内容，限制20000字")
	private String content;

	@ApiModelProperty(value = "题目类型   0：单选   1：多选   2：判断   3：填空   4：主观")
	private Integer type;

	@ApiModelProperty(value = "题目解析，限制8000字")
	private String analysis;

	@ApiModelProperty(value = "题目难度   1 ~ 5  ")
	private Integer difficulty;

	@ApiModelProperty(value = "题目适用范围   0：学校公共题库   1：账号私有题库")
	private Integer scope;

	@ApiModelProperty(value = "题目分值")
	private Float score;

	@ApiModelProperty(value = "课程ID")
	private Long courseId;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "更新时间")
	private Date updateDate;

	@ApiModelProperty(value = "选项列表")
	private List<QbQuestionOptionDTO> options;

	@ApiModelProperty(value = "创建者名称，额外字段")
	private String creatorName;

	@ApiModelProperty(value = "课程名称 ， 额外字段")
	private String courseName;

	@ApiModelProperty(value = "当前题目在试卷中的序号，额外字段")
	private Integer globalQuestionIndex;

	@ApiModelProperty(value = "题目答案 ， 额外字段，导出试卷时用到")
	private String answer;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("content", this.content);
		map.put("type", this.type);
		map.put("answer", this.answer);
		map.put("difficulty", this.difficulty);
		map.put("analysis", this.analysis);
		map.put("score", this.score);
		map.put("globalQuestionIndex", this.globalQuestionIndex);

		// 确保options不为null
		map.put("options", this.options != null ? this.options.stream().map(QbQuestionOptionDTO::toMap).collect(Collectors.toList()) : Collections.emptyList());
		return map;
	}
}
