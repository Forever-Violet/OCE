/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.ocepgen.modules.sys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.ocepgen.common.constant.Constant;
import io.ocepgen.common.page.PageData;
import io.ocepgen.common.service.impl.BaseServiceImpl;
import io.ocepgen.common.utils.ConvertUtils;
import io.ocepgen.modules.security.password.PasswordUtils;
import io.ocepgen.modules.security.user.SecurityUser;
import io.ocepgen.modules.security.user.UserDetail;
import io.ocepgen.modules.sys.dao.SysUserDao;
import io.ocepgen.modules.sys.dto.SysUserDTO;
import io.ocepgen.modules.sys.entity.SysUserEntity;
import io.ocepgen.modules.sys.enums.SuperAdminEnum;
import io.ocepgen.modules.sys.service.SysSchoolService;

import io.ocepgen.modules.sys.service.SysRoleUserService;
import io.ocepgen.modules.sys.service.SysUserClassService;
import io.ocepgen.modules.sys.service.SysUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * 系统用户
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
@AllArgsConstructor
public class SysUserServiceImpl extends BaseServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
    private final SysRoleUserService sysRoleUserService;
    private final SysSchoolService sysSchoolService;
    private final SysUserClassService sysUserClassService;


    @Override
    public PageData<SysUserDTO> page(Map<String, Object> params) {
        //转换成like
        paramsToLike(params, "username");

        //分页
        IPage<SysUserEntity> page = getPage(params, Constant.CREATE_DATE, false);


        return new PageData<>(getList(params), page.getTotal());
        //return getPageData(dtoList, page.getTotal(), SysUserDTO.class);
    }

    private List<SysUserDTO> getList(Map<String, Object> params) {
        //普通管理员，只能查询所属学校的用户
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            String schoolId = user.getSchoolId().toString();
            params.put("schoolId", schoolId);
        }

        List<SysUserEntity> list = baseDao.getList(params);
        List<SysUserDTO> dtoList = new ArrayList<>(list.size());

        list.forEach(entity -> {
            SysUserDTO sysUserDTO = new SysUserDTO();
            BeanUtils.copyProperties(entity, sysUserDTO);
            List<Long> roleIdList = sysRoleUserService.getRoleIdList(entity.getId());
            // 填充角色id列表
            sysUserDTO.setRoleIdList(roleIdList);
            // 填充角色名称列表
            sysUserDTO.setRoleNameList(sysRoleUserService.getRoleNameList(roleIdList));

            List<Long> classIdList = sysUserClassService.getClassIdList(entity.getId());
            // 填充班级id列表
            sysUserDTO.setClassIdList(classIdList);
            // 填充班级名称列表
            sysUserDTO.setClassNameList(sysUserClassService.getClassNameList(classIdList));
            dtoList.add(sysUserDTO);
        });
        return dtoList;
    }

    @Override
    public List<SysUserDTO> list(Map<String, Object> params) {
        //普通管理员，只能查询所属学校的数据
        return getList(params);
        //return ConvertUtils.sourceToTarget(entityList, SysUserDTO.class);
    }



    @Override
    public SysUserDTO get(Long id) {
        SysUserEntity entity = baseDao.getById(id);
        SysUserDTO dto = new SysUserDTO();
        dto = ConvertUtils.sourceToTarget(entity, SysUserDTO.class);
        //用户角色列表
        List<Long> roleIdList = sysRoleUserService.getRoleIdList(id);
        dto.setRoleIdList(roleIdList);

        //用户班级列表
        List<Long> classIdList = sysUserClassService.getClassIdList(id);
        dto.setClassIdList(classIdList);
        return dto;
    }

    @Override
    public SysUserDTO getByUsername(String username) {
        SysUserEntity entity = baseDao.getByUsername(username);
        return ConvertUtils.sourceToTarget(entity, SysUserDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysUserDTO dto) {
        SysUserEntity entity = ConvertUtils.sourceToTarget(dto, SysUserEntity.class);

        //密码加密
        String password = PasswordUtils.encode(entity.getPassword());
        entity.setPassword(password);

        //保存用户
        entity.setSuperAdmin(SuperAdminEnum.NO.value());

        //普通管理员，只能插入其所属学校的用户
        if (entity.getSchoolId() == null) { //这里只是做个校验，因为前端已经把schoolId填进去了，后端再进行一次校验
            UserDetail user = SecurityUser.getUser();
            if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) { //如果用户不是superAdmin
                entity.setSchoolId(user.getSchoolId());
            }
        }
        insert(entity);

        //保存角色用户关系
        sysRoleUserService.saveOrUpdate(entity.getId(), dto.getRoleIdList());
        //保存班级用户关系
        sysUserClassService.saveOrUpdate(entity.getId(), dto.getClassIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysUserDTO dto) {
        SysUserEntity entity = ConvertUtils.sourceToTarget(dto, SysUserEntity.class);

        //密码加密
        if (StrUtil.isBlank(dto.getPassword())) {
            entity.setPassword(null);
        } else {
            String password = PasswordUtils.encode(entity.getPassword());
            entity.setPassword(password);
        }

        //更新用户
        updateById(entity);

        //更新角色用户关系
        sysRoleUserService.saveOrUpdate(entity.getId(), dto.getRoleIdList());
        //更新班级用户关系
        sysUserClassService.saveOrUpdate(entity.getId(), dto.getClassIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        //删除用户
        baseDao.deleteBatchIds(Arrays.asList(ids));

        //删除角色用户关系
        sysRoleUserService.deleteByUserIds(ids);

        //删除班级用户关系
        sysUserClassService.deleteByUserIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long id, String newPassword) {
        newPassword = PasswordUtils.encode(newPassword);

        baseDao.updatePassword(id, newPassword);
    }

    @Override
    public int getCountBySchoolId(Long schoolId) {
        return baseDao.getCountBySchoolId(schoolId);
    }

    @Override
    public List<Long> getUserIdListBySchoolId(List<Long> schoolIdList) {
        return baseDao.getUserIdListBySchoolId(schoolIdList);
    }

    @Override
    public List<SysUserDTO> getStudentList(Map<String, Object> params) {
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            // 如果用户非超级管理员，只能查询其学校的学生列表
            String schoolId = user.getSchoolId().toString();
            params.put("schoolId", schoolId);
        }
        return baseDao.getStudentList(params);
    }

    @Override
    public List<SysUserDTO> getTeacherList(Map<String, Object> params) {
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            // 如果用户非超级管理员，只能查询其学校的学生列表
            String schoolId = user.getSchoolId().toString();
            params.put("schoolId", schoolId);
        }
        return baseDao.getTeacherList(params);
    }

    @Override
    public String getUsernameAndRealNameByUserId(Long userId) {

        SysUserEntity user = baseDao.getById(userId);
        // 拼接真实姓名 和 用户名
        return user.getRealName() + "（" + user.getUsername() + "）";
    }

    @Override
    public List<SysUserDTO> getListByIds(List<Long> idList) {
        return ConvertUtils.sourceToTarget(baseDao.selectBatchIds(idList), SysUserDTO.class);
    }

    @Override
    public Integer getTeacherCountByCondition(Map<String, Object> params) {
        params.put("roleName", "老师");
        return baseDao.getCountByCondition(params);
    }

    @Override
    public SysUserDTO getByUsernameAndSchool(String username, Long schoolId) {
        LambdaQueryWrapper<SysUserEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUserEntity::getUsername, username)
                .eq(SysUserEntity::getSchoolId, schoolId);

        SysUserEntity user = baseDao.selectOne(lqw);
        // 如果user为空，则再查一次，确认是否为超级管理员

        if (user == null) {
            // 超级管理员
            lqw = new LambdaQueryWrapper<>();
            lqw.eq(SysUserEntity::getUsername, username)
                    .eq(SysUserEntity::getSuperAdmin, SuperAdminEnum.YES.value());

            user = baseDao.selectOne(lqw);
        }

        return ConvertUtils.sourceToTarget(user, SysUserDTO.class);
    }

    @Override
    public SysUserDTO getByEmail(String email) {
        LambdaQueryWrapper<SysUserEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUserEntity::getEmail, email);

        return ConvertUtils.sourceToTarget(baseDao.selectOne(lqw), SysUserDTO.class);
    }
}
