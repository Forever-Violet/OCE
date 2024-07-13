package io.ocepgen.modules.question.controller;

import io.ocepgen.common.annotation.LogOperation;
import io.ocepgen.common.constant.Constant;
import io.ocepgen.common.page.PageData;
import io.ocepgen.common.utils.Result;
import io.ocepgen.common.validator.AssertUtils;
import io.ocepgen.common.validator.ValidatorUtils;
import io.ocepgen.common.validator.group.AddGroup;
import io.ocepgen.common.validator.group.DefaultGroup;
import io.ocepgen.common.validator.group.UpdateGroup;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.ocepgen.modules.question.service.QbQuestionService;
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
 * 题库_题目表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@RestController
@RequestMapping("question/question")
@Api(tags="题库_题目表")
public class QbQuestionController {
    @Autowired
    private QbQuestionService qbQuestionService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("question:question:page")
    public Result<PageData<QbQuestionDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<QbQuestionDTO> page = qbQuestionService.page(params);

        return new Result<PageData<QbQuestionDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("question:question:info")
    public Result<QbQuestionDTO> get(@PathVariable("id") Long id){
        QbQuestionDTO data = qbQuestionService.get(id);

        return new Result<QbQuestionDTO>().ok(data);
    }

    @GetMapping("list")
    @ApiOperation("问题列表")
    @RequiresPermissions("question:question:list")
    public Result<List<QbQuestionDTO>> getQuestionList(@ApiIgnore @RequestParam Map<String, Object> params){
        List<QbQuestionDTO> data = qbQuestionService.getListAndOptions(params);

        return new Result<List<QbQuestionDTO>>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("question:question:save")
    public Result save(@RequestBody QbQuestionDTO dto){
        //String requestPostStr = GetRequestJsonUtils.getRequestPostStr(request);
        //QbQuestionDTO dto = JSONObject.parseObject(requestPostStr, QbQuestionDTO.class);
/*        HttpServletRequest orgRequest = XssHttpServletRequestWrapper.getOrgRequest(request);
        String content = orgRequest.getParameter("analysis");
        System.out.println(content);*/
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        qbQuestionService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("question:question:update")
    public Result update(@RequestBody QbQuestionDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        qbQuestionService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("question:question:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        qbQuestionService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("question:question:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {

        qbQuestionService.exportQuestionListToWord(params, response);

    }

}
