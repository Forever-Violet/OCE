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
import io.ocepgen.modules.exam.autogenpaper.vo.GenExamPaperRuleVo;
import io.ocepgen.modules.exam.dto.EpExamPaperDTO;
import io.ocepgen.modules.exam.excel.EpExamPaperExcel;
import io.ocepgen.modules.exam.service.EpExamPaperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 试卷表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@RestController
@RequestMapping("exam/epexampaper")
@Api(tags="试卷表")
@Slf4j
public class EpExamPaperController {
    @Autowired
    private EpExamPaperService epExamPaperService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("exam:epexampaper:page")
    public Result<PageData<EpExamPaperDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<EpExamPaperDTO> page = epExamPaperService.page(params);

        return new Result<PageData<EpExamPaperDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("exam:epexampaper:info")
    public Result<EpExamPaperDTO> get(@PathVariable("id") Long id){
        EpExamPaperDTO data = epExamPaperService.get(id);

        return new Result<EpExamPaperDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("exam:epexampaper:save")
    public Result save(@RequestBody EpExamPaperDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        epExamPaperService.save(dto);

        return new Result();
    }

    @PostMapping("generate")
    @ApiOperation("智能组卷")
    @LogOperation("智能组卷")
    @RequiresPermissions("exam:epexampaper:generate")
    public Result<EpExamPaperDTO> generate(@RequestBody GenExamPaperRuleVo rule, BindingResult result){
        //效验数据
        ValidatorUtils.validateEntity(rule, DefaultGroup.class);

        log.warn("rule:{}",rule);
        return new Result<EpExamPaperDTO>().ok(epExamPaperService.generateExamPaper(rule));
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("exam:epexampaper:update")
    public Result update(@RequestBody EpExamPaperDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        epExamPaperService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("exam:epexampaper:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        epExamPaperService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("exam:epexampaper:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<EpExamPaperDTO> list = epExamPaperService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "试卷表", list, EpExamPaperExcel.class);
    }

    @GetMapping("export/word")
    @ApiOperation("导出word")
    @LogOperation("导出word")
    @RequiresPermissions("exam:epexampaper:export")
    public void exportWord(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        epExamPaperService.exportWord(params, response);


    }

    @GetMapping("export/pdf")
    @ApiOperation("导出pdf")
    @LogOperation("导出pdf")
    @RequiresPermissions("exam:epexampaper:export")
    public void exportPdf(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        epExamPaperService.exportPDF(params, response);

    }

    @GetMapping("export/answer")
    @ApiOperation("导出纯答案")
    @LogOperation("导出纯答案")
    @RequiresPermissions("exam:epexampaper:export")
    public void exportAnswer(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        epExamPaperService.exportAnswer(params, response);

    }

}
