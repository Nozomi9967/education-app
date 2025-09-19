package com.education.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.pojo.entity.VerifyCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VerifyCodeMapper extends BaseMapper<VerifyCode> {
    // 无需手动编写基本CRUD方法，BaseMapper已提供：
    // insert() / updateById() / selectById() / selectList() 等
}
