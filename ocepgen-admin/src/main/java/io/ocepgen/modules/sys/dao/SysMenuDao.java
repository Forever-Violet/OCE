/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.ocepgen.modules.sys.dao;

import io.ocepgen.common.dao.BaseDao;
import io.ocepgen.modules.sys.entity.SysMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface SysMenuDao extends BaseDao<SysMenuEntity> {

	SysMenuEntity getById(@Param("id") Long id);

	/**
	 * 查询所有菜单列表
	 *
	 * @param menuType 菜单类型
	 */
	List<SysMenuEntity> getMenuList(@Param("menuType") Integer menuType);

	/**
	 * 查询用户菜单列表
	 *
	 * @param userId 用户ＩＤ
	 * @param menuType 菜单类型
	 */
	List<SysMenuEntity> getUserMenuList(@Param("userId") Long userId, @Param("menuType") Integer menuType);

	/**
	 * 查询用户权限列表
	 * @param userId  用户ID
	 */
	List<String> getUserPermissionsList(Long userId);

	/**
	 * 查询所有权限列表
	 */
	List<String> getPermissionsList();

	/**
	 * 根据父菜单，查询子菜单
	 * @param pid  父菜单ID
	 */
	List<SysMenuEntity> getListPid(Long pid);

}
