package io.ocepgen.modules.exam.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 组卷参数配置表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-05-10
 */
@Data
@ApiModel(value = "组卷参数配置表")
public class ExamGenerateParamsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	private Long id;

	@ApiModelProperty(value = "学校ID")
	private Long schoolId;

	@ApiModelProperty(value = "配置名称")
	private String configName;

	@ApiModelProperty(value = "种群大小")
	private Integer populationSize;

	@ApiModelProperty(value = "最大迭代次数")
	private Integer maxIteratorNum;

	@ApiModelProperty(value = "变异概率")
	private Float mutationRate;

	@ApiModelProperty(value = "交叉概率")
	private Float crossoverRate;

	@ApiModelProperty(value = "期望适应度")
	private Float expectedFitness;

	@ApiModelProperty(value = "准确率")
	private Float accuracy;

	@ApiModelProperty(value = "使用次数")
	private Integer useTimes;

	@ApiModelProperty(value = "组卷配置公开状态，0 公开；1 私有")
	private Integer scope;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "创建者名称，额外字段")
	private String creatorName;


}
