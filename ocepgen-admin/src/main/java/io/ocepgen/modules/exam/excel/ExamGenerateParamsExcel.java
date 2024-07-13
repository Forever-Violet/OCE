package io.ocepgen.modules.exam.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 组卷参数配置表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-05-10
 */
@Data
public class ExamGenerateParamsExcel {

    @ExcelProperty(value = "种群大小")
    private Integer populationSize;
    @ExcelProperty(value = "配置名称")
    private String configName;
    @ExcelProperty(value = "最大迭代次数")
    private Integer maxIteratorNum;
    @ExcelProperty(value = "变异概率")
    private Float mutationRate;
    @ExcelProperty(value = "交叉概率")
    private Float crossoverRate;
    @ExcelProperty(value = "期望适应度")
    private Float expectedFitness;
    @ExcelProperty(value = "准确率")
    private Float accuracy;
    @ExcelProperty(value = "使用次数")
    private Integer useTimes;
//    @ExcelProperty(value = "组卷配置公开状态")
//    private Integer scope;
//    @ExcelProperty(value = "创建时间")
//    private Date createDate;
//    @ExcelProperty(value = "创建者")
//    private Long creator;

}
