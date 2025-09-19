package com.education.service;

import com.education.pojo.dto.InsertInterestDTO;
import com.education.result.Result;

import javax.validation.Valid;

public interface InterestService {
    /**
     * 新增兴趣圈
     * @param insertInterestDTO
     * @return
     */
    Result insert(InsertInterestDTO insertInterestDTO);

    /**
     * 删除兴趣圈
     * @param interestId
     * @return
     */
    Result remove(Integer interestId);

    /**
     * 根据兴趣圈名查询
     * @param name
     * @return
     */
    Result queryByName(String name);

    /**
     * 获取自己所关注的兴趣圈
     * @return
     */
    Result myInterest();

    /**
     * 关注兴趣圈
     * @param interestId
     * @return
     */
    Result followInterest(Integer interestId);
}
