package com.education.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.constant.BusinessConstant;
import com.education.exception.BusinessException;
import com.education.pojo.dto.VerifyCodeDTO;
import com.education.pojo.dto.VerifyCodeGenerateDTO;
import com.education.pojo.entity.VerifyCode;
import com.education.mapper.VerifyCodeMapper;
import com.education.result.Result;
import com.education.service.VerifyCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class VerifyCodeServiceImpl implements VerifyCodeService {

    @Autowired
    private VerifyCodeMapper verifyCodeMapper;

    // 1. 注入 RestTemplate 用于发送 HTTP 请求
    @Autowired
    private RestTemplate restTemplate;

    // 建议将 URL 放在配置文件中
    private static final String PUSH_API_URL = "https://push.spug.cc/send/RZykKraJDM8w0lAL";

    /**
     * 生成验证码并发送（带事务控制）
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 异常时回滚
    public Result generateCode(VerifyCodeGenerateDTO generateDTO) {
        try {
            // 1. 防刷校验（5分钟内最多3次）
            LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
            int count = this.countByTimeRange(
                    generateDTO.getBusinessId(),
                    generateDTO.getBusinessType(),
                    fiveMinutesAgo
            );
            if (count >= 3) {
                return Result.error(BusinessConstant.FREQUENT_OPERATION);
            }

            // 2. 生成6位数字验证码 (优化了生成方式)
            String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            log.info("生成验证码：{}，业务ID：{}", code, generateDTO.getBusinessId());

            // 3. 构建验证码实体
            VerifyCode verifyCode = new VerifyCode();
            verifyCode.setBusinessId(generateDTO.getBusinessId());
            verifyCode.setIdType(generateDTO.getIdType());
            verifyCode.setBusinessType(generateDTO.getBusinessType());
            verifyCode.setCode(code);
            verifyCode.setExpireTime(LocalDateTime.now().plusMinutes(15)); // 15分钟过期
            verifyCode.setUseStatus(0); // 0-未使用
            verifyCode.setCreateTime(LocalDateTime.now());

            // 4. 保存到数据库
            verifyCodeMapper.insert(verifyCode);

            // 5. 发送请求到第三方API（若发送失败，事务回滚）
            boolean sendSuccess = sendCodeViaApi(generateDTO.getPhone(), code);

            if (!sendSuccess) {
                // 如果发送失败，抛出异常，触发事务回滚
                throw new BusinessException("验证码发送失败，请稍后重试");
            }

            return Result.success("验证码已发送");
        } catch (BusinessException e) {
            log.error("生成验证码失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("生成验证码异常", e);
            return Result.error("系统异常");
        }
    }

    /**
     * 通过第三方 API 发送验证码
     * @param phone 手机号
     * @param code  验证码
     * @return true if success, false otherwise
     */
    private boolean sendCodeViaApi(String phone, String code) {
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "edu_app");
        requestBody.put("code", code);
        requestBody.put("targets", phone);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            log.info("正在向 API 发送请求: {}", PUSH_API_URL);
            ResponseEntity<String> response = restTemplate.postForEntity(PUSH_API_URL, request, String.class);

            // 检查响应状态码
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("API 请求成功，响应: {}", response.getBody());
                // 这里假设返回任何 200 OK 都代表发送成功
                // 如果 API 有特定的成功响应体（如 {"code":0}），需要在这里解析判断
                return true;
            } else {
                log.error("API 请求失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return false;
            }
        } catch (RestClientException e) {
            // 处理网络异常、连接超时等问题
            log.error("调用推送 API 时发生异常", e);
            return false;
        }
    }

    /**
     * 校验验证码
     */
    @Override
    public Result validateCode(VerifyCodeDTO verifyCodeDTO, Integer businessType) {
        try {
            // 1. 查询最新有效验证码
            VerifyCode code = this.getLatestValidCode(
                    verifyCodeDTO.getId(), // businessId（用户UUID）
                    businessType,
                    1 // idType=1（phone类型）
            );

            // 2. 校验逻辑
            if (code == null) {
                return Result.error("验证码不存在或已失效");
            }
            if (!code.getCode().equals(verifyCodeDTO.getVerifyCode())) {
                return Result.error("验证码错误");
            }

            // 3. 标记为已使用
            this.markAsUsed(code.getId());
            return null;
        } catch (Exception e) {
            log.error("验证码校验异常", e);
            return Result.error("系统异常");
        }
    }

    /**
     * 查询最新有效的验证码 (此方法未改动)
     */
    @Override
    public VerifyCode getLatestValidCode(String businessId, Integer businessType, Integer idType) {
        QueryWrapper<VerifyCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("business_id", businessId)
                .eq("business_type", businessType)
                .eq("id_type", idType)
                .eq("use_status", 0) // 未使用
                .gt("expire_time", LocalDateTime.now()) // 未过期
                .orderByDesc("create_time")
                .last("LIMIT 1");
        return verifyCodeMapper.selectOne(queryWrapper);
    }

    /**
     * 标记验证码为已使用 (此方法未改动)
     */
    @Override
    public void markAsUsed(Long codeId) {
        VerifyCode code = new VerifyCode();
        code.setId(codeId);
        code.setUseStatus(1); // 1-已使用
        verifyCodeMapper.updateById(code);
    }

    /**
     * 统计单位时间内的生成次数 (此方法未改动)
     */
    @Override
    public int countByTimeRange(String businessId, Integer businessType, LocalDateTime startTime) {
        QueryWrapper<VerifyCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("business_id", businessId)
                .eq("business_type", businessType)
                .ge("create_time", startTime);
        return Math.toIntExact(verifyCodeMapper.selectCount(queryWrapper));
    }
}