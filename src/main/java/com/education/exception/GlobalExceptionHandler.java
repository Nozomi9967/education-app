package com.education.exception;

import com.education.constant.BusinessConstant;
import com.education.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.education.result.Result;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 处理参数校验异常（只返回第一条错误信息）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException e) {
        // 获取第一条错误信息
        String errorMsg = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        // 只返回这条错误信息，不再罗列所有错误
        log.error(errorMsg);
        return Result.error(errorMsg);
    }

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.error(e.getMessage());
        return Result.error(e.getMessage());
    }

    // 处理其他未知异常
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error(e.getMessage());
        return Result.error(BusinessConstant.SYSTEM_ERROR);
    }
}