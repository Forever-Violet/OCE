package io.ocepgen.modules.security.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
/**
 * @author roxy
 */
@Data
@ApiModel(value = "重置密码表单")
public class ResetPwdDTO  implements Serializable {

    @ApiModelProperty(value = "密码")
    @NotBlank(message="{sysuser.password.require}")
    private String password;

    @ApiModelProperty(value = "验证码")
    @NotBlank(message="{sysuser.captcha.require}")
    private String captcha;

    @ApiModelProperty(value = "邮箱")
    @NotBlank(message="{sysuser.email.require}")
    private String email;

}
