/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.ocepgen.modules.security.controller;

import io.ocepgen.common.exception.ErrorCode;
import io.ocepgen.common.exception.OcepgenException;
import io.ocepgen.common.utils.IpUtils;
import io.ocepgen.common.utils.Result;
import io.ocepgen.common.validator.AssertUtils;
import io.ocepgen.common.validator.ValidatorUtils;
import io.ocepgen.modules.log.entity.SysLogLoginEntity;
import io.ocepgen.modules.log.enums.LoginOperationEnum;
import io.ocepgen.modules.log.enums.LoginStatusEnum;
import io.ocepgen.modules.log.service.SysLogLoginService;
import io.ocepgen.modules.security.dto.LoginDTO;
import io.ocepgen.modules.security.password.PasswordUtils;
import io.ocepgen.modules.security.service.CaptchaService;
import io.ocepgen.modules.security.service.SysUserTokenService;
import io.ocepgen.modules.security.user.SecurityUser;
import io.ocepgen.modules.security.user.UserDetail;
import io.ocepgen.modules.sys.dto.SysUserDTO;
import io.ocepgen.modules.sys.enums.UserStatusEnum;
import io.ocepgen.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 登录
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@Api(tags = "登录管理")
@AllArgsConstructor
public class LoginController {
    private final SysUserService sysUserService;
    private final SysUserTokenService sysUserTokenService;
    private final CaptchaService captchaService;
    private final SysLogLoginService sysLogLoginService;

    @GetMapping("captcha")
    @ApiOperation(value = "验证码", produces = "application/octet-stream")
    @ApiImplicitParam(paramType = "query", dataType = "string", name = "uuid", required = true)
    public void captcha(HttpServletResponse response, String uuid) throws IOException {
        //uuid不能为空
        AssertUtils.isBlank(uuid, ErrorCode.IDENTIFIER_NOT_NULL);

        //生成验证码
        captchaService.create(response, uuid);
    }

    @PostMapping("login")
    @ApiOperation(value = "登录")
    public Result login(HttpServletRequest request, @RequestBody LoginDTO login) {
        //效验数据
        ValidatorUtils.validateEntity(login);

        //验证码是否正确
        boolean flag = captchaService.validate(login.getUuid(), login.getCaptcha());
        if (!flag) {
            return new Result().error(ErrorCode.CAPTCHA_ERROR, "验证码不正确");
        }

        //用户信息
        SysUserDTO user = sysUserService.getByUsernameAndSchool(login.getUsername(), login.getSchoolId());

        SysLogLoginEntity log = new SysLogLoginEntity();
        log.setOperation(LoginOperationEnum.LOGIN.value());
        log.setCreateDate(new Date());
        log.setIp(IpUtils.getIpAddr(request));
        log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));

        //用户不存在
        if (user == null) {
            log.setStatus(LoginStatusEnum.FAIL.value());
            log.setCreatorName(login.getUsername());
            sysLogLoginService.save(log);

            throw new OcepgenException(ErrorCode.ACCOUNT_PASSWORD_ERROR, "账号不存在");
        }

        // 非超级管理员用户选择的学校不正确
//        if (user.getSuperAdmin().equals(SuperAdminEnum.NO.value()) && !login.getSchoolId().equals(user.getSchoolId())) {
//            log.setStatus(LoginStatusEnum.FAIL.value());
//            log.setCreatorName(login.getUsername());
//            sysLogLoginService.save(log);
//
//            throw new OcepgenException("该学校不存在该用户");
//        }

        //密码错误
        if (!PasswordUtils.matches(login.getPassword(), user.getPassword())) {
            log.setStatus(LoginStatusEnum.FAIL.value());
            log.setCreator(user.getId());
            log.setCreatorName(user.getUsername());
            sysLogLoginService.save(log);

            throw new OcepgenException("账号或密码错误");
        }

        //账号停用
        if (user.getStatus() == UserStatusEnum.DISABLE.value()) {
            log.setStatus(LoginStatusEnum.LOCK.value());
            log.setCreator(user.getId());
            log.setCreatorName(user.getUsername());
            sysLogLoginService.save(log);

            throw new OcepgenException("该账号已停用");
        }

        //登录成功
        log.setStatus(LoginStatusEnum.SUCCESS.value());
        log.setCreator(user.getId());
        log.setCreatorName(user.getUsername());
        sysLogLoginService.save(log);

        return sysUserTokenService.createToken(user.getId());
    }

    @PostMapping("logout")
    @ApiOperation(value = "退出")
    public Result logout(HttpServletRequest request) {
        UserDetail user = SecurityUser.getUser();

        //退出
        sysUserTokenService.logout(user.getId());

        //用户信息
        SysLogLoginEntity log = new SysLogLoginEntity();
        log.setOperation(LoginOperationEnum.LOGOUT.value());
        log.setIp(IpUtils.getIpAddr(request));
        log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        log.setIp(IpUtils.getIpAddr(request));
        log.setStatus(LoginStatusEnum.SUCCESS.value());
        log.setCreator(user.getId());
        log.setCreatorName(user.getUsername());
        log.setCreateDate(new Date());
        sysLogLoginService.save(log);

        return new Result();
    }

}
