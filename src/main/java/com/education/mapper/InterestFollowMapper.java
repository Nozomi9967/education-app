package com.education.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.pojo.entity.InterestFollow;
import com.education.pojo.vo.InterestRawVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InterestFollowMapper extends BaseMapper<InterestFollow> {
    List<InterestRawVO> myInterest(String currentUserId);
}
