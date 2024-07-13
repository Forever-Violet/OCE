package io.ocepgen.modules.data.service.impl;

import io.ocepgen.common.redis.RedisKeys;
import io.ocepgen.common.redis.RedisUtils;
import io.ocepgen.modules.course.service.CourseService;
import io.ocepgen.modules.data.service.DataDisplayService;
import io.ocepgen.modules.data.vo.AutoAndManualPaperCountVo;
import io.ocepgen.modules.data.vo.CardDataVo;
import io.ocepgen.modules.data.vo.LastAndNowMonthQuestionCountVo;
import io.ocepgen.modules.exam.service.EpExamPaperService;
import io.ocepgen.modules.question.service.QbQuestionService;
import io.ocepgen.modules.security.user.SecurityUser;
import io.ocepgen.modules.security.user.UserDetail;
import io.ocepgen.modules.sys.enums.SuperAdminEnum;
import io.ocepgen.modules.sys.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author roxy
 */
@Service
@Slf4j
public class DataDisplayServiceImpl implements DataDisplayService {

    @Resource
    private QbQuestionService questionService;

    @Resource
    private EpExamPaperService examPaperService;

    @Resource
    private SysUserService userService;

    @Resource
    private CourseService courseService;

    @Resource
    private RedisUtils redisUtils;

    private final long redisDataExpireTime = 60 * 60 * 2; //Redis数据过期时间

/*
    @Override
    public YestAndTodayNewQuestionCountVo getYestAndTodayNewQuestionCount() {
        YestAndTodayNewQuestionCountVo dataVo = new YestAndTodayNewQuestionCountVo();
        // 日期格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // Calendar
        Calendar calendar = Calendar.getInstance();
        // 今天的日期
        String today = format.format(calendar.getTime());
        // 昨天的日期
        calendar.add(Calendar.DATE, -1);
        String yesterday = format.format(calendar.getTime());

        // 昨天的新增题数，按照类型顺序查询
        List<Integer> yestQuestionCount = questionService.getQuestionCountByCreateTime(yesterday);
        // 今天的新增题数
        List<Integer> todayQuestionCount = questionService.getQuestionCountByCreateTime(today);

        dataVo.setYestNewQuestionCount(yestQuestionCount);
        dataVo.setTodayNewQuestionCount(todayQuestionCount);
        return dataVo;
    }
*/

    @Override
    public LastAndNowMonthQuestionCountVo getLastAndNowMonthQuestionCount() {
        // 日期格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // Calendar
        Calendar calendar = Calendar.getInstance();
        // 今天的日期
        String today = format.format(calendar.getTime());
        // 上个月的日期
        calendar.add(Calendar.MONTH, -1);
        String lastMonthDate = format.format(calendar.getTime());
        String key;

        Map<String, Object> params = new HashMap<>();
        //普通管理员，只能查询所属学校
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            String schoolId = user.getSchoolId().toString();
            params.put("schoolId", schoolId);
            key = RedisKeys.getDataDisplayKey("lastAndNowMonthQuestionCount_" + schoolId);
        } else {
            key = RedisKeys.getDataDisplayKey("lastAndNowMonthQuestionCount_superAdmin");
        }

        // 先从Redis找
        LastAndNowMonthQuestionCountVo lastAndNowMonthQuestionCountVo = (LastAndNowMonthQuestionCountVo) redisUtils.get(key);
        if (lastAndNowMonthQuestionCountVo == null) {
            lastAndNowMonthQuestionCountVo = new LastAndNowMonthQuestionCountVo();
            // 上月的新增题数，按照类型顺序查询
            params.put("date", lastMonthDate);
            List<Integer> lastMonthQuestionCount = questionService.getMonthQuestionCountByCreateDate(params);
            // 本月的新增题数
            params.put("date", today);
            List<Integer> nowMonthQuestionCount = questionService.getMonthQuestionCountByCreateDate(params);

            lastAndNowMonthQuestionCountVo.setLastMonthNewQuestionCount(lastMonthQuestionCount);
            lastAndNowMonthQuestionCountVo.setNowMonthNewQuestionCount(nowMonthQuestionCount);
            // 保存到redis，过期时间2小时
            redisUtils.set(key, lastAndNowMonthQuestionCountVo, redisDataExpireTime);
        }
        return lastAndNowMonthQuestionCountVo;
    }


    @Override
    public AutoAndManualPaperCountVo getAutoAndManualPaperCount() {
        String key;
        // 构造查询条件
        Map<String, Object> params = new HashMap<>();
        //普通管理员，只能查询所属学校
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            String schoolId = user.getSchoolId().toString();
            params.put("schoolId", schoolId);
            key = RedisKeys.getDataDisplayKey("autoAndManualPaperCount_" + schoolId);
        } else {
            key = RedisKeys.getDataDisplayKey("autoAndManualPaperCount_superAdmin");
        }
        // 先从Redis查数据
        AutoAndManualPaperCountVo autoAndManualPaperCountVo = (AutoAndManualPaperCountVo) redisUtils.get(key);
        if (autoAndManualPaperCountVo == null) {
            //Redis不存在则去数据库查询
            autoAndManualPaperCountVo = examPaperService.getAutoAndManualPaperCount(params);
            // 保存到Redis， 过期时间2小时
            redisUtils.set(key, autoAndManualPaperCountVo, redisDataExpireTime);
        }
        return autoAndManualPaperCountVo;
    }

    @Override
    public CardDataVo getCardData() {
        Map<String, Object> params = new HashMap<>();
        String key;
        UserDetail user = SecurityUser.getUser();
        //普通管理员，只能查询所属学校
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            String schoolId = user.getSchoolId().toString();
            params.put("schoolId", schoolId);
            key = RedisKeys.getDataDisplayKey("cardData_" + schoolId);
        } else {
            key = RedisKeys.getDataDisplayKey("cardData_superAdmin");
        }

        // 先从Redis中查询，非超管 根据schoolId，
        CardDataVo cardDataVo = (CardDataVo) redisUtils.get(key);
        if (cardDataVo == null) { //Redis不存在则去数据库查询
            cardDataVo = new CardDataVo();

            cardDataVo.setTeacherCount(userService.getTeacherCountByCondition(params));
            cardDataVo.setQuestionCount(questionService.getQuestionCountByCondition(params));
            cardDataVo.setExamPaperCount(examPaperService.getExamPaperCountByCondition(params));
            cardDataVo.setCourseCount(courseService.getCourseCountByCondition(params));
            // 保存到Redis， 过期时间2小时
            redisUtils.set(key, cardDataVo, redisDataExpireTime);
        }
        return cardDataVo;


    }
}
