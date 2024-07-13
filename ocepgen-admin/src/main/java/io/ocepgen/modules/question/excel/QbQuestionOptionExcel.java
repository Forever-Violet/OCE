package io.ocepgen.modules.question.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

/**
 * 题库_题目选项+答案表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
public class QbQuestionOptionExcel {
    @ExcelProperty(value = "题目选项ID")
    private Long id;
    @ExcelProperty(value = "对于选择题此字段即为选项内容，对于填空、判断、填空、主观题此字段为空")
    private String content;
    @ExcelProperty(value = "对于选择题此字段不为空，默认1即为答案，对于填空、判断（正确/错误）、填空、主观题此字段内容即为选项答案")
    private String answer;
    @ExcelProperty(value = "题目ID")
    private Long questionId;
    @ExcelProperty(value = "创建时间")
    private Date createDate;
    @ExcelProperty(value = "更新时间")
    private Date updateDate;

}
