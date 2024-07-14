package com.jkzz.smart_mines.pojo.qo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class LogQO implements Serializable {

    /**
     * 设备id
     */
    @NotNull(message = "请选择设备")
    Integer deviceId;

    /**
     * 页数
     */
    @Min(value = 1, message = "页数最小为1")
    @NotNull(message = "请选择页数")
    Integer pageIndex;

    /**
     * 参数名
     */
    String parameterName = "";

    /**
     * 时间范围
     */
    @Size(min = 2, max = 2, message = "请选择正确的起始时间")
    long[] time;

    public void setParameterName(String parameterName) {
        this.parameterName = "%" + parameterName + "%";
    }

}
