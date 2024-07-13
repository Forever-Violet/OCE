/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.ocepgen.modules.security.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface CaptchaService {

    /**
     * 图片验证码
     */
    void create(HttpServletResponse response, String uuid) throws IOException;

    /**
     * 验证码效验
     * @param key  redis key
     * @param code  验证码
     * @return  true：成功  false：失败
     */
    boolean validate(String key, String code);

    /**
     * 发送验证码到指定邮箱
     * @param email 邮箱
     * @return  true：成功  false：失败
     */
    boolean postToEmail(String email);

    /**
     * 邮箱验证码验证
     * @param key redis key
     * @param code 验证码
     * @return true：成功  false：失败
     */
    boolean resetPwdCaptchaValidate(String key, String code);

    /**
     * 获取邮箱五分钟内已发送验证码次数
     * @param email 邮箱
     * @return 五分钟内已发送验证码次数
     */
    Integer getEmailPostCaptchaTimes(String email);
}
