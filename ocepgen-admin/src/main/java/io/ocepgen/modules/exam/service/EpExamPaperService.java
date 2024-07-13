package io.ocepgen.modules.exam.service;

import io.ocepgen.common.service.CrudService;
import io.ocepgen.modules.data.vo.AutoAndManualPaperCountVo;
import io.ocepgen.modules.exam.autogenpaper.vo.GenExamPaperRuleVo;
import io.ocepgen.modules.exam.dto.EpExamPaperDTO;
import io.ocepgen.modules.exam.entity.EpExamPaperEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 试卷表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
public interface EpExamPaperService extends CrudService<EpExamPaperEntity, EpExamPaperDTO> {

    /**
     * 智能组卷
     * @param rule 用户设定的组卷参数
     */
    EpExamPaperDTO generateExamPaper(GenExamPaperRuleVo rule);

    /**
     * 导出试卷的word版
     * @param params 导出参数
     * @param response response
     */
    void exportWord(Map<String, Object> params, HttpServletResponse response);

    /**
     * 导出试卷的pdf版
     * @param params 导出参数
     * @param response response
     */
    void exportPDF(Map<String, Object> params, HttpServletResponse response);

    /**
     * 导出试卷的纯答案版word
     * @param response response
     */
    void exportAnswer(Map<String, Object> params, HttpServletResponse response);

    /**
     * 获取自动组卷和手动组卷的试卷数量
     * @return AutoAndManualPaperCountVo
     */
    AutoAndManualPaperCountVo getAutoAndManualPaperCount(Map<String, Object> params);

    /**
     * 根据条件获取试卷数量
     */
    Integer getExamPaperCountByCondition(Map<String, Object> params);
}
