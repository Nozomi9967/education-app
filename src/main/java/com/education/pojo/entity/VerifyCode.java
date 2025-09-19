package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_verify_code") // 对应数据库表名
@Schema(description = "验证码实体类")
public class VerifyCode {

    @TableId(type = IdType.AUTO) // 自增主键
    @Schema(description = "主键ID")
    private Long id;

    @TableField("business_id") // 对应表字段名
    @Schema(description = "业务关联ID（如用户UUID、手机号）")
    private String businessId;

    @TableField("id_type")
    @Schema(description = "业务ID类型（1-手机号，2-邮箱，3-UUID用户ID）")
    private Integer idType;

    @TableField("code")
    @Schema(description = "验证码内容")
    private String code;

    @TableField("business_type")
    @Schema(description = "业务类型（1-注册，2-登录，3-注销）")
    private Integer businessType;

    @TableField("expire_time")
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @TableField("use_status")
    @Schema(description = "使用状态（0-未使用，1-已使用）")
    private Integer useStatus;

    @TableField(value = "create_time", fill = FieldFill.INSERT) // 插入时自动填充
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
