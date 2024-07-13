package io.ocepgen.modules.sys.service;

import io.ocepgen.common.service.CrudService;
import io.ocepgen.modules.sys.dto.SysSchoolDTO;
import io.ocepgen.modules.sys.entity.SysSchoolEntity;

/**
 * 学校管理
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-10-28
 */
public interface SysSchoolService extends CrudService<SysSchoolEntity, SysSchoolDTO> {

    SysSchoolDTO get(Long id);

    /**
     * 根据学校ID获取学校名称
     * @return schoolName
     */
    String getSchoolNameById(Long schoolId);

    void update(SysSchoolDTO dto);

}
