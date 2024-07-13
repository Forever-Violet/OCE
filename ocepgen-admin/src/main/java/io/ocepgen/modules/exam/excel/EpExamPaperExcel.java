package io.ocepgen.modules.exam.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 试卷表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
public class EpExamPaperExcel {
//    @ExcelProperty(value = "试卷ID")
//    private Long id;
    @ExcelProperty(value = "试卷标题")
    private String title;
//    @ExcelProperty(value = "课程ID")
//    private Long courseId;
    @ExcelProperty(value = "试卷类型")
    private Integer type;
    @ExcelProperty(value = "试卷总分")
    private Float score;
    @ExcelProperty(value = "题目数量")
    private Float questionCount;
    @ExcelProperty(value = "试卷介绍")
    private String remark;
//    @ExcelProperty(value = "创建者")
//    private Long creator;
//    @ExcelProperty(value = "创建时间")
//    private Date createDate;

}
