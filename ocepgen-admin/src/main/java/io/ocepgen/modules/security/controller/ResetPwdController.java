package io.ocepgen.modules.security.controller;

import io.ocepgen.common.exception.ErrorCode;
import io.ocepgen.common.exception.OcepgenException;
import io.ocepgen.common.redis.RedisKeys;
import io.ocepgen.common.utils.Result;
import io.ocepgen.common.validator.ValidatorUtils;
import io.ocepgen.modules.security.dto.ResetPwdDTO;
import io.ocepgen.modules.security.dto.postCaptchaToEmailDTO;
import io.ocepgen.modules.security.service.CaptchaService;
import io.ocepgen.modules.sys.dto.SysUserDTO;
import io.ocepgen.modules.sys.enums.UserStatusEnum;
import io.ocepgen.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author roxy
 */
@RestController
@Api(tags = "重置密码管理")
@AllArgsConstructor
public class ResetPwdController {

    @Resource
    SysUserService sysUserService;

    @Resource
    CaptchaService captchaService;

    private final Integer MaxPostCaptchaTimes = 5;



    @PostMapping("resetPassword")
    @ApiOperation(value = "重置密码")
    public Result resetPwd(HttpServletRequest request, @RequestBody ResetPwdDTO resetPwd) {
        //效验数据
        ValidatorUtils.validateEntity(resetPwd);

        //用户信息
        SysUserDTO user = sysUserService.getByEmail(resetPwd.getEmail());

        //用户不存在，因为验证码发送后，用户还是有可能改变邮箱
        if (user == null) {
            throw new OcepgenException("该邮箱未绑定用户");
        }

        //账号停用
        if (user.getStatus() == UserStatusEnum.DISABLE.value()) {
            throw new OcepgenException(ErrorCode.ACCOUNT_DISABLE);
        }

        //验证码是否正确
        boolean flag = captchaService.resetPwdCaptchaValidate(RedisKeys.getCaptchaKey(resetPwd.getEmail()),
                resetPwd.getCaptcha());

        if (!flag) {
            return new Result().error(ErrorCode.CAPTCHA_ERROR);
        }

        sysUserService.updatePassword(user.getId(), resetPwd.getPassword());

        //修改成功
        return new Result().success(0, "密码重置成功");
    }

    @PostMapping("postCaptchaToEmail")
    @ApiOperation(value = "发送验证码到邮箱")
    public Result postCaptchaToEmail(HttpServletRequest request, @RequestBody postCaptchaToEmailDTO postDTO) {
        //效验数据
        ValidatorUtils.validateEntity(postDTO);

        //用户信息
        SysUserDTO user = sysUserService.getByEmail(postDTO.getEmail());

        //用户不存在
        if (user == null) {
            throw new OcepgenException("该邮箱未绑定用户");
        }

        //账号停用
        if (user.getStatus() == UserStatusEnum.DISABLE.value()) {
            throw new OcepgenException(ErrorCode.ACCOUNT_DISABLE);
        }

        // 限制验证码发送次数，5分钟内最多发送5次
        Integer times = captchaService.getEmailPostCaptchaTimes(postDTO.getEmail());
        if (times >= MaxPostCaptchaTimes) {
            throw new OcepgenException("验证码发送频繁，邮箱锁定，请五分钟后再试");
        }

        captchaService.postToEmail(postDTO.getEmail());

        //修改成功
        return new Result().success(0,
                "验证码发送成功，五分钟内剩余发送次数：" + (MaxPostCaptchaTimes - times - 1) + "次");
    }

}
