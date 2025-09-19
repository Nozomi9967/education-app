package com.education.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "返回粗略的兴趣圈信息，用于搜索展示")
public class InterestRawVO {
    private Integer id;
    private String name;
    private String avatar;
    private Integer participateCount;
    @Schema(description = "关注时间")
    private LocalDateTime followTime;
}
