package io.ocepgen.modules.security.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author roxy
 */
@Data
@ApiModel(value = "邮箱")
public class postCaptchaToEmailDTO {
    @ApiModelProperty(value = "邮箱")
    @NotBlank(message="{sysuser.email.require}")
    private String email;

}
