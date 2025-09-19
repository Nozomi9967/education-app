package com.education.exception;

import lombok.Getter;

/**
 * 业务异常类，适配Result返回结构
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误消息（直接对应Result中的msg字段）
     */
    private final String message;

    /**
     * 构造方法：使用自定义错误消息
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.message = message;
    }
}