package com.jkzz.smart_mines.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogSignalCurveVO implements Serializable {

    /**
     * 操作后的值
     */
    private String signalValue;

    /**
     * 操作时间戳
     */
    private Long timeMillis;

}
