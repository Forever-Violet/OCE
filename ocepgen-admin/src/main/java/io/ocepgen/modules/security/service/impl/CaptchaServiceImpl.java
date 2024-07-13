/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.ocepgen.modules.security.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.ocepgen.common.dto.EmailDTO;
import io.ocepgen.common.redis.RedisKeys;
import io.ocepgen.common.redis.RedisUtils;
import io.ocepgen.common.utils.CommonUtils;
import io.ocepgen.common.utils.EmailUtils;
import io.ocepgen.modules.security.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * 验证码
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private EmailUtils emailUtils;
    @Value("${ocepgen.redis.open: false}")
    private boolean open;
    /**
     * Local Cache  5分钟过期
     */
    Cache<String, String> localCache = CacheUtil.newLRUCache(1000, 1000 * 60 * 5);

    @Override
    public void create(HttpServletResponse response, String uuid) throws IOException {
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        //生成验证码
        SpecCaptcha captcha = new SpecCaptcha(150, 40);
        captcha.setLen(5);
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        captcha.out(response.getOutputStream());

        //保存到缓存
        setCache(uuid, captcha.text());
    }

    @Override
    public boolean validate(String key, String code) {
        //获取验证码
        String captcha = getCache(key);

        //效验成功
        if (code.equalsIgnoreCase(captcha)) {
            return true;
        }

        return false;
    }


    private void setCache(String key, String value) {
        if (open) {
            key = RedisKeys.getCaptchaKey(key);
            redisUtils.set(key, value, 300); //5分钟过期
        } else {
            localCache.put(key, value);
        }
    }

    private String getCache(String key) {
        if (open) {
            key = RedisKeys.getCaptchaKey(key);
            String captcha = (String) redisUtils.get(key);
            //删除验证码
            if (captcha != null) {
                redisUtils.delete(key);
            }

            return captcha;
        }

        String captcha = localCache.get(key);
        //删除验证码
        if (captcha != null) {
            localCache.remove(key);
        }
        return captcha;
    }

    @Override
    public boolean resetPwdCaptchaValidate(String key, String code) {
        String captcha;
        //获取验证码
        if (open) {
            captcha = (String) redisUtils.get(key);
        } else  {
            captcha = localCache.get(key);
        }

        //效验成功
        return code.equalsIgnoreCase(captcha);
    }

    @Override
    public boolean postToEmail(String email) {
        String captcha = CommonUtils.getRandomCode();

        EmailDTO emailDTO = EmailDTO.builder()
                .email(email)
                .subject("验证码（一键生成考卷系统）")
                .contentMap(new HashMap<String, Object>(){{
                    put("content", "您正在重置密码，验证码为：" + captcha + "，该验证码5分钟内有效，请及时使用。");
                }})
                .template("/email/email.html") // thymeleaf模版引擎默认从classpath:/templates 目录下加载模版文件，不能用绝对路径
                .build();
        emailUtils.sendHtmlMail(emailDTO);
        // 保存验证码到redis
        setCache(email, captcha);
        return true;
    }

    @Override
    public Integer getEmailPostCaptchaTimes(String email) {
        String key = RedisKeys.getCaptchaPostTimesKey(email);
        // 先获取邮箱对应的次数
        Integer times = (Integer) redisUtils.get(key);
        if (times == null) {
            times = 0;
            // 没有次数，则设置次数为1
            redisUtils.set(key, 1, 5 * 60); // 5分钟过期
        } else {
            // 次数+1
            //redisUtils.set(key, times + 1, 5 * 60);  // 5分钟过期
            redisUtils.incr(key);
        }
        return times;
    }
}
