package com.education.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Schema(description = "用户注册信息类")
public class RegisDTO implements Serializable {

    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    @Schema(description = "用户手机号码，用于注册和登录", example = "13800138000")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^0-9a-zA-Z]).+$",
            message = "密码必须包含数字、字母和特殊字符")
    @Schema(description = "用户密码，用于账户安全验证，需包含数字、字母和特殊字符", example = "P@ssw0rd123")
    private String password;

    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    @Schema(description = "短信验证码，用于验证手机号码有效性，6位数字", example = "654321")
    private String verifyCode;
}
