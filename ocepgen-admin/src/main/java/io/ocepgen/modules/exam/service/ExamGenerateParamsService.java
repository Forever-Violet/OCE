package io.ocepgen.modules.exam.service;

import io.ocepgen.common.service.CrudService;
import io.ocepgen.modules.exam.dto.ExamGenerateParamsDTO;
import io.ocepgen.modules.exam.entity.ExamGenerateParamsEntity;

import java.util.List;
import java.util.Map;

/**
 * 组卷参数配置表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-05-10
 */
public interface ExamGenerateParamsService extends CrudService<ExamGenerateParamsEntity, ExamGenerateParamsDTO> {

    /**
     * 获取组卷配置排行榜 取前十
     */
    List<ExamGenerateParamsDTO> getRankingList(Map<String, Object> params);
}
