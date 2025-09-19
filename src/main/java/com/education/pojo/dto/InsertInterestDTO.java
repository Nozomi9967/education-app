package com.education.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Schema(description = "前端传递的兴趣圈信息类")
public class InsertInterestDTO implements Serializable {

    @NotBlank(message = "兴趣圈名称不能为空")
    @Size(max = 10, message = "兴趣圈名称不能超过10个字符")
    @Schema(description = "兴趣圈名称（最多10个字符）", example = "篮球爱好者")
    private String name;

    @Size(max = 255, message = "头像地址不能超过255个字符")
    @Schema(description = "兴趣圈头像URL（可选，最多255个字符）", example = "https://example.com/avatar/basketball.jpg")
    private String avatar;

    @Size(max = 255, message = "封面地址不能超过255个字符")
    @Schema(description = "兴趣圈封面URL（可选，最多255个字符）", example = "https://example.com/cover/basketball.jpg")
    private String cover;
}
