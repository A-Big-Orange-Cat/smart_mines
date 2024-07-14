package com.jkzz.smart_mines.pojo.qo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class LogCurveQO implements Serializable {

    /**
     * 设备类型
     */
    @NotNull(message = "请选择设备类型")
    private Integer deviceTypeId;

    /**
     * 设备id
     */
    @NotNull(message = "请选择设备")
    private Integer deviceId;

    /**
     * 设备参数代码
     */
    @NotNull(message = "请选择设备参数")
    private String code;

    /**
     * 时间范围
     */
    @NotNull(message = "请选择起始时间")
    @Size(min = 2, max = 2, message = "请选择正确的起始时间")
    private long[] time;

}
