/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.ocepgen.modules.job.controller;

import io.ocepgen.common.constant.Constant;
import io.ocepgen.common.page.PageData;
import io.ocepgen.common.utils.Result;
import io.ocepgen.modules.job.dto.ScheduleJobLogDTO;
import io.ocepgen.modules.job.service.ScheduleJobLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * 定时任务日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/sys/scheduleLog")
@Api(tags = "定时任务日志")
@AllArgsConstructor
public class ScheduleJobLogController {
    private final ScheduleJobLogService scheduleJobLogService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "jobId", value = "jobId", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:schedule:log")
    public Result<PageData<ScheduleJobLogDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ScheduleJobLogDTO> page = scheduleJobLogService.page(params);

        return new Result<PageData<ScheduleJobLogDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("sys:schedule:log")
    public Result<ScheduleJobLogDTO> info(@PathVariable("id") Long id) {
        ScheduleJobLogDTO log = scheduleJobLogService.get(id);

        return new Result<ScheduleJobLogDTO>().ok(log);
    }
}
