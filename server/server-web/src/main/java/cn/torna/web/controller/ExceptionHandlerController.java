package cn.torna.web.controller;

import cn.torna.common.bean.Result;
import cn.torna.common.exception.BizException;
import cn.torna.common.exception.ErrorCode;
import cn.torna.common.exception.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局错误处理
 *
 * @author wugang
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(Exception.class)
    public Object exceptionHandler(Exception e) {
        if (e instanceof ExceptionCode) {
            ExceptionCode exceptionCode = (ExceptionCode) e;
            ErrorCode errorCode = exceptionCode.getCode();
            log.error("报错，code:{}, msg:{}", errorCode.getCode(), errorCode.getMsg(), e);
            return Result.err(errorCode.getCode(), errorCode.getMsg());
        }
        if (e instanceof BizException || e instanceof IllegalArgumentException) {
            RuntimeException bizException = (RuntimeException) e;
            return Result.err(bizException.getMessage());
        }
        // 处理JSR-303错误
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
            String msg = allErrors.stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return Result.err(msg);
        }
        log.error("未知错误：", e);
        return Result.err("系统错误，请查看日志");
    }

}
