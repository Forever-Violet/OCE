package io.ocepgen.modules.data.vo;

import lombok.Data;

import java.util.List;

/**
 * @author roxy
 */
@Data
public class LastAndNowMonthQuestionCountVo {

    // 上月新增题目数量
    List<Integer> lastMonthNewQuestionCount;
    // 今日新增题目数量
    List<Integer> nowMonthNewQuestionCount;
}
