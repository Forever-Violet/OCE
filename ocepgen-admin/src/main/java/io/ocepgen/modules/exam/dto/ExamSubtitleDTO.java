package io.ocepgen.modules.exam.dto;

import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 试卷小标题表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-24
 */
@Data
@ApiModel(value = "试卷小标题表")
public class ExamSubtitleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	private Long id;

	@ApiModelProperty(value = "小标题名称")
	private String examSubtitleName;

	@ApiModelProperty(value = "试卷ID")
	private Long examPaperId;

	@ApiModelProperty(value = "标题排序")
	private Integer sort;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "题目列表 额外信息")
	private List<QbQuestionDTO> questions;


	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("examSubtitleName", this.examSubtitleName);
		map.put("sort", this.sort);
		map.put("questions", this.questions != null ? this.questions.stream().map(QbQuestionDTO::toMap).collect(Collectors.toList()) : Collections.emptyList());
		return map;
	}
}
