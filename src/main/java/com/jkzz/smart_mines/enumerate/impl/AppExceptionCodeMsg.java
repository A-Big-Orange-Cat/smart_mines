package com.jkzz.smart_mines.enumerate.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppExceptionCodeMsg {

    SYSTEM_NOT_ACTIVATION(10001, "系统未激活，请填写激活码或联系管理员。"),
    SYSTEM_INVALID_ACTIVATION_CODE(1000, "激活码无效"),
    FAILURE_ACTIVATION(1000, "激活失败，请重试"),

    ERROR_DATA_TYPE(500, "数据类型错误"),

    FAILURE_INSERT(500, "添加失败"),
    FAILURE_DELETE(500, "删除失败"),
    FAILURE_UPDATE(500, "更新失败"),
    FAILURE_FILE_SAVE(500, "地图文件保存失败"),
    FAILURE_FILE_UPLOAD(500, "地图文件上传失败"),

    IP_EXIST(409, "IP地址已存在"),

    USER_EXIST(409, "用户名已存在"),
    USER_FILE_LOGIN(403, "用户命或密码错误"),
    USER_NOT_TOKEN(401, "您未登录，请登录"),
    USER_TOKEN_OVERDUE(401, "登录已失效，请重新登录"),
    USER_LOGIN_REPLACE(401, "您的账号已在别处登录！"),
    USER_PERMISSION_ERROR(403, "您没有权限！"),

    PLC_MONITOR_NOT_EXIST(500, "未开启读写PLC"),
    PLC_OFF_LINE(500, "PLC处于离线状态"),
    PLC_CONTROL_NOT_REMOTE(403, "未开启远程控制"),

    DEVICE_NOT_EXISTS(500, "根据提供的ip没有找到设备"),
    ;

    private final int code;

    private final String msg;

}
