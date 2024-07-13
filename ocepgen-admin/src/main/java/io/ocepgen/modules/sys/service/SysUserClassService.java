package io.ocepgen.modules.sys.service;

import io.ocepgen.common.service.BaseService;
import io.ocepgen.common.service.CrudService;
import io.ocepgen.modules.sys.dto.SysUserClassDTO;
import io.ocepgen.modules.sys.entity.SysUserClassEntity;

import java.util.List;

/**
 * 班级管理
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2023-11-29
 */
public interface SysUserClassService extends BaseService<SysUserClassEntity> {

    void saveOrUpdate(Long userId, List<Long> classIdList);

    void deleteByClassIds(Long[] classIds);

    void deleteByUserIds(Long[] userIds);

    List<Long> getClassIdList(Long userId);

    List<String> getClassNameList(List<Long> classIdList);
}
