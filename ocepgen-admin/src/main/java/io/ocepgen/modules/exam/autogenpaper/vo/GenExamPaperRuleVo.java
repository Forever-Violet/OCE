package io.ocepgen.modules.exam.autogenpaper.vo;

import io.ocepgen.common.validator.group.DefaultGroup;
import io.ocepgen.modules.question.enums.QuestionTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 用户设定的 智能组卷参数 vo
 * @author Roxy
 */
@Data
@ApiModel(value = "用户设定的 智能组卷参数 vo")
public class GenExamPaperRuleVo {

    @ApiModelProperty(value = "题目类型权重")
    private Map<QuestionTypeEnum, Float> questionTypeWeightMap;

    @Max(value = 5,message = "期望难度范围为：0-5", groups = DefaultGroup.class)
    @ApiModelProperty(value = "期望难度")
    private Float expectedDifficulty;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    @NotNull(message = "期望题数不能为空", groups = DefaultGroup.class)
    @ApiModelProperty(value = "期望题数")
    private Integer expectedQuestionCount;

    @ApiModelProperty(value = "组卷配置ID")
    private Long configId;



}
