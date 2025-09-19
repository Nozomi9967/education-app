package com.education.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "更新课程DTO")
public class UpdateCourseDTO implements Serializable {

    @Schema(description = "课程ID")
    private Long id;

    @Schema(description = "兴趣分类ID")
    private Integer interestId;

    @Schema(description = "课程名称")
    private String name;

    @Schema(description = "课程价格")
    private BigDecimal price;

    @Schema(description = "课程描述")
    private String description;

    @Schema(description = "适用人群")
    private String suitableFor;
}
