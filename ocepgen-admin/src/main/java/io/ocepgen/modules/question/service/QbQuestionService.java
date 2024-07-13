package io.ocepgen.modules.question.service;

import io.ocepgen.common.service.CrudService;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.ocepgen.modules.question.entity.QbQuestionEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 题库_题目表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
public interface QbQuestionService extends CrudService<QbQuestionEntity, QbQuestionDTO> {
    List<QbQuestionDTO> getListAndOptions(Map<String, Object> params);

    /**
     * 导出题目列表至Word文档
     * @param params 导出条件
     * @param response res
     */
    void exportQuestionListToWord(Map<String, Object> params, HttpServletResponse response);

    /**
     * 根据创建日期查询题数
     * @param date 创建日期
     * @return 题数，按类型分类
     */
//    List<Integer> getQuestionCountByCreateTime(String date);
    /**
     * 根据创建月份查询题数
     * @param params 创建日期、学校id
     * @return 题数，按类型分类
     */
    List<Integer> getMonthQuestionCountByCreateDate(Map<String, Object> params);

    /**
     * 条件查询题目数量
     */
    Integer getQuestionCountByCondition(Map<String, Object> params);
}
