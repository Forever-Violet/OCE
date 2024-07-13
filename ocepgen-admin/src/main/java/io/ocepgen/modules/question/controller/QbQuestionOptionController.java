package io.ocepgen.modules.question.controller;

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
import io.ocepgen.modules.question.dto.QbQuestionOptionDTO;
import io.ocepgen.modules.question.excel.QbQuestionOptionExcel;
import io.ocepgen.modules.question.service.QbQuestionOptionService;
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
 * 题库_题目选项+答案表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@RestController
@RequestMapping("question/qbquestionoption")
@Api(tags="题库_题目选项+答案表")
public class QbQuestionOptionController {
    @Autowired
    private QbQuestionOptionService qbQuestionOptionService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("question:qbquestionoption:page")
    public Result<PageData<QbQuestionOptionDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<QbQuestionOptionDTO> page = qbQuestionOptionService.page(params);

        return new Result<PageData<QbQuestionOptionDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("question:qbquestionoption:info")
    public Result<QbQuestionOptionDTO> get(@PathVariable("id") Long id){
        QbQuestionOptionDTO data = qbQuestionOptionService.get(id);

        return new Result<QbQuestionOptionDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("question:qbquestionoption:save")
    public Result save(@RequestBody QbQuestionOptionDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        qbQuestionOptionService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("question:qbquestionoption:update")
    public Result update(@RequestBody QbQuestionOptionDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        qbQuestionOptionService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("question:qbquestionoption:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        qbQuestionOptionService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("question:qbquestionoption:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<QbQuestionOptionDTO> list = qbQuestionOptionService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "题库_题目选项+答案表", list, QbQuestionOptionExcel.class);
    }

}