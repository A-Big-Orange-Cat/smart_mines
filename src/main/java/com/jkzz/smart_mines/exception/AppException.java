package com.jkzz.smart_mines.exception;

import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppException extends RuntimeException {

    private int code;
    private String msg;

    public AppException(AppExceptionCodeMsg appExceptionCodeMsg) {
        this.code = appExceptionCodeMsg.getCode();
        this.msg = appExceptionCodeMsg.getMsg();
    }

}
