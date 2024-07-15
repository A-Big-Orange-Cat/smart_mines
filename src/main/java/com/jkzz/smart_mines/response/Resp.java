package com.jkzz.smart_mines.response;

import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.io.Serializable;

@Getter
@ApiModel("通用返回对象")
public class Resp<T> implements Serializable {

    private Resp() {

    }

    private Resp(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @ApiModelProperty("状态码")
    private int code;

    @ApiModelProperty("提示信息")
    private String msg;

    @ApiModelProperty("数据封装")
    private T data;

    public static <T> Resp<T> success() {
        return new Resp<>(200, "success", null);
    }

    public static <T> Resp<T> success(T data) {
        return new Resp<>(200, "success", data);
    }

    public static <T> Resp<T> error() {
        return new Resp<>(500, "error", null);
    }

    public static <T> Resp<T> error(String msg) {
        return new Resp<>(500, msg, null);
    }

    public static <T> Resp<T> error(int code, String msg) {
        return new Resp<>(code, msg, null);
    }

    public static <T> Resp<T> error(AppExceptionCodeMsg appExceptionCodeMsg) {
        return new Resp<>(appExceptionCodeMsg.getCode(), appExceptionCodeMsg.getMsg(), null);
    }

    public Resp<T> code(int code) {
        this.code = code;
        return this;
    }

    public Resp<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Resp<T> data(T data) {
        this.data = data;
        return this;
    }

}
