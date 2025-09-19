package com.education.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.pojo.dto.ActivityDTO;
import com.education.pojo.dto.ActivityQueryRequest;
import com.education.pojo.dto.CreateActivityRequest;
import com.education.pojo.dto.UpdateActivityRequest;
import com.education.pojo.entity.Activity;
import com.education.result.PageResult;

import java.util.List;

/**
 * ActivityService接口
 */
public interface ActivityService extends IService<Activity> {

    /**
     * 创建活动
     */
    Long createActivity(CreateActivityRequest request);

    /**
     * 更新活动
     */
    boolean updateActivity(UpdateActivityRequest request);

    /**
     * 删除活动（软删除）
     */
    boolean deleteActivity(Long id);

    /**
     * 根据ID查询活动详情
     */
    ActivityDTO getActivityById(Long id);

    /**
     * 游标分页查询活动列表
     */
    PageResult<ActivityDTO> pageActivities(String cursor, Integer pageSize, ActivityQueryRequest queryRequest);

    /**
     * 根据用户ID查询用户创建的活动
     */
    List<ActivityDTO> getActivityByUserId(String userId);

    /**
     * 根据兴趣圈ID查询活动
     */
    List<ActivityDTO> getActivityByInterestId(Integer interestId);

    /**
     * 更新活动状态
     */
    boolean updateActivityStatus(Long id, Integer status);
}