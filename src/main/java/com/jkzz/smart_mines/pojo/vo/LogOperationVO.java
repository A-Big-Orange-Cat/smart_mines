package com.jkzz.smart_mines.pojo.vo;

import com.jkzz.smart_mines.enumerate.impl.ControlModeEnum;
import com.jkzz.smart_mines.enumerate.impl.OperationResultEnum;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import com.jkzz.smart_mines.pojo.domain.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class LogOperationVO implements Serializable {

    /**
     * 操作记录id
     */
    private Long logOperationId;

    /**
     * 设备id
     */
    private Integer deviceId;

    /**
     * 设备参数
     */
    private BaseDeviceTypeParameter baseDeviceTypeParameter;

    /**
     * 用户
     */
    private User user;

    /**
     * 远程/就地(0:就地/1:远程)
     */
    private ControlModeEnum controlMode;

    /**
     * 操作的值
     */
    private String operationValue;

    /**
     * 操作结果
     */
    private OperationResultEnum operationResult;

    /**
     * 操作时间戳
     */
    private Long timeMillis;

}
