package io.ocepgen.modules.sys.dao;

import io.ocepgen.common.dao.BaseDao;
import io.ocepgen.modules.sys.entity.SysUserClassEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 班级管理
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2023-11-29
 */
@Mapper
public interface SysUserClassDao extends BaseDao<SysUserClassEntity> {

    void deleteByClassIds(Long[] classIds);

    void deleteByUserIds(Long[] userIds);

    List<Long> getClassIdList(Long userId);

    String getClassNameById(Long classId);

    String getGradeNameById(Long classId);
}
