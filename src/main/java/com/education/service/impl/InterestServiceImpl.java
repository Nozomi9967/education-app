package com.education.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.constant.BusinessConstant;
import com.education.context.BaseContext;
import com.education.mapper.InterestFollowMapper;
import com.education.mapper.InterestMapper;
import com.education.pojo.dto.InsertInterestDTO;
import com.education.pojo.entity.Interest;
import com.education.pojo.entity.InterestFollow;
import com.education.pojo.vo.InterestRawVO;
import com.education.result.Result;
import com.education.service.InterestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class InterestServiceImpl implements InterestService {

    @Autowired
    private InterestMapper interestMapper;

    @Autowired
    private InterestFollowMapper interestFollowMapper;

    /**
     * 新增兴趣圈
     * @param insertInterestDTO
     * @return
     */
    @Override
    public Result insert(InsertInterestDTO insertInterestDTO) {

        // 1.判断名字是否重复
        boolean isExist = interestMapper.exists(
                new QueryWrapper<Interest>().eq("name", insertInterestDTO.getName())
        );
        if (isExist) {
            return Result.error(BusinessConstant.NAME_ALREADY_EXIST);
        }

        // 2.转换对象
        Interest interest = new Interest();
        BeanUtils.copyProperties(insertInterestDTO, interest);
        interest.setUserId(BaseContext.getCurrentId());
        interest.setStatus(BusinessConstant.ACTIVE_STATUS);
        interest.setParticipateCount(BusinessConstant.DEFAULT_COUNT);
        LocalDateTime now = LocalDateTime.now();
        interest.setCreateTime(now);
        interest.setUpdateTime(now);
        interest.setDeleteTime(null);

        // 3.存入数据库
        int rows = interestMapper.insert(interest);
        if (rows == 0) {
            return Result.error(BusinessConstant.INSERT_FAIL);
        }
        return Result.success();
    }

    /**
     * 删除兴趣圈
     * @param interestId
     * @return
     */
    @Override
    public Result remove(Integer interestId) {
        // 1.获取userId
        String currentUserId = BaseContext.getCurrentId();

        // 2.判断是否为发布者
        boolean isExist = interestMapper.exists(
                new QueryWrapper<Interest>().eq("id", interestId)
                        .eq("user_id", currentUserId)
        );
        if (!isExist) {
            return Result.error(BusinessConstant.INTEREST_NOT_EXIST);
        }

        // 3.生成更新对象
        Interest interest = new Interest();
        interest.setId(interestId);
        interest.setStatus(BusinessConstant.INACTIVE_STATUS);
        LocalDateTime now = LocalDateTime.now();
        interest.setUpdateTime(now);
        interest.setDeleteTime(now);

        // 4.更新数据库
        int rows = interestMapper.updateById(interest);
        if (rows == 0) {
            return Result.error(BusinessConstant.DELETE_FAIL);
        }
        return Result.success();
    }

    /**
     * 根据兴趣圈名查询
     * @param name
     * @return
     */
    @Override
    public Result queryByName(String name) {
//        未加followTime版本
//        // 1.查询
//        QueryWrapper<Interest> queryWrapper = new QueryWrapper<Interest>()
//                .eq("status", BusinessConstant.ACTIVE_STATUS)
//                .isNull("delete_time")
//                .orderByDesc("participate_count");
//
//        // 只有当 name 不为 null 且不为空字符串时，才添加 like 条件
//        if (name != null && !name.isEmpty()) {
//            queryWrapper.like("name", name);
//        }
//        List<Interest> interests = interestMapper.selectList(queryWrapper);
//
//
//        // 2.转换类型
//        List<InterestRawVO> interestRawVOList = new ArrayList<>();
//        try {
//            for(Interest interest : interests) {
//                InterestRawVO interestRawVO = new InterestRawVO();
//                BeanUtils.copyProperties(interest, interestRawVO);
//                interestRawVOList.add(interestRawVO);
//            }
//        } catch (BusinessException e) {
//            log.error("Interest：queryByName：{}",e.getMessage());
//            throw new BusinessException(BusinessConstant.SEARCH_FAIL);
//        } catch (RuntimeException e){
//            log.error(e.getMessage());
//            throw e;
//        }

        List<InterestRawVO> interestRawVOList = interestMapper.queryByName(name);


        return Result.success(interestRawVOList);
    }



    /**
     * 获取自己所关注的兴趣圈
     * @return
     */
    @Override
    public Result myInterest() {
        List<InterestRawVO> list = interestFollowMapper.myInterest(BaseContext.getCurrentId());
        return Result.success(list);
    }

    /**
     * 关注兴趣圈
     * @param interestId
     * @return
     */
    @Override
    public Result followInterest(Integer interestId) {
        // 1.判断兴趣圈id有效
        boolean isExist = interestMapper.exists(
                new QueryWrapper<Interest>().eq("id", interestId)
                        .eq("status", BusinessConstant.ACTIVE_STATUS)
                        .isNull("delete_time")
        );
        if (!isExist) {
            return Result.error(BusinessConstant.INTEREST_NOT_EXIST);
        }

        // 2.存入数据库
        InterestFollow interestFollow = new InterestFollow();
        interestFollow.setInterestId(interestId);
        interestFollow.setUserId(BaseContext.getCurrentId());
        interestFollow.setCreateTime(LocalDateTime.now());

        int rows = interestFollowMapper.insert(interestFollow);
        if (rows == 0) {
            return Result.error(BusinessConstant.INSERT_FAIL);
        }

        return Result.success();
    }
}
