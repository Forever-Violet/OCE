package io.ocepgen.modules.course.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.ocepgen.common.constant.Constant;
import io.ocepgen.common.exception.OcepgenException;
import io.ocepgen.common.page.PageData;
import io.ocepgen.common.service.impl.CrudServiceImpl;
import io.ocepgen.modules.course.dao.CourseDao;
import io.ocepgen.modules.course.dto.CourseDTO;
import io.ocepgen.modules.course.dto.CourseTeacherDTO;
import io.ocepgen.modules.course.entity.CourseEntity;
import io.ocepgen.modules.course.service.CourseService;
import io.ocepgen.modules.course.service.CourseTeacherService;
import io.ocepgen.modules.exam.entity.CourseTeacherEntity;
import io.ocepgen.modules.security.user.SecurityUser;
import io.ocepgen.modules.security.user.UserDetail;
import io.ocepgen.modules.sys.dto.SysUserDTO;
import io.ocepgen.modules.sys.enums.SuperAdminEnum;
import io.ocepgen.modules.sys.service.SysRoleUserService;
import io.ocepgen.modules.sys.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程信息表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@Service
public class CourseServiceImpl extends CrudServiceImpl<CourseDao, CourseEntity, CourseDTO> implements CourseService {

    @Resource
    SysUserService sysUserService;
    @Resource
    SysRoleUserService sysRoleUserService;
    @Resource
    CourseTeacherService courseTeacherService;

    @Override //摆设
    public QueryWrapper<CourseEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        QueryWrapper<CourseEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);
        return wrapper;
    }

    @Override
    public PageData<CourseDTO> page(Map<String, Object> params) {

        //分页
        IPage<CourseEntity> page = getPage(params, Constant.CREATE_DATE, false);

        return new PageData<>(getList(params), page.getTotal());
    }

    @Override
    public List<CourseDTO> list(Map<String, Object> params) {
        return getList(params);
    }

    @Override
    @Transactional
    public void save(CourseDTO dto) {
        // 先保存课程信息，新增的记录返回id到dto
        super.save(dto);
        // 再保存课程与教师关系
        dto.getTeacherIdList().forEach(teacherId -> {
            CourseTeacherDTO relation = new CourseTeacherDTO();
            relation.setCourseId(dto.getId());
            relation.setTeacherId(teacherId);
            courseTeacherService.save(relation);
        });

    }

    @Override
    public CourseDTO get(Long id) {

        // 查询课程信息
        CourseDTO dto = super.get(id);
        // 查询课程与教师关系
        List<CourseTeacherEntity> relationList = courseTeacherService.getByCourseId(id);
        // 设置教师id列表
        dto.setTeacherIdList(relationList.stream().map(CourseTeacherEntity::getTeacherId).collect(Collectors.toList()));
        return dto;
    }

    @Override
    @Transactional
    public void update(CourseDTO dto) {
        // 先查询 数据库中的 课程与教师关系，待删除列表
        List<CourseTeacherEntity> relationList = courseTeacherService.getByCourseId(dto.getId());
        // 遍历 传来的 教师id列表
        for (Long teacherId : dto.getTeacherIdList()) {
            if (relationList.stream().anyMatch(relation -> relation.getTeacherId().equals(teacherId))) {
                //去除当前教师id
                relationList.removeIf(relation -> relation.getTeacherId().equals(teacherId));
            } else {
                // 如果不存在于数据库，说明是新增
                CourseTeacherDTO relation = new CourseTeacherDTO();
                relation.setCourseId(dto.getId());
                relation.setTeacherId(teacherId);
                courseTeacherService.save(relation);
            }
        }
        // 删除 待删除的 课程-教师信息
        if (relationList.size() > 0) {
            courseTeacherService.deleteBatchIds(relationList.stream().map(CourseTeacherEntity::getId).collect(Collectors.toList()));
        }

        super.update(dto);
    }

    private List<CourseDTO> getList(Map<String, Object> params) {
        //普通管理员，只能查询所属学校的用户
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            String schoolId = user.getSchoolId().toString();
            params.put("schoolId", schoolId);
        }

        List<CourseEntity> list = baseDao.getList(params);
        List<CourseDTO> dtoList = new ArrayList<>(list.size());

        list.forEach(entity -> {
            CourseDTO courseDTO = new CourseDTO();
            // 设置创建者名称（如果不为空的话）
            if (entity.getCreator() != null) {
                courseDTO.setCreatorName(sysUserService.getUsernameAndRealNameByUserId(entity.getCreator()));
            }
            BeanUtils.copyProperties(entity, courseDTO);
            // 查询课程的任教老师idList
            List<Long> teacherIdList = courseTeacherService
                    .getByCourseId(entity.getId()).stream().map(CourseTeacherEntity::getTeacherId)
                    .collect(Collectors.toList());
            if (!teacherIdList.isEmpty()) {
                // 根据教师idList查询教师列表
                List<SysUserDTO> teacherList = sysUserService.getListByIds(teacherIdList);
                // 设置任教老师真实姓名列表
                courseDTO.setTeacherNameList(teacherList.stream().map(SysUserDTO::getRealName).collect(Collectors.toList()));
            }

            dtoList.add(courseDTO);
        });
        return dtoList;
    }

    @Override
    @Transactional
    public void delete(Long[] ids) {
        // 查询当前用户类型
        UserDetail user = SecurityUser.getUser();
        // 根据用户id查询其角色id列表
        List<Long> roleIdList = sysRoleUserService.getRoleIdList(user.getId());
        // 如果不是只有一个角色，那直接跳过，如果只有一个角色，并且角色为老师
        if (roleIdList.size() == 1 && "老师".equals(sysRoleUserService.getRoleNameList(roleIdList).get(0))) {
            for (Long id : ids) {
                CourseEntity entity = baseDao.selectById(id);
                // 判断是否为当前用户创建
                if (!entity.getCreator().equals(user.getId())) {
                    throw new OcepgenException("您没有权限删除{" + entity.getCourseName() + "}课程");
                }
            }
        }
        for (Long id : ids) {
            // 删除课程与教师的关系
            courseTeacherService.deleteByCourseId(id);
        }
        // 删除课程
        super.delete(ids);
    }

    @Override
    public Integer getCourseCountByCondition(Map<String, Object> params) {
        return baseDao.getCourseCountByCondition(params);
    }
}
