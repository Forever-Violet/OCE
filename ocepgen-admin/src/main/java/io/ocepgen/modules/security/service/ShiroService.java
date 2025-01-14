/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.ocepgen.modules.security.service;

import io.ocepgen.modules.security.user.UserDetail;
import io.ocepgen.modules.sys.entity.SysUserEntity;
import io.ocepgen.modules.security.entity.SysUserTokenEntity;

import java.util.List;
import java.util.Set;

/**
 * shiro相关接口
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface ShiroService {
    /**
     * 获取用户权限列表
     */
    Set<String> getUserPermissions(UserDetail user);

    SysUserTokenEntity getByToken(String token);

    /**
     * 根据用户ID，查询用户
     * @param userId
     */
    SysUserEntity getUser(Long userId);

    /**
     * 获取用户对应的学校数据权限
     * @param userId  用户ID
     * @return        返回学校ID列表
     */
    List<Long> getDataScopeList(Long userId);
}
