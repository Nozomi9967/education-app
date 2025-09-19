package com.education.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.pojo.entity.Interest;
import com.education.pojo.vo.InterestRawVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InterestMapper extends BaseMapper<Interest> {
    List<InterestRawVO> queryByName(String name);
}
