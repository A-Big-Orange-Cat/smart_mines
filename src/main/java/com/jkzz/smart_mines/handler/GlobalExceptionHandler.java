package com.jkzz.smart_mines.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.exception.AppException;
import com.jkzz.smart_mines.response.Resp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局自定义异常处理
     */
    @ExceptionHandler(value = AppException.class)
    @ResponseBody
    public <T> Resp<T> exceptionHandler(AppException e) {
        log.error(e.getMsg());
        return Resp.error(e.getCode(), e.getMsg());
    }

    /**
     * 全局运行时异常处理
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public <T> Resp<T> handleRuntimeException(RuntimeException e) {
        log.error("服务器异常：" + e.getCause().getMessage());
        return Resp.error(500, "服务器异常：" + e.getCause().getMessage());
    }

    /**
     * 全局方法参数校验
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public <T> Resp<T> handleMethodArgumentNotValidException(BindException e) {
        return Resp.error(500, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 全局登录异常处理
     */
    @ExceptionHandler(NotLoginException.class)
    public <T> Resp<T> handlerNotLoginException(NotLoginException exception) {
        // 判断登录异常的场景，定制化返回提示
        String type = exception.getType();
        switch (type) {
            case NotLoginException.INVALID_TOKEN:
            case NotLoginException.TOKEN_TIMEOUT:
                return Resp.error(AppExceptionCodeMsg.USER_TOKEN_OVERDUE);
            case NotLoginException.BE_REPLACED:
            case NotLoginException.KICK_OUT:
                return Resp.error(AppExceptionCodeMsg.USER_LOGIN_REPLACE);
            default:
                return Resp.error(AppExceptionCodeMsg.USER_NOT_TOKEN);
        }
    }

    /**
     * 全局角色校验异常处理
     */
    @ExceptionHandler(NotRoleException.class)
    public <T> Resp<T> handlerNotRoleException() {
        return Resp.error(AppExceptionCodeMsg.USER_PERMISSION_ERROR);
    }

    /**
     * 全局权限校验异常处理
     */
    @ExceptionHandler(NotPermissionException.class)
    public <T> Resp<T> handlerNotPermissionException() {
        return Resp.error(AppExceptionCodeMsg.USER_PERMISSION_ERROR);
    }

}
