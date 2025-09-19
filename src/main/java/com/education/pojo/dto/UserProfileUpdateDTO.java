package com.education.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Schema(description = "用户个人资料更新类")
public class UserProfileUpdateDTO implements Serializable {

    @NotBlank(message = "用户ID不能为空")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "用户ID格式错误（必须为UUID）")
    @Schema(description = "用户ID，必填", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "昵称，1-8个字符", example = "张三")
    private String nickname;

    @Schema(description = "性别，0-未知 1-男 2-女", example = "1")
    @Min(value = 0, message = "性别不能小于0")
    @Max(value = 2, message = "性别不能大于2")
    private Integer gender;  // 改为Integer类型

    @Schema(description = "生日，格式yyyy-MM-dd", example = "1990-01-01")
    private LocalDateTime birthday;

    @Schema(description = "所在地，不超过100个字符", example = "北京市海淀区")
    private String location;
}
