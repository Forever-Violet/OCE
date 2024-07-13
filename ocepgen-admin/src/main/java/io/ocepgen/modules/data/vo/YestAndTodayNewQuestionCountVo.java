package io.ocepgen.modules.data.vo;

import lombok.Data;

import java.util.List;

/**
 * @author roxy
 */
@Data
public class YestAndTodayNewQuestionCountVo {

    // 昨日新增题目数量
    List<Integer> yestNewQuestionCount;
    // 今日新增题目数量
    List<Integer> todayNewQuestionCount;
}
