package io.ocepgen.modules.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

/**
 * 题库_题目表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
public class QbQuestionExcel {
    @ExcelProperty(value = "题目ID")
    private Long id;
    @ExcelProperty(value = "题目内容，限制2000字")
    private String content;
    @ExcelProperty(value = "题目类型   0：单选   1：多选   2：判断   3：填空   4：主观")
    private Integer type;
    @ExcelProperty(value = "题目解析，限制8000字")
    private String analysis;
    @ExcelProperty(value = "题目难度   1 ~ 5  ")
    private Integer difficulty;
    @ExcelProperty(value = "题目适用范围   0：学校公共题库   1：账号私有题库")
    private Integer scope;
    @ExcelProperty(value = "题目分值")
    private Float score;
    @ExcelProperty(value = "课程ID")
    private Long courseId;
    @ExcelProperty(value = "创建者")
    private Long creator;
    @ExcelProperty(value = "创建时间")
    private Date createDate;
    @ExcelProperty(value = "更新时间")
    private Date updateDate;

}