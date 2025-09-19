package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 同义词实体类
 * 用于存储教育平台中的关键词与同义词映射关系
 */
@Data
@Schema(description = "同义词实体类")
@TableName("edu_synonym") // 数据库表名，可根据实际情况调整
public class Synonym implements Serializable {

    private Long id;

    private String keyword;

    private String synonymWord;

    private Double weight = 0.8; // 默认权重0.8

    private Integer status = 1;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime deleteTime;

}
