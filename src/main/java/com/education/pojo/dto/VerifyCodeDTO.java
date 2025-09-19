package com.education.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@Schema(description = "验证码验证DTO，用于接收前端传递的验证信息")
public class VerifyCodeDTO implements Serializable {

    @NotBlank(message = "业务ID不能为空")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "业务ID必须是UUID格式")
    @Schema(description = "业务关联ID（UUID格式，如用户ID）",
            example = "550e8400-e29b-41d4-a716-446655440000",
            pattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    private String id;

    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    @Schema(description = "短信验证码，6位数字",
            example = "654321",
            pattern = "^\\d{6}$")
    private String verifyCode;
}
