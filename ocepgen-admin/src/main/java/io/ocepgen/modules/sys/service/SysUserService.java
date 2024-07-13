/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.ocepgen.modules.sys.service;

import io.ocepgen.common.page.PageData;
import io.ocepgen.common.service.BaseService;
import io.ocepgen.modules.sys.dto.SysUserDTO;
import io.ocepgen.modules.sys.entity.SysUserEntity;

import java.util.List;
import java.util.Map;


/**
 * 系统用户
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysUserService extends BaseService<SysUserEntity> {

	PageData<SysUserDTO> page(Map<String, Object> params);

	List<SysUserDTO> list(Map<String, Object> params);

	SysUserDTO get(Long id);

	SysUserDTO getByUsername(String username);

	void save(SysUserDTO dto);

	void update(SysUserDTO dto);

	void delete(Long[] ids);

	/**
	 * 根据用户id获取用户名和真实姓名
	 * @param userId 用户id
	 */
	String getUsernameAndRealNameByUserId(Long userId);

	/**
	 * 修改密码
	 * @param id           用户ID
	 * @param newPassword  新密码
	 */
	void updatePassword(Long id, String newPassword);

	/**
	 * 根据学校ID，查询用户数
	 */
	int getCountBySchoolId(Long schoolId);

	/**
	 * 根据学校ID,查询用户Id列表
	 */
	List<Long> getUserIdListBySchoolId(List<Long> schoolIdList);

	/**
	 * 获取学生列表，通过联动角色、用户角色表，且用户只能查询其学校的学生
	 */
	List<SysUserDTO> getStudentList(Map<String, Object> params);

	/**
	 * 获取教师列表，通过联动角色、用户角色表，且用户只能查询其学校的教师
	 */
	List<SysUserDTO> getTeacherList(Map<String, Object> params);

	/**
	 * 根据用户idList查询用户列表
	 */
	List<SysUserDTO> getListByIds(List<Long> idList);

	/**
	 * 根据条件查询教师数量
	 * @param params 条件map
	 * @return Integer
	 */
    Integer getTeacherCountByCondition(Map<String, Object> params);

	/**
	 * 根据用户名和学校id查询用户
	 * @param username 用户名
	 * @param schoolId 学校id
	 * @return
	 */
	SysUserDTO getByUsernameAndSchool(String username, Long schoolId);

	/**
	 * 根据邮箱查询用户
	 * @param email 邮箱
	 * @return SysUserDTO
	 */
	SysUserDTO getByEmail(String email);
}
