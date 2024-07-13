package io.ocepgen.modules.exam.controller;

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
import io.ocepgen.modules.exam.dto.ExamGenerateParamsDTO;
import io.ocepgen.modules.exam.excel.ExamGenerateParamsExcel;
import io.ocepgen.modules.exam.service.ExamGenerateParamsService;
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
 * 组卷参数配置表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-05-10
 */
@RestController
@RequestMapping("exam/generateparams")
@Api(tags="组卷参数配置表")
public class ExamGenerateParamsController {
    @Autowired
    private ExamGenerateParamsService examGenerateParamsService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("exam:generateparams:page")
    public Result<PageData<ExamGenerateParamsDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<ExamGenerateParamsDTO> page = examGenerateParamsService.page(params);

        return new Result<PageData<ExamGenerateParamsDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("exam:generateparams:info")
    public Result<ExamGenerateParamsDTO> get(@PathVariable("id") Long id){
        ExamGenerateParamsDTO data = examGenerateParamsService.get(id);

        return new Result<ExamGenerateParamsDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("exam:generateparams:save")
    public Result save(@RequestBody ExamGenerateParamsDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        examGenerateParamsService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("exam:generateparams:update")
    public Result update(@RequestBody ExamGenerateParamsDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        examGenerateParamsService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("exam:generateparams:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        examGenerateParamsService.delete(ids);

        return new Result();
    }

    @GetMapping("list")
    @ApiOperation("获取组卷配置列表")
    @LogOperation("获取组卷配置列表")
    @RequiresPermissions("exam:generateparams:list")
    public Result<List<ExamGenerateParamsDTO>> list(@ApiIgnore @RequestParam Map<String, Object> params) throws Exception {
        List<ExamGenerateParamsDTO> list = examGenerateParamsService.list(params);

        return new Result<List<ExamGenerateParamsDTO>>().ok(list);
    }

    @GetMapping("rankingList")
    @ApiOperation("获取组卷配置排行榜")
    @LogOperation("获取组卷配置排行榜")
    @RequiresPermissions("exam:generateparams:list")
    public Result<List<ExamGenerateParamsDTO>> rankingList(@ApiIgnore @RequestParam Map<String, Object> params) throws Exception {
        List<ExamGenerateParamsDTO> list = examGenerateParamsService.getRankingList(params);

        return new Result<List<ExamGenerateParamsDTO>>().ok(list);
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("exam:generateparams:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<ExamGenerateParamsDTO> list = examGenerateParamsService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "组卷参数配置表", list, ExamGenerateParamsExcel.class);
    }

}
