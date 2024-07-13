package io.ocepgen.modules.exam.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.ocepgen.common.constant.Constant;
import io.ocepgen.common.exception.OcepgenException;
import io.ocepgen.common.page.PageData;
import io.ocepgen.common.service.impl.CrudServiceImpl;
import io.ocepgen.common.utils.ConvertUtils;
import io.ocepgen.modules.exam.dao.ExamGenerateParamsDao;
import io.ocepgen.modules.exam.dto.ExamGenerateParamsDTO;
import io.ocepgen.modules.exam.entity.ExamGenerateParamsEntity;
import io.ocepgen.modules.exam.enums.GeneratePaperParamsEnum;
import io.ocepgen.modules.exam.service.ExamGenerateParamsService;
import io.ocepgen.modules.security.user.SecurityUser;
import io.ocepgen.modules.security.user.UserDetail;
import io.ocepgen.modules.sys.enums.SuperAdminEnum;
import io.ocepgen.modules.sys.service.SysRoleUserService;
import io.ocepgen.modules.sys.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 组卷参数配置表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-05-10
 */
@Service
public class ExamGenerateParamsServiceImpl extends CrudServiceImpl<ExamGenerateParamsDao, ExamGenerateParamsEntity, ExamGenerateParamsDTO> implements ExamGenerateParamsService {

    @Resource
    private ExamGenerateParamsDao examGenerateParamsDao;
    @Resource
    SysUserService sysUserService;

    @Resource
    SysRoleUserService sysRoleUserService;

    @Override
    public QueryWrapper<ExamGenerateParamsEntity> getWrapper(Map<String, Object> params){

        String schoolId = (String) params.get("schoolId");

        QueryWrapper<ExamGenerateParamsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(schoolId), "school_id", schoolId);

        return wrapper;
    }


    private List<ExamGenerateParamsDTO> getList(Map<String, Object> params) {

        //普通管理员，只能查询所属学校的用户
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            params.put("schoolId", user.getSchoolId().toString());
        }
        params.put("userId", user.getId());

        List<ExamGenerateParamsEntity> list = baseDao.getList(params);

        List<ExamGenerateParamsDTO> dtoList = new ArrayList<>(list.size());

        list.forEach(entity -> {
            // 如果当前配置是账号私有的，并且创建者又不是当前用户，那么将这条数据过滤掉，也就是跳出本次循环，不添加到dtoList
            if (entity.getScope() == GeneratePaperParamsEnum.PRIVATE.value() && !user.getId().equals(entity.getCreator())) {
                return;
            }

            ExamGenerateParamsDTO paramsDTO = new ExamGenerateParamsDTO();
            // 设置创建者名称（如果不为空的话）
            if (entity.getCreator() != null) {
                paramsDTO.setCreatorName(sysUserService.getUsernameAndRealNameByUserId(entity.getCreator()));
            }
            BeanUtils.copyProperties(entity, paramsDTO);
            dtoList.add(paramsDTO);
        });
        return dtoList;
    }


    @Override
    public PageData<ExamGenerateParamsDTO> page(Map<String, Object> params) {
        //分页
        IPage<ExamGenerateParamsEntity> page = getPage(params, Constant.CREATE_DATE, false);


        return new PageData<>(getList(params), page.getTotal());
    }

    @Override
    public List<ExamGenerateParamsDTO> list(Map<String, Object> params) {
        return getList(params);
    }


    @Override
    public void delete(Long[] ids) {
        // 查询当前用户类型
        UserDetail user = SecurityUser.getUser();
        // 根据用户id查询其角色id列表
        List<Long> roleIdList = sysRoleUserService.getRoleIdList(user.getId());
        // 如果不是只有一个角色，那直接跳过，如果只有一个角色，并且角色为老师
        if (roleIdList.size() == 1 && "老师".equals(sysRoleUserService.getRoleNameList(roleIdList).get(0))) {
            for (Long id : ids) {
                ExamGenerateParamsEntity entity = baseDao.selectById(id);
                // 判断是否为当前用户创建
                if (!entity.getCreator().equals(user.getId())) { //如果不是
                    throw new OcepgenException("您没有权限删除{" + entity.getConfigName() + "}配置");
                }
            }
        }
        super.delete(ids);

    }


    @Override
    public void save(ExamGenerateParamsDTO dto) {
        // 填充用户学校Id
        UserDetail user = SecurityUser.getUser();
        Long schoolId = null;
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            schoolId = user.getSchoolId();
        } else {
            // 超级管理员用
            schoolId = 12138L;
        }
        dto.setSchoolId(schoolId);
        // 初始化准确度和使用次数
        dto.setUseTimes(0);
        dto.setAccuracy(0.00f);
        super.save(dto);
    }

    @Override
    public List<ExamGenerateParamsDTO> getRankingList(Map<String, Object> params) {
        //普通管理员，只能查询所属学校的用户
        UserDetail user = SecurityUser.getUser();
        String schoolId = null;
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            schoolId = user.getSchoolId().toString();
        } else if (params.get("schoolId") != null) {
            schoolId = params.get("schoolId").toString();
        }
        // 构造查询条件
        LambdaQueryWrapper<ExamGenerateParamsEntity> lqw = new LambdaQueryWrapper<>();
        // 以准确率accuracy为排序条件，取前十记录，且只有公共的配置才参与排行
        lqw.eq(StringUtils.isNotBlank(schoolId), ExamGenerateParamsEntity::getSchoolId, schoolId)
                .orderBy(true, false, ExamGenerateParamsEntity::getAccuracy)
                .eq(ExamGenerateParamsEntity::getScope, GeneratePaperParamsEnum.PUBLIC.value())
                .last("limit 10");
        return ConvertUtils.sourceToTarget(baseDao.selectList(lqw), ExamGenerateParamsDTO.class);
    }
}
