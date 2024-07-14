package com.jkzz.smart_mines.pojo.vo;

import com.jkzz.smart_mines.enumerate.impl.CommunicationProtocolEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceTypeVO implements Serializable {

    /**
     * 设备类型id
     */
    private Integer deviceTypeId;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;

    /**
     * 通讯协议
     */
    private CommunicationProtocolEnum communicationProtocol;

    /**
     * 启用的设备数量
     */
    private Integer deviceCount;

}
