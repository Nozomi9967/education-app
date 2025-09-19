package com.education.service;

import com.education.pojo.dto.VerifyCodeDTO;
import com.education.pojo.dto.VerifyCodeGenerateDTO;
import com.education.pojo.entity.VerifyCode;
import com.education.result.Result;

import java.time.LocalDateTime;

public interface VerifyCodeService {

    /**
     * 生成并发送验证码
     * @param generateDTO 生成验证码的参数（业务ID、类型等）
     * @return 生成结果（成功/失败信息）
     */
    Result generateCode(VerifyCodeGenerateDTO generateDTO);

    /**
     * 校验验证码
     * @param verifyCodeDTO 包含业务ID和验证码
     * @param businessType 业务类型（如注销=3）
     * @return 校验结果（通过/失败信息）
     */
    Result validateCode(VerifyCodeDTO verifyCodeDTO, Integer businessType);

    /**
     * 查询最新有效的验证码
     * @param businessId 业务ID（如用户UUID）
     * @param businessType 业务类型
     * @param idType ID类型（如UUID=3）
     * @return 验证码实体（null表示无有效验证码）
     */
    VerifyCode getLatestValidCode(String businessId, Integer businessType, Integer idType);

    /**
     * 标记验证码为已使用
     * @param codeId 验证码ID
     */
    void markAsUsed(Long codeId);

    /**
     * 统计单位时间内的验证码生成次数（防刷用）
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param startTime 起始时间
     * @return 生成次数
     */
    int countByTimeRange(String businessId, Integer businessType, LocalDateTime startTime);
}
