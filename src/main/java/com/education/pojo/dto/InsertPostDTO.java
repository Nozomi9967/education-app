package com.education.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Schema(description = "前端传递的帖子信息类")
public class InsertPostDTO implements Serializable {

    @NotBlank(message = "用户ID不能为空")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "用户ID格式错误（必须是36位UUID，格式如：xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx）")
    @Schema(description = "发布用户ID（36位UUID，带连字符）",
            example = "3e9e2f31-0952-4a2b-bb92-ad1188d709f4")
    private String userId;

    @NotNull(message = "兴趣圈ID不能为空")
    @Schema(description = "所属兴趣圈ID", example = "1")
    private Integer interestId;

    @NotBlank(message = "帖子标题不能为空")
    @Size(max = 10, message = "标题不能超过10个字符")
    @Schema(description = "帖子标题（最多10个字符）", example = "我的第一篇帖子")
    private String title;

    @NotBlank(message = "帖子内容不能为空")
    @Size(max = 1000, message = "内容不能超过1000个字符")
    @Schema(description = "帖子内容（最多1000个字符）", example = "这是帖子的详细内容...")
    private String content;
}
