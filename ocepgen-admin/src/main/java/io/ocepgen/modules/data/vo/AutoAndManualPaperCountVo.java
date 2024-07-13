package io.ocepgen.modules.data.vo;

import lombok.Data;

/**
 * @author roxy
 */
@Data
public class AutoAndManualPaperCountVo {

    // 智能组卷数量
    Integer autoPaperCount;
    // 手动组卷数量
    Integer manualPaperCount;
}
