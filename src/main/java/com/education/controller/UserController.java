package com.education.controller;

import com.education.pojo.dto.DeletionVerifyCodeDTO;
import com.education.pojo.dto.UserProfileUpdateDTO;
import com.education.result.Result;
import com.education.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注销接口
     * @param deletionVerifyCodeDTO
     * @return
     */
    @DeleteMapping("/deletion")
    @Operation(summary = "用户注销接口",description = "对验证码进行校验，校验成功则注销")
    public Result delete(@Valid  @RequestBody DeletionVerifyCodeDTO deletionVerifyCodeDTO) {
        // TODO: 需要先实现验证码部分逻辑
        return Result.success();
    }

    /**
     * 用户个人简介修改接口
     * @param userProfileUpdateDTO
     * @return
     */
    @PutMapping("/profile")
    @Operation(summary = "用户个人简介修改接口")
    public Result modify(@Valid @RequestBody UserProfileUpdateDTO userProfileUpdateDTO) {
        return userService.modify(userProfileUpdateDTO);
    }

    /**
     * 用户个人简介查询接口
     * @return
     */
    @GetMapping("/profile/{userId}")
    @Operation(summary = "用户个人简介查询接口")
    public Result search() {
        return userService.search();
    }

    /**
     * 关注接口
     * @param followedId
     * @return
     */
    @PostMapping("/follow/{followedId}")
    @Operation(summary = "关注接口")
    public Result follow(@PathVariable String followedId) {
        return userService.follow(followedId);
    }

    /**
     * 取消关注接口
     * @param followedId
     * @return
     */
    @DeleteMapping("/follow/{followedId}")
    @Operation(summary = "取消关注接口")
    public Result unfollow(@PathVariable String followedId) {
        return userService.unfollow(followedId);
    }

    /**
     * 查询自己关注列表
     * @return
     */
    @GetMapping("/follow/list")
    @Operation(summary = "查询自己关注列表",description = "最新关注的在前面")
    public Result getFollowList() {
        return userService.followList();
    }

    /**
     * 根据userId查询关注列表
     * @param followerId
     * @return
     */
    @GetMapping("/follow/listById")
    @Operation(summary = "根据userId查询关注列表",description = "用于查询别人的关注列表，排序同上")
    public Result getFollowListById(@RequestParam String followerId) {
        return userService.followListById(followerId);
    }

    /**
     * 查询自己粉丝列表
     * @return
     */
    @GetMapping("/fans/list")
    @Operation(summary = "查询自己粉丝列表")
    public Result getFansList() {
        return userService.fansList();
    }

    /**
     * 查询别人的粉丝列表
     * @param followedId
     * @return
     */
    @GetMapping("/fans/listById")
    @Operation(summary = "查询别人的粉丝列表，根据id")
    public Result getFansListById(@RequestParam String followedId) {
        return userService.fansListById(followedId);
    }



}
