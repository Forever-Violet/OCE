package io.ocepgen.modules.course.controller;

import io.ocepgen.common.annotation.LogOperation;
import io.ocepgen.common.constant.Constant;
import io.ocepgen.common.page.PageData;
import io.ocepgen.common.utils.ExcelUtils;
import io.ocepgen.common.utils.Result;
import io.ocepgen.common.validator.AssertUtils;
import io.ocepgen.common.validator.ValidatorUtils;
import io.ocepgen.common.validator.group.AddGroup;
import io.ocepgen.common.validator.group.DefaultGroup;
import io.ocepgen.common.validator.group.UpdateGroup;
import io.ocepgen.modules.course.dto.CourseDTO;
import io.ocepgen.modules.course.excel.CourseExcel;
import io.ocepgen.modules.course.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 课程信息表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@RestController
@RequestMapping("course/course")
@Api(tags="课程信息表")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("course:course:page")
    public Result<PageData<CourseDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<CourseDTO> page = courseService.page(params);

        return new Result<PageData<CourseDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("course:course:info")
    public Result<CourseDTO> get(@PathVariable("id") Long id){
        CourseDTO data = courseService.get(id);

        return new Result<CourseDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("course:course:save")
    public Result save(@RequestBody CourseDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        courseService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("course:course:update")
    public Result update(@RequestBody CourseDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        courseService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("course:course:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        courseService.delete(ids);

        return new Result();
    }

    @GetMapping("list")
    @ApiOperation("获取班级列表")
    @LogOperation("获取班级列表")
    @RequiresPermissions("course:course:list")
    public Result<List<CourseDTO>> list(@ApiIgnore @RequestParam Map<String, Object> params) throws Exception {
        List<CourseDTO> list = courseService.list(params);

        return new Result<List<CourseDTO>>().ok(list);
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("course:course:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<CourseDTO> list = courseService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "课程信息表", list, CourseExcel.class);
    }

}
