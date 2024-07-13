package io.ocepgen.modules.data.controller;

import io.ocepgen.common.utils.Result;
import io.ocepgen.modules.data.service.DataDisplayService;
import io.ocepgen.modules.data.vo.AutoAndManualPaperCountVo;
import io.ocepgen.modules.data.vo.CardDataVo;
import io.ocepgen.modules.data.vo.LastAndNowMonthQuestionCountVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 试卷表
 *
 * @author Roxy 1250812574@qq.com
 * @since 1.0.0 2024-03-18
 */
@RestController
@RequestMapping("dataDisplay")
@Api(tags="数据展示")
@Slf4j
public class DataDisplayController {
    @Autowired
    private DataDisplayService dataDisplayService;


//    @PostMapping("yestAndTodayNewQuestionCount")
//    @ApiOperation("信息")
//    //@RequiresPermissions("exam:epexampaper:info")
//    public Result<YestAndTodayNewQuestionCountVo> getYestAndTodayNewQuestionCount(){
//        YestAndTodayNewQuestionCountVo data = dataDisplayService.getYestAndTodayNewQuestionCount();
//
//        return new Result<YestAndTodayNewQuestionCountVo>().ok(data);
//    }

    @PostMapping("lastAndNowMonthQuestionCount")
    @ApiOperation("信息")
    //@RequiresPermissions("exam:epexampaper:info")
    public Result<LastAndNowMonthQuestionCountVo> getLastAndNowMonthQuestionCount(){
        LastAndNowMonthQuestionCountVo data = dataDisplayService.getLastAndNowMonthQuestionCount();

        return new Result<LastAndNowMonthQuestionCountVo>().ok(data);
    }

    @PostMapping("autoAndManualPaperCount")
    @ApiOperation("信息")
    //@RequiresPermissions("exam:epexampaper:info")
    public Result<AutoAndManualPaperCountVo> getAutoAndManualPaperCount(){
        AutoAndManualPaperCountVo data = dataDisplayService.getAutoAndManualPaperCount();

        return new Result<AutoAndManualPaperCountVo>().ok(data);
    }

    @PostMapping("cardData")
    @ApiOperation("信息")
    //@RequiresPermissions("exam:epexampaper:info")
    public Result<CardDataVo> getCardData(){
        CardDataVo data = dataDisplayService.getCardData();

        return new Result<CardDataVo>().ok(data);
    }

}
