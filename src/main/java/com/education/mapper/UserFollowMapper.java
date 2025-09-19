package com.education.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.pojo.entity.UserFollow;
import com.education.pojo.vo.UserRawVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
    List<UserRawVO> followList(String currentId);

    List<UserRawVO> fansList(String currentId);
}
