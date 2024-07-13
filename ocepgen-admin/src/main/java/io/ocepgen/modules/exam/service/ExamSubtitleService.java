package io.ocepgen.modules.exam.service;

import io.ocepgen.common.service.CrudService;
import io.ocepgen.modules.exam.dto.ExamSubtitleDTO;
import io.ocepgen.modules.exam.entity.ExamSubtitleEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 试卷小标题表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-24
 */
public interface ExamSubtitleService extends CrudService<ExamSubtitleEntity, ExamSubtitleDTO> {

    /**
     * 根据试卷id获取试卷小标题（包含小标题下所属题目列表）列表
     * @param id id
     * @return List<ExamSubtitleDTO>
     */
    List<ExamSubtitleDTO> getListByExamPaperId(Long id);

    /**
     * 根据试卷id列表删除试卷小标题列表
     * @param examPaperIds 试卷id列表
     */
    @Transactional
    void deleteByExamPaperId(Long[] examPaperIds);
}
