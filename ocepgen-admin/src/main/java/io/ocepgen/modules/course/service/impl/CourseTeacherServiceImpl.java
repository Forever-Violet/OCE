package io.ocepgen.modules.course.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.ocepgen.common.service.impl.CrudServiceImpl;
import io.ocepgen.modules.course.dao.CourseTeacherDao;
import io.ocepgen.modules.course.dto.CourseTeacherDTO;
import io.ocepgen.modules.course.service.CourseTeacherService;
import io.ocepgen.modules.exam.entity.CourseTeacherEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 课程-教师关系表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-04-09
 */
@Service
public class CourseTeacherServiceImpl extends CrudServiceImpl<CourseTeacherDao, CourseTeacherEntity, CourseTeacherDTO> implements CourseTeacherService {

    @Override
    public QueryWrapper<CourseTeacherEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<CourseTeacherEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public List<CourseTeacherEntity> getByCourseId(Long id) {
        LambdaQueryWrapper<CourseTeacherEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CourseTeacherEntity::getCourseId, id);
        return baseDao.selectList(lqw);
    }

    @Override
    public void deleteByCourseId(Long id) {
        baseDao.delete(new LambdaQueryWrapper<CourseTeacherEntity>().eq(CourseTeacherEntity::getCourseId, id));
    }
}
