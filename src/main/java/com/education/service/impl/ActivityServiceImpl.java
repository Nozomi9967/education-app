package com.education.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.constant.BusinessConstant;
import com.education.context.BaseContext;
import com.education.exception.BusinessException;
import com.education.mapper.ActivityMapper;
import com.education.pojo.dto.ActivityDTO;
import com.education.pojo.dto.ActivityQueryRequest;
import com.education.pojo.dto.CreateActivityRequest;
import com.education.pojo.dto.UpdateActivityRequest;
import com.education.pojo.entity.Activity;
import com.education.result.PageResult;
import com.education.service.ActivityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ActivityServiceImpl实现类
 */
@Service
@Transactional
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public Long createActivity(CreateActivityRequest request) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(request, activity);
        activity.setStatus(1); // 默认启用状态
        activity.setUserId(BaseContext.getCurrentId());
        LocalDateTime now = LocalDateTime.now();
        activity.setCreateTime(now);
        activity.setUpdateTime(now);

        boolean saved = this.save(activity);
        if (!saved) {
            throw new RuntimeException("创建活动失败");
        }

        return activity.getId();
    }

    @Override
    public boolean updateActivity(UpdateActivityRequest request) {
        Activity activity = this.getById(request.getId());

        if(!Objects.equals(activity.getUserId(), BaseContext.getCurrentId())){
            throw new BusinessException("无权限");
        }

        if (activity == null || activity.getDeleteTime() != null) {
            throw new BusinessException("活动不存在");
        }

        // 只更新非空字段
        Activity updateActivity = new Activity();
        updateActivity.setUpdateTime(LocalDateTime.now());
        updateActivity.setId(request.getId());

        if (request.getInterestId() != null) {
            updateActivity.setInterestId(request.getInterestId());
        }
        if (StringUtils.hasText(request.getName())) {
            updateActivity.setName(request.getName());
        }
        if (request.getCover() != null) {
            updateActivity.setCover(request.getCover());
        }
        if (StringUtils.hasText(request.getTheme())) {
            updateActivity.setTheme(request.getTheme());
        }
        if (request.getHighlight() != null) {
            updateActivity.setHighlight(request.getHighlight());
        }
        if (StringUtils.hasText(request.getTime())) {
            updateActivity.setTime(request.getTime());
        }
        if (StringUtils.hasText(request.getLocation())) {
            updateActivity.setLocation(request.getLocation());
        }
        if (StringUtils.hasText(request.getParticipateMethod())) {
            updateActivity.setParticipateMethod(request.getParticipateMethod());
        }
        if (request.getEventFlow() != null) {
            updateActivity.setEventFlow(request.getEventFlow());
        }

        return this.updateById(updateActivity);
    }

    @Override
    public boolean deleteActivity(Long id) {
        Activity activity = this.getById(id);
        if (activity == null || activity.getDeleteTime() != null) {
            throw new BusinessException("活动不存在");
        }

        // 检查是否是活动创建者
        if (!activity.getUserId().equals(BaseContext.getCurrentId())) {
            throw new BusinessException("无权限删除此活动");
        }

        Activity updateActivity = new Activity();
        updateActivity.setId(id);
        updateActivity.setDeleteTime(LocalDateTime.now());

        return this.updateById(updateActivity);
    }

    @Override
    public ActivityDTO getActivityById(Long id) {
        return activityMapper.selectActivityById(id);
    }

    @Override
    public PageResult<ActivityDTO> pageActivities(String cursor, Integer pageSize, ActivityQueryRequest queryRequest) {
        // 1. 限制pageSize最大值，防止恶意请求
        pageSize = Math.min(pageSize, BusinessConstant.MAX_PAGE_SIZE);

        // 2. 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("cursor", cursor);
        params.put("pageSize", pageSize);

        // 添加查询条件
        if (queryRequest != null) {
            if (StringUtils.hasText(queryRequest.getUserId())) {
                params.put("userId", queryRequest.getUserId());
            }
            if (queryRequest.getInterestId() != null) {
                params.put("interestId", queryRequest.getInterestId());
            }
            if (StringUtils.hasText(queryRequest.getName())) {
                params.put("name", queryRequest.getName());
            }
            if (queryRequest.getStatus() != null) {
                params.put("status", queryRequest.getStatus());
            }
            if (queryRequest.getStartTime() != null) {
                params.put("startTime", queryRequest.getStartTime());
            }
            if (queryRequest.getEndTime() != null) {
                params.put("endTime", queryRequest.getEndTime());
            }
        }

        // 3. 调用mapper查询当前页数据
        List<ActivityDTO> activityList = activityMapper.pageActivities(params);

        // 4. 计算下一页游标
        Long nextCursor = null;
        if (activityList.size() == pageSize) {
            // 如果当前页数据量等于pageSize，说明可能还有下一页
            nextCursor = activityList.get(activityList.size() - 1).getId();
        }

        // 5. 封装分页结果
        PageResult<ActivityDTO> pageResult = new PageResult<>();
        pageResult.setRecords(activityList);
        pageResult.setCursor(nextCursor);

        return pageResult;
    }

    @Override
    public List<ActivityDTO> getActivityByUserId(String userId) {
        return activityMapper.selectActivityByUserId(userId);
    }

    @Override
    public List<ActivityDTO> getActivityByInterestId(Integer interestId) {
        return activityMapper.selectActivityByInterestId(interestId);
    }

    @Override
    public boolean updateActivityStatus(Long id, Integer status) {
        Activity activity = this.getById(id);
        if (activity == null || activity.getDeleteTime() != null) {
            throw new BusinessException("活动不存在");
        }

        // 检查是否是活动创建者
        if (!activity.getUserId().equals(BaseContext.getCurrentId())) {
            throw new BusinessException("无权限修改此活动状态");
        }

        Activity updateActivity = new Activity();
        updateActivity.setId(id);
        updateActivity.setStatus(status);
        updateActivity.setUpdateTime(LocalDateTime.now());

        return this.updateById(updateActivity);
    }
}