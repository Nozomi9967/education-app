package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Schema(description = "兴趣圈类")
@TableName("edu_interest")
public class Interest {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String userId;
    private String name;
    private String avatar;
    private String cover;
    private Integer participateCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
}
