package com.education.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "插入课程dto类")
public class InsertCourseDTO implements Serializable {
    private Integer interestId;
    private String name;
    private BigDecimal price;
    private String description;
    private String suitableFor;
}
