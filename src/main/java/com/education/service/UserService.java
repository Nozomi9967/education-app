package com.education.service;

import com.education.pojo.dto.LoginDTO;
import com.education.pojo.dto.RegisDTO;
import com.education.pojo.dto.UserProfileUpdateDTO;
import com.education.result.Result;

import javax.validation.Valid;

public interface UserService {
    /**
     * 用户注册接口
     *
     * @param regisDTO
     */
    Result add(RegisDTO regisDTO);

    /**
     * 用户登录接口
     *
     * @param loginDTO
     * @return
     */
    Result login(LoginDTO loginDTO);

    /**
     * 修改user部分字段
     * @param userProfileUpdateDTO
     * @return
     */
    Result modify(@Valid UserProfileUpdateDTO userProfileUpdateDTO);

    /**
     * 根据userId查询user部分字段
     *
     * @return
     */
    Result search();

    /**
     * 关注接口
     * @param followedId
     * @return
     */
    Result follow(String followedId);

    /**
     * 取消关注接口
     * @param followedId
     * @return
     */
    Result unfollow(String followedId);

    /**
     * 查询关注列表
     * @return
     */
    Result followList();

    /**
     * 根据userId查询关注列表
     * @param followerId
     * @return
     */
    Result followListById(String followerId);

    /**
     * 查询自己粉丝列表
     * @return
     */
    Result fansList();

    /**
     * 查询别人的粉丝列表
     * @param followedId
     * @return
     */
    Result fansListById(String followedId);
}
