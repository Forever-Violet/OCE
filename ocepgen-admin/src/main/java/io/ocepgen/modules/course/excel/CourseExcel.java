package io.ocepgen.modules.course.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

/**
 * 课程信息表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Data
public class CourseExcel {
    @ExcelProperty(value = "课程ID")
    private Long id;
    @ExcelProperty(value = "课程名称")
    private String courseName;
    @ExcelProperty(value = "年级ID")
    private Long gradeId;
    @ExcelProperty(value = "课程介绍")
    private String remark;
    @ExcelProperty(value = "状态 0正常 1结课")
    private Integer status;
    @ExcelProperty(value = "创建者")
    private Long creator;
    @ExcelProperty(value = "创建时间")
    private Date createDate;

}