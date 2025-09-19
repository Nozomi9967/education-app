package com.education.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用于返回前端粗略的用户信息类，可供关注列表展示")
public class UserRawVO implements Serializable {
    private String id;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String location;
}
