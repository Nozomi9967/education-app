package com.education.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.constant.BusinessConstant;
import com.education.context.BaseContext;
import com.education.mapper.CourseMapper;
import com.education.mapper.UserCourseMapper;
import com.education.pojo.dto.InsertCourseDTO;
import com.education.pojo.dto.UpdateCourseDTO;
import com.education.pojo.entity.Course;
import com.education.pojo.entity.UserCourse;
import com.education.pojo.vo.CourseJoinVO;
import com.education.pojo.vo.CourseVO;
import com.education.result.Result;
import com.education.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private UserCourseMapper userCourseMapper;

    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    @Override
    @Transactional
    public Result insert(InsertCourseDTO insertCourseDTO) {
        // 获取当前用户ID
        String userId = BaseContext.getCurrentId();

        // 转换DTO为实体
        Course course = new Course();
        BeanUtils.copyProperties(insertCourseDTO, course);

        // 设置创建人ID
        course.setUserId(userId);
        // 初始化参与人数为0
        course.setParticipateCount(0);
        // 设置时间
        LocalDateTime now = LocalDateTime.now();
        course.setCreateTime(now);
        course.setUpdateTime(now);
        // 初始删除时间为null（未删除）
        course.setDeleteTime(null);

        // 保存课程
        boolean save = save(course);
        if (save) {
            return Result.success("课程创建成功");
        } else {
            return Result.error("课程创建失败");
        }
    }

    @Override
    @Transactional
    public Result deleteById(Long id) {
        // 获取当前用户ID
        String userId = BaseContext.getCurrentId();

        // 检查课程是否存在且属于当前用户
        Course course = getById(id);
        if (course == null || course.getDeleteTime() != null) {
            return Result.error("课程不存在");
        }

        if (!course.getUserId().equals(userId)) {
            return Result.error("没有权限删除该课程");
        }

        // 逻辑删除：设置删除时间
        course.setDeleteTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        boolean update = updateById(course);

        if (update) {
            return Result.success("课程删除成功");
        } else {
            return Result.error("课程删除失败");
        }
    }

    @Override
    @Transactional
    public Result update(UpdateCourseDTO updateCourseDTO) {
        // 获取当前用户ID
        String userId = BaseContext.getCurrentId();

        // 检查课程是否存在且属于当前用户
        Long courseId = updateCourseDTO.getId();
        log.info("id:",courseId.toString());
        Course course = getById(courseId);
        if (course == null || course.getDeleteTime() != null) {
            return Result.error("课程不存在");
        }

        if (!course.getUserId().equals(userId)) {
            return Result.error("没有权限修改该课程");
        }

        // 更新课程信息
        BeanUtils.copyProperties(updateCourseDTO, course);
        course.setUpdateTime(LocalDateTime.now());

        boolean update = updateById(course);
        if (update) {
            return Result.success("课程更新成功");
        } else {
            return Result.error("课程更新失败");
        }
    }

    @Override
    public Course getById(Long id) {
        Course course = baseMapper.selectById(id);
        if (course == null) {
            log.error("课程不存在");
            return null;
        }
        return course;
    }

    @Override
    public Result<List<CourseVO>> getByInterestId(Integer interestId) {
        List<CourseVO> courseVOList = baseMapper.selectCourseVOByInterestId(interestId);
        return Result.success(courseVOList);
    }

    @Override
    public Result<List<CourseVO>> getByCurrentUser() {
        String userId = BaseContext.getCurrentId();
        List<CourseVO> courseVOList = baseMapper.selectCourseVOByUserId(userId);
        return Result.success(courseVOList);
    }

    @Override
    @Transactional
    public Result joinCourse(Long courseId) {
        // 获取当前用户ID
        String userId = BaseContext.getCurrentId();

        // 检查课程是否存在
        Course course = getById(courseId);
        if (course == null || course.getDeleteTime() != null) {
            return Result.error("课程不存在");
        }

        // 检验是否选过
        boolean isExist = userCourseMapper.exists(
                new QueryWrapper<UserCourse>().eq("user_id", userId).eq("course_id", courseId)
        );
        if (isExist) {
            return Result.error("已订阅过该课程");
        }

        // 参与人数+1
        course.setParticipateCount(course.getParticipateCount() + 1);
        course.setUpdateTime(LocalDateTime.now());

        UserCourse userCourse = new UserCourse();
        userCourse.setUserId(userId);
        userCourse.setCourseId(courseId);
        userCourse.setCreateTime(LocalDateTime.now());
        int rows = userCourseMapper.insert(userCourse);
        if (rows <= 0) {
            return Result.error("加入课程失败");
        }

        boolean update = updateById(course);
        if (update) {
            return Result.success("加入课程成功");
        } else {
            return Result.error("加入课程失败");
        }
    }

    @Override
    public Result<CourseVO> getCourseVOById(Long id) {
        boolean isExist = courseMapper.exists(
                new QueryWrapper<Course>().eq("id", id)
                        .isNull("delete_time")
        );
        if (!isExist) {
            return Result.error(BusinessConstant.COURSE_NOT_EXIST);
        }

        CourseVO courseVOById = courseMapper.getCourseVOById(id);
        return Result.success(courseVOById);
    }

    @Override
    public Result<List<CourseJoinVO>> myJoinCourse() {
        String userId = BaseContext.getCurrentId();
        return Result.success(courseMapper.myJoinCourse(userId));
    }
}
