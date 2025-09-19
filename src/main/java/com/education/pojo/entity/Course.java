package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "课程实体类")
@TableName("edu_course")
public class Course implements Serializable {

    @Schema(description = "课程ID")
    private Long id;

    @Schema(description = "创建人ID")
    @TableField("user_id")
    private String userId;

    @Schema(description = "兴趣分类ID")
    @TableField("interest_id")
    private Integer interestId;

    @Schema(description = "课程名称")
    private String name;

    @Schema(description = "课程价格")
    private BigDecimal price;

    @Schema(description = "参与人数")
    @TableField("participate_count")
    private Integer participateCount;

    @Schema(description = "课程描述")
    private String description;

    @Schema(description = "适用人群")
    @TableField("suitable_for")
    private String suitableFor;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @Schema(description = "删除时间，null表示未删除")
    @TableField("delete_time")
    private LocalDateTime deleteTime;
}
