package com.education.controller;

import com.education.pojo.dto.InsertCourseDTO;
import com.education.pojo.dto.UpdateCourseDTO;
import com.education.pojo.vo.CourseVO;
import com.education.result.Result;
import com.education.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程管理
 */
@RestController
@RequestMapping("/course")
@Slf4j
public class CourseController {

    @Autowired
    private CourseService courseService;


    @PostMapping("/add")
    @Operation(summary = "新增课程接口")
    public Result addCourse(@RequestBody InsertCourseDTO insertCourseDTO) {
        log.info("新增课程: {}", insertCourseDTO);
        return courseService.insert(insertCourseDTO);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除课程接口")
    public Result deleteCourse(
            @Parameter(description = "课程ID") @PathVariable Long id) {
        log.info("删除课程: {}", id);
        return courseService.deleteById(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新课程接口")
    public Result updateCourse(@RequestBody UpdateCourseDTO updateCourseDTO) {
        log.info("更新课程: {}", updateCourseDTO);
        return courseService.update(updateCourseDTO);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "根据ID查询课程详情")
    public Result<CourseVO> getCourseById(
            @Parameter(description = "课程ID") @PathVariable Long id) {
        log.info("查询课程详情: {}", id);
        return courseService.getCourseVOById(id);
    }

    @GetMapping("/interest/{interestId}")
    @Operation(summary = "查询指定兴趣圈的课程")
    public Result<List<CourseVO>> getCoursesByInterestId(
            @Parameter(description = "兴趣圈ID") @PathVariable Integer interestId) {
        log.info("查询兴趣圈 {} 的课程", interestId);
        return courseService.getByInterestId(interestId);
    }

    @GetMapping("/my")
    @Operation(summary = "查询当前用户创建的课程")
    public Result<List<CourseVO>> getMyCourses() {
        log.info("查询当前用户创建的课程");
        return courseService.getByCurrentUser();
    }

    @PostMapping("/join/{courseId}")
    @Operation(summary = "加入课程")
    public Result joinCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        log.info("加入课程: {}", courseId);
        return courseService.joinCourse(courseId);
    }
}
