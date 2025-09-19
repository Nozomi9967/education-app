package com.education.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

// Activity DTO
@Data
public class ActivityDTO {
    private Long id;
    private String userId;
    private Integer interestId;
    private String interestName; // 关联的兴趣圈名称
    private String name;
    private String cover;
    private String theme;
    private String highlight;
    private String time;
    private String location;
    private String participateMethod;
    private String eventFlow;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}