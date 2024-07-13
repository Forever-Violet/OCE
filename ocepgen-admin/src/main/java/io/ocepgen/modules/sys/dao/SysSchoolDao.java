package io.ocepgen.modules.sys.dao;

import io.ocepgen.common.dao.BaseDao;

import io.ocepgen.modules.sys.entity.SysSchoolEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学校管理
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-10-28
 */
@Mapper
public interface SysSchoolDao extends BaseDao<SysSchoolEntity> {


    SysSchoolEntity getById(Long id);


}
