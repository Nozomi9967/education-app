package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "活动实体类")
@TableName("edu_activity")
public class Activity implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String userId;
    private Integer interestId;
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
    private LocalDateTime deleteTime;

}
