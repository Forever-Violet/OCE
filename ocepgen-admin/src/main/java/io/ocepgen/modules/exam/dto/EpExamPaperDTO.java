package io.ocepgen.modules.exam.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 试卷表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
@ApiModel(value = "试卷表")
public class EpExamPaperDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "试卷ID")
	private Long id;

	@ApiModelProperty(value = "试卷标题")
	private String title;

	@ApiModelProperty(value = "课程ID")
	private Long courseId;

	@ApiModelProperty(value = "试卷类型，0 手动组卷；1 智能组卷")
	private Integer type;

	@ApiModelProperty(value = "试卷总分")
	private Float score;

	@ApiModelProperty(value = "题目平均难度")
	private Float difficulty;

	@ApiModelProperty(value = "题目数量")
	private Integer questionCount;

	@ApiModelProperty(value = "试卷介绍")
	private String remark;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "创建者名称，额外字段")
	private String creatorName;

	@ApiModelProperty(value = "试卷适用范围 0公共 1私有")
	private Integer scope;

	@ApiModelProperty(value = "小标题列表， 额外信息")
	private List<ExamSubtitleDTO> subtitles;

	@ApiModelProperty(value = "课程名称，额外信息")
	private String courseName;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("title", this.title);
		map.put("id", this.id);
		map.put("remark", this.remark);
		map.put("score", this.score);
		map.put("difficulty", this.difficulty);
		map.put("questionCount", this.questionCount);
		map.put("courseName", this.courseName);
		map.put("subtitles", this.subtitles != null ? this.subtitles.stream().map(ExamSubtitleDTO::toMap).collect(Collectors.toList()) : null);
		return map;
	}

}
