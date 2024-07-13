package io.ocepgen.modules.data.vo;

import lombok.Data;

/**
 * @author roxy
 */
@Data
public class CardDataVo {

    // 教师数量
    Integer teacherCount;
    // 试题数量
    Integer questionCount;
    // 考卷数量
    Integer examPaperCount;
    // 课程数量
    Integer courseCount;
}
