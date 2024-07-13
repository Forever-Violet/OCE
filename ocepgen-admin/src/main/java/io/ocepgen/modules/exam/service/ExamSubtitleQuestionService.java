package io.ocepgen.modules.exam.service;

import io.ocepgen.common.service.CrudService;
import io.ocepgen.modules.exam.dto.ExamSubtitleDTO;
import io.ocepgen.modules.exam.dto.ExamSubtitleQuestionDTO;
import io.ocepgen.modules.exam.entity.ExamSubtitleQuestionEntity;
import io.ocepgen.modules.question.dto.QbQuestionDTO;

import java.util.List;

/**
 * 试卷小标题-题目关系表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-24
 */
public interface ExamSubtitleQuestionService extends CrudService<ExamSubtitleQuestionEntity, ExamSubtitleQuestionDTO> {

    /**
     * 根据小标题id查询关联的题目列表
     * @param id 小标题id
     * @return 题目列表
     */
    List<QbQuestionDTO> getQuestionListBySubtitleId(Long id);

    /**
     * 根据小标题id列表删除与题目的关联关系
     * @param subtitleIds 小标题ids
     */
    void deleteBySubtitleIds(List<Long> subtitleIds);

    /**
     * 根据小标题id和题目id列表删除与题目的关联关系
     * @param id 小标题id
     * @param questionIds 题目id列表
     */
    void deleteBySubtitleIdAndQuestions(Long id, List<Long> questionIds);
}
