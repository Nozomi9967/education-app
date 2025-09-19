package com.education.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Schema(description = "生成验证码时传递的参数DTO")
public class VerifyCodeGenerateDTO implements Serializable {

    @NotBlank(message = "业务关联ID不能为空")
    @Schema(description = "业务关联ID（如用户UUID、手机号等，根据idType区分）",
            example = "13604606152")
    private String businessId;

    @NotNull(message = "业务类型不能为空")
    @Schema(description = "业务类型（1-注册，2-登录，3-注销，4-密码重置）",
            example = "1")
    private Integer businessType;

    @NotNull(message = "ID类型不能为空")
    @Schema(description = "业务ID类型（1-手机号，2-邮箱，3-UUID用户ID）",
            example = "1")
    private Integer idType;

    @NotBlank(message = "接收验证码的手机号不能为空")
    @Schema(description = "用于接收短信验证码的手机号",
            example = "13604606152")
    private String phone;
}
