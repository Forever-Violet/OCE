package io.ocepgen.modules.sys.service;

import io.ocepgen.common.service.CrudService;
import io.ocepgen.modules.sys.dto.SysSchoolSemesterDTO;
import io.ocepgen.modules.sys.entity.SysSchoolSemesterEntity;

/**
 * 学期管理
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2023-12-01
 */
public interface SysSchoolSemesterService extends CrudService<SysSchoolSemesterEntity, SysSchoolSemesterDTO> {

    /**
     * 根据学校id获取最新学期id
     */
    Long getLatestSemesterId(Long schoolId);
}
