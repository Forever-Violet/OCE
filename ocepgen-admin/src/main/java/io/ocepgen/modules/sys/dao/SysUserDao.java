/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.ocepgen.modules.sys.dao;

import io.ocepgen.common.dao.BaseDao;
import io.ocepgen.modules.sys.dto.SysUserDTO;
import io.ocepgen.modules.sys.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 系统用户
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface SysUserDao extends BaseDao<SysUserEntity> {

	List<SysUserEntity> getList(Map<String, Object> params);

	SysUserEntity getById(Long id);

	SysUserEntity getByUsername(String username);

	int updatePassword(@Param("id") Long id, @Param("newPassword") String newPassword);

	/**
	 * 根据学校ID，查询用户数
	 */
	int getCountBySchoolId(Long schoolId);

	/**
	 * 根据学校ID,查询用户ID列表
	 */
	List<Long> getUserIdListBySchoolId(List<Long> schoolIdList);

	/**
	 * 根据年级id 查询用户学号（用户名）列表
	 */
	List<Long> getStudentNoListByGradeId(Long gradeId);

	/**
	 * 根据班级id，查询学生学号（用户名）列表
	 */
	List<Long> getStudentNoListByClassId(Long classId);
	/**
	 * 获取学生列表
	 */
    List<SysUserDTO> getStudentList(Map<String, Object> params);

	/**
	 * 获取教师列表
	 */
	List<SysUserDTO> getTeacherList(Map<String, Object> params);

	/**
	 * 获取用户数量
	 */
    Integer getCountByCondition(Map<String, Object> params);
}
