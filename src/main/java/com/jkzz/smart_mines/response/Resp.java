package com.jkzz.smart_mines.response;

import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@ApiModel("通用返回对象")
public class Resp<T> implements Serializable {

    @ApiModelProperty("状态码")
    private int code;

    @ApiModelProperty("提示信息")
    private String msg;

    @ApiModelProperty("数据封装")
    private T data;

    public static Resp<Object> success() {
        return new Resp<>(200, "success", null);
    }

    public static <T> Resp<T> success(T data) {
        return new Resp<>(200, "success", data);
    }

    public static <T> Resp<T> success(String msg, T data) {
        return new Resp<>(200, msg, data);
    }

    public static <T> Resp<T> error(AppExceptionCodeMsg appExceptionCodeMsg) {
        return new Resp<>(appExceptionCodeMsg.getCode(), appExceptionCodeMsg.getMsg(), null);
    }

    public static <T> Resp<T> error(int code, String msg) {
        return new Resp<>(code, msg, null);
    }

}
