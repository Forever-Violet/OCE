package io.ocepgen.modules.data.service;

import io.ocepgen.modules.data.vo.AutoAndManualPaperCountVo;
import io.ocepgen.modules.data.vo.CardDataVo;
import io.ocepgen.modules.data.vo.LastAndNowMonthQuestionCountVo;

/**
 * @author roxy
 */
public interface DataDisplayService {
//    YestAndTodayNewQuestionCountVo getYestAndTodayNewQuestionCount();

    LastAndNowMonthQuestionCountVo getLastAndNowMonthQuestionCount();

    /**
     * 自动和手动组卷数量
     * @return AutoAndManualPaperCountVo
     */
    AutoAndManualPaperCountVo getAutoAndManualPaperCount();

    /**
     * 教师、试题、试卷、课程数量
     * @return CardDataVo
     */
    CardDataVo getCardData();
}
