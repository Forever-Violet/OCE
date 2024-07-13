package io.ocepgen.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 试卷小标题表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-24
 */
@Data
@TableName("exam_subtitle")
public class ExamSubtitleEntity {

    /**
     * ID
     */
	private Long id;
    /**
     * 小标题名称
     */
	private String examSubtitleName;
    /**
     * 试卷ID
     */
	private Long examPaperId;
    /**
     * 标题排序
     */
	private Integer sort;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
	private Date createDate;
}
