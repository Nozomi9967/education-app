package com.education.controller;

import com.education.pojo.dto.ActivityDTO;
import com.education.pojo.dto.ActivityQueryRequest;
import com.education.pojo.dto.CreateActivityRequest;
import com.education.pojo.dto.UpdateActivityRequest;
import com.education.result.PageResult;
import com.education.result.Result;
import com.education.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理活动相关接口
 */
@RestController
@RequestMapping("/activity")
@Validated
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * 创建活动
     */
    @PostMapping("/create")
    @Operation(summary = "创建活动")
    public Result createActivity(@Valid @RequestBody CreateActivityRequest request) {
        try {
            Long activityId = activityService.createActivity(request);
            return Result.success("创建活动成功");
        } catch (Exception e) {
            return Result.error("创建活动失败: " + e.getMessage());
        }
    }

    /**
     * 更新活动
     */
    @PutMapping("/update")
    @Operation(summary = "更新活动")
    public Result updateActivity(@Valid @RequestBody UpdateActivityRequest request) {
        try {
            boolean updated = activityService.updateActivity(request);
            return Result.success("更新活动成功");
        } catch (Exception e) {
            return Result.error("更新活动失败: " + e.getMessage());
        }
    }

    /**
     * 删除活动（软删除）
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除活动")
    public Result deleteActivity(@PathVariable Long id) {
        try {
            boolean deleted = activityService.deleteActivity(id);
            return Result.success("删除活动成功");
        } catch (Exception e) {
            return Result.error("删除活动失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询活动详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询活动详情")
    public Result<ActivityDTO> getActivityById(@PathVariable Long id) {
        try {
            ActivityDTO activity = activityService.getActivityById(id);
            if (activity == null) {
                return Result.error("活动不存在");
            }
            return Result.success(activity);
        } catch (Exception e) {
            return Result.error("查询活动失败: " + e.getMessage());
        }
    }

    /**
     * 游标分页查询活动列表
     */
    @PostMapping("/page")
    @Operation(summary = "游标分页查询活动列表")
    public Result<PageResult<ActivityDTO>> pageActivities(@RequestParam(required = false) String cursor,
                                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                                          @RequestBody(required = false) ActivityQueryRequest queryRequest) {
        try {
            PageResult<ActivityDTO> pageResult = activityService.pageActivities(cursor, pageSize, queryRequest);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询活动列表失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户创建的活动
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户创建的活动")
    public Result<List<ActivityDTO>> getActivityByUserId(@PathVariable String userId) {
        try {
            List<ActivityDTO> activities = activityService.getActivityByUserId(userId);
            return Result.success(activities);
        } catch (Exception e) {
            return Result.error("查询用户活动失败: " + e.getMessage());
        }
    }

    /**
     * 根据兴趣圈ID查询活动
     */
    @GetMapping("/interest/{interestId}")
    @Operation(summary = "根据兴趣圈ID查询活动")
    public Result<List<ActivityDTO>> getActivityByInterestId(@PathVariable Integer interestId) {
        try {
            List<ActivityDTO> activities = activityService.getActivityByInterestId(interestId);
            return Result.success(activities);
        } catch (Exception e) {
            return Result.error("查询兴趣圈活动失败: " + e.getMessage());
        }
    }

    /**
     * 更新活动状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新活动状态")
    public Result updateActivityStatus(@PathVariable Long id,
                                                @RequestParam Integer status
                                                ) {
        try {
            boolean updated = activityService.updateActivityStatus(id, status);
            return Result.success("更新活动状态成功");
        } catch (Exception e) {
            return Result.error("更新活动状态失败: " + e.getMessage());
        }
    }
}