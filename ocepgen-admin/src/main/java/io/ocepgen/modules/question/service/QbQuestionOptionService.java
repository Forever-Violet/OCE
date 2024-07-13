package io.ocepgen.modules.question.service;

import io.ocepgen.common.service.CrudService;
import io.ocepgen.modules.question.dto.QbQuestionOptionDTO;
import io.ocepgen.modules.question.entity.QbQuestionOptionEntity;

import java.util.List;

/**
 * 题库_题目选项+答案表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
public interface QbQuestionOptionService extends CrudService<QbQuestionOptionEntity, QbQuestionOptionDTO> {

    /**
     * 根据题目id获取关联选项列表
     * @param id
     * @return
     */
    List<QbQuestionOptionEntity> getOptionsByQuestionId(Long id);

    /**
     * 根据题目id获取关联选项id列表
     * @param id
     * @return
     */
    List<Long> getOptionIdsByQuestionId(Long id);

    /**
     * 根据题目id删除关联选项
     * @param id
     */
    void deleteByQuestionId(Long id);
}
