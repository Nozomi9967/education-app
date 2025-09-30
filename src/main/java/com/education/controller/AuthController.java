package com.education.controller;

import com.education.pojo.dto.LoginDTO;
import com.education.pojo.dto.RegisDTO;
import com.education.pojo.dto.VerifyCodeGenerateDTO;
import com.education.result.Result;
import com.education.service.UserService;
import com.education.service.VerifyCodeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 登录注册管理
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;



    @Autowired
    private VerifyCodeService verifyCodeService;

    /**
     * 10.249.46.148  HDU-WIFI下的内网ip地址
     * @return
     */
    @GetMapping("/ping")
    @Operation(summary = "用于调试")
    public Result ping() {
        return Result.success("访问成功");
    }



    /**
     * 用户注册接口
     * @param regisDTO
     * @return
     */
    @PostMapping("/regis")
    @Operation(summary = "用户注册接口", description = "用户通过手机号码、密码和验证码完成注册")
    public Result register(@Valid @RequestBody RegisDTO regisDTO) {
//        try {
//            String id = userService.add(regisDTO);
//
//            // 生成jwt令牌
//            HashMap<String, Object> claims = new HashMap<>();
//            claims.put(BusinessConstant.USER_ID,id);
//            String jwt = JwtUtil.createJWT(
//                    jwtProperties.getSecretKey(),
//                    jwtProperties.getTtl(),
//                    claims
//            );
//
//            // 将userId存入当前线程中
//            BaseContext.setCurrentId(id);
//
//            return Result.success(jwt);
//        } catch (BusinessException e) {
//            // 捕获业务异常，如验证码错误、用户已存在
//            log.warn("注册失败：{}",e.getMessage());
//            return Result.error(e.getMessage());
//        } catch (Exception e){
//            // 捕获系统异常
//            log.error("注册发生异常", e);
//            return Result.error("系统异常，请稍后重试");
//        }
        return userService.add(regisDTO);
    }

    /**
     * 用户登录接口
     * @param loginDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录接口", description = "用户通过用户名和密码完成登录")
    public Result login(@Valid @RequestBody LoginDTO loginDTO) {
//        try {
//            UserLoginVO userLoginVO = userService.login(loginDTO);
//
//            // 生成jwt令牌
//            HashMap<String, Object> claims = new HashMap<>();
//            claims.put(BusinessConstant.USER_ID,userLoginVO.getId());
//            String jwt = JwtUtil.createJWT(
//                    jwtProperties.getSecretKey(),
//                    jwtProperties.getTtl(),
//                    claims
//            );
//            userLoginVO.setToken(jwt);
//
//            // 将userId存入当前线程中
//            BaseContext.setCurrentId(userLoginVO.getId());
//
//
//            return Result.success(userLoginVO);
//        } catch (BusinessException e) {
//            // 捕获业务异常，如用户不存在、密码错误
//            log.warn("登录失败：{}",e.getMessage());
//            throw new BusinessException(e.getMessage());
//        } catch (Exception e){
//            // 捕获系统异常
//            log.error("登录发生异常", e);
//            return Result.error("系统异常，请稍后重试");
//        }
        return userService.login(loginDTO);

    }

    /**
     * 发送验证码接口
     * @param verifyCodeGenerateDTO
     * @return
     */
    @PostMapping("/verify-code")
    @Operation(summary = "发送验证码接口")
    public Result verifyCode(@RequestBody VerifyCodeGenerateDTO  verifyCodeGenerateDTO) {
        return verifyCodeService.generateCode(verifyCodeGenerateDTO);
    }
}
