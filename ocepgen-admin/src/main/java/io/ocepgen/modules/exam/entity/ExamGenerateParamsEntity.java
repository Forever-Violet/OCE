package io.ocepgen.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 组卷参数配置表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-05-10
 */
@Data
@TableName("exam_generate_params")
public class ExamGenerateParamsEntity {

    /**
     * ID
     */
	private Long id;
    /**
     * schoolId
     */
    private Long schoolId;
    /**
     * 配置名称
     */
    private String configName;
    /**
     * 种群大小
     */
	private Integer populationSize;
    /**
     * 最大迭代次数
     */
	private Integer maxIteratorNum;
    /**
     * 变异概率
     */
	private Float mutationRate;
    /**
     * 交叉概率
     */
	private Float crossoverRate;
    /**
     * 期望适应度
     */
	private Float expectedFitness;
    /**
     * 准确率
     */
	private Float accuracy;
    /**
     * 使用次数
     */
	private Integer useTimes;
    /**
     * 组卷配置公开状态，0 公开；1 私有
     */
	private Integer scope;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;
    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Long creator;
}
