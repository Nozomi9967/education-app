package com.education.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.constant.BusinessConstant;
import com.education.context.BaseContext;
import com.education.exception.BusinessException;
import com.education.mapper.UserFollowMapper;
import com.education.mapper.UserMapper;
import com.education.pojo.dto.LoginDTO;
import com.education.pojo.dto.RegisDTO;
import com.education.pojo.dto.UserProfileUpdateDTO;
import com.education.pojo.dto.VerifyCodeDTO;
import com.education.pojo.entity.User;
import com.education.pojo.entity.UserFollow;
import com.education.pojo.vo.UserLoginVO;
import com.education.pojo.vo.UserProfileVO;
import com.education.pojo.vo.UserRawVO;
import com.education.properties.JwtProperties;
import com.education.result.Result;
import com.education.service.UserService;
import com.education.service.VerifyCodeService;
import com.education.util.JwtUtil;
import com.education.util.NumberPool;
import com.education.util.PasswordEncoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;
    
    @Autowired
    private VerifyCodeService verifyCodeService;


    @Autowired
    private NumberPool numberPool;

    /**
     * 用户注册接口
     *
     * @param regisDTO
     * @return
     */
    @Override
    public Result add(RegisDTO regisDTO) {
        String verifyCode = regisDTO.getVerifyCode();
        String phone = regisDTO.getPhone();
        String password = regisDTO.getPassword();

        // 1.检验验证码 TODO
        VerifyCodeDTO verifyCodeDTO = new VerifyCodeDTO();
        verifyCodeDTO.setId(regisDTO.getPhone());
        verifyCodeDTO.setVerifyCode(regisDTO.getVerifyCode());
        Result result = verifyCodeService.validateCode(verifyCodeDTO, 1);
        if(result != null){
            return result;
        }

        // 2.检验手机号是否已注册
        User isExist = userMapper.selectOne(
                new QueryWrapper<User>().eq("phone", phone)
        );
        if (isExist != null)
            throw new BusinessException(BusinessConstant.PHONE_ALREADY_EXIST);

        // 3.加密密码
        String encodePassword = passwordEncoderUtil.encodePassword(password);

        // 4.生成id
        String id = UUID.randomUUID().toString();

        // 5.生成默认随机用户名
        Integer randomEightNum = numberPool.getNumber();
        String username = "user" + randomEightNum;

        // 6.填充User剩余字段
        User user = new User();
        user.setId(id);
        user.setPhone(phone);
        user.setPassword(encodePassword);
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setUsername(username);
        user.setNickname(BusinessConstant.DEFAULT_NICKNAME);

        // 7.使用MP的insert方法插入数据
        int rows = userMapper.insert(user);

        // 8.判断插入结果（可选，根据业务需求）
        if (rows != 1) {
            throw new BusinessException(BusinessConstant.REGIS_ERROR);
        }

         // 生成jwt令牌
            HashMap<String, Object> claims = new HashMap<>();
            claims.put(BusinessConstant.USER_ID,id);
            String jwt = JwtUtil.createJWT(
                    jwtProperties.getSecretKey(),
                    jwtProperties.getTtl(),
                    claims
            );

            // 将userId存入当前线程中
            BaseContext.setCurrentId(id);
        return Result.success(id);
    }

    /**
     * 用户登录接口
     *
     * @param loginDTO
     * @return
     */
    @Override
    public Result login(LoginDTO loginDTO) {
        // 1.判断用户是否存在
        User user = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", loginDTO.getUsername())
                        .isNull("delete_time")
        );
        if (user==null) {
            throw new BusinessException(BusinessConstant.USER_NOT_EXIST);
        }

        // 2.对比密码
        boolean isMatch = passwordEncoderUtil.matchesPassword(loginDTO.getPassword(), user.getPassword());
        if (!isMatch) {
            throw new BusinessException(BusinessConstant.PASSWORD_NOT_MATCH);
        }

        // 3.封装UserLoginVO
        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setUsername(user.getUsername());
        userLoginVO.setNickname(user.getNickname());
        userLoginVO.setId(user.getId());
        userLoginVO.setAvatar(user.getAvatar());

        // 生成jwt令牌
            HashMap<String, Object> claims = new HashMap<>();
            claims.put(BusinessConstant.USER_ID,userLoginVO.getId());
            String jwt = JwtUtil.createJWT(
                    jwtProperties.getSecretKey(),
                    jwtProperties.getTtl(),
                    claims
            );
            userLoginVO.setToken(jwt);

            // 将userId存入当前线程中
            BaseContext.setCurrentId(userLoginVO.getId());

        return Result.success(userLoginVO);
    }

    /**
     * 修改user部分字段
     * @param userProfileUpdateDTO
     * @return
     */
    @Override
    public Result modify(UserProfileUpdateDTO userProfileUpdateDTO) {
        // 1.检验该用户是否存在
        User user = userMapper.selectById(userProfileUpdateDTO.getId());
        if(user == null || user.getDeleteTime() == null) {
            throw new BusinessException(BusinessConstant.USER_NOT_EXIST);
        }

        // 2.复制更新字段到user实体
        BeanUtils.copyProperties(userProfileUpdateDTO, user);
        user.setUpdateTime(LocalDateTime.now());

        // 3.执行更新
        int rows = userMapper.updateById(user);
        if (rows == 0) {
            log.warn("用户资料更新失败，用户ID：{}", userProfileUpdateDTO.getId());
            return Result.error(BusinessConstant.UPDATE_FAIL);
        }

        log.info("用户资料更新成功，用户ID：{}", userProfileUpdateDTO.getId());
        return Result.success("资料更新成功");
    }

    /**
     * 根据userId查询user部分字段
     *
     * @return
     */
    @Override
    public Result search() {
        // 1.检验用户id
        String currentId = BaseContext.getCurrentId();
        if(currentId == null) {
            return Result.error(BusinessConstant.LOGIN_EXPIRED);
        }

        // 2.查询
        User user = userMapper.selectById(currentId);
        if(user == null || user.getDeleteTime() == null) {
            return Result.error(BusinessConstant.USER_NOT_EXIST);
        }

        // 3.转换为UserProfileVO
        UserProfileVO userProfileVO = new UserProfileVO();
        BeanUtils.copyProperties(user, userProfileVO);

        return Result.success(userProfileVO);
    }

    /**
     * 关注接口
     * @param followedId
     * @return
     */
    @Override
    public Result follow(String followedId) {
        // 1.检验
        String currentId = BaseContext.getCurrentId();
        if(followedId == null || currentId == null) {
            return Result.error(BusinessConstant.FOLLOW_ERROR);
        }

        // 2.重复性检验
        boolean isExist = userFollowMapper.exists(
                new QueryWrapper<UserFollow>().eq("followed_id", followedId)
                        .eq("follower_id", currentId)
        );
        if(isExist) {
            return Result.error(BusinessConstant.FOLLOW_DUPLICATE);
        }

        // 2.插入数据库
        UserFollow userFollow = new UserFollow();
        userFollow.setFollowedId(followedId);
        userFollow.setFollowerId(currentId);
        userFollow.setCreateTime(LocalDateTime.now());
        int rows = userFollowMapper.insert(userFollow);
        if (rows == 0) {
            return Result.error(BusinessConstant.FOLLOW_ERROR);
        }
        return Result.success();
    }

    /**
     * 取消关注接口
     * @param followedId
     * @return
     */
    @Override
    public Result unfollow(String followedId) {
        String currentId = BaseContext.getCurrentId();
        if(followedId == null || currentId == null) {
            return Result.error(BusinessConstant.UNFOLLOW_ERROR);
        }

        // 检验是否已关注
        QueryWrapper<UserFollow> wrapper = new QueryWrapper<UserFollow>().eq("followed_id", followedId)
                .eq("follower_id", currentId);
        boolean isExist = userFollowMapper.exists(
                wrapper
        );
        if(!isExist) {
            return Result.error(BusinessConstant.UNFOLLOW_ERROR);
        }

        int rows = userFollowMapper.delete(wrapper);
        if (rows == 0) {
            return Result.error(BusinessConstant.UNFOLLOW_ERROR);
        }
        return Result.success();
    }

    /**
     * 查询关注列表
     * @return
     */
    @Override
    public Result followList() {
        String currentId = BaseContext.getCurrentId();
        if(currentId == null) {
            return Result.error(BusinessConstant.LOGIN_EXPIRED);
        }

        List<UserRawVO> userRawVOS = userFollowMapper.followList(currentId);

        return Result.success(userRawVOS);
    }

    /**
     * 根据userId查询关注列表
     * @param followerId
     * @return
     */
    @Override
    public Result followListById(String followerId) {
        if(followerId == null) {
            return Result.error(BusinessConstant.PARAM_IS_NULL);
        }

        List<UserRawVO> userRawVOS = userFollowMapper.followList(followerId);

        return Result.success(userRawVOS);
    }

    /**
     * 查询自己粉丝列表
     * @return
     */
    @Override
    public Result fansList() {
        String currentId = BaseContext.getCurrentId();
        if(currentId == null) {
            return Result.error(BusinessConstant.LOGIN_EXPIRED);
        }

        List<UserRawVO> userRawVOS = userFollowMapper.fansList(currentId);
        return Result.success(userRawVOS);
    }

    /**
     * 查询别人的粉丝列表
     * @param followedId
     * @return
     */
    @Override
    public Result fansListById(String followedId) {
        if(followedId == null) {
            return Result.error(BusinessConstant.PARAM_IS_NULL);
        }
        List<UserRawVO> userRawVOS = userFollowMapper.fansList(followedId);
        return Result.success(userRawVOS);
    }
}
