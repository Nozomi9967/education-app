package com.education.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.pojo.dto.InsertCourseDTO;
import com.education.pojo.dto.UpdateCourseDTO;
import com.education.pojo.entity.Course;
import com.education.pojo.vo.CourseJoinVO;
import com.education.pojo.vo.CourseVO;
import com.education.result.Result;

import java.util.List;

public interface CourseService extends IService<Course> {

    /**
     * 新增课程
     */
    Result insert(InsertCourseDTO insertCourseDTO);

    /**
     * 根据ID删除课程（逻辑删除）
     */
    Result deleteById(Long id);

    /**
     * 更新课程信息
     */
    Result update(UpdateCourseDTO updateCourseDTO);

    /**
     * 根据ID查询课程详情
     */
    Course getById(Long id);

    /**
     * 查询指定兴趣圈的所有课程
     */
    Result<List<CourseVO>> getByInterestId(Integer interestId);

    /**
     * 查询当前用户创建的所有课程
     */
    Result<List<CourseVO>> getByCurrentUser();

    /**
     * 加入课程（参与人数+1）
     */
    Result joinCourse(Long courseId);

    Result<CourseVO> getCourseVOById(Long id);

    Result<List<CourseJoinVO>> myJoinCourse();
}
