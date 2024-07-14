package com.jkzz.smart_mines.pojo.vo;

import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import lombok.Data;

import java.io.Serializable;

@Data
public class LogAlarmVO implements Serializable {

    /**
     * 报警记录id
     */
    private Long logAlarmId;

    /**
     * 设备id
     */
    private Integer deviceId;

    /**
     * 基础设备参数id
     */
    private BaseDeviceTypeParameter baseDeviceTypeParameter;

    /**
     * 报警的值
     */
    private String alarmValue;

    /**
     * 报警时间戳
     */
    private Long timeMillis;

}
