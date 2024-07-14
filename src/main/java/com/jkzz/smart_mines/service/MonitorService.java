package com.jkzz.smart_mines.service;

import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import com.jkzz.smart_mines.pojo.param.PlcParam;

public interface MonitorService {

    /**
     * 查询参数设备的值
     *
     * @param communicationManager 通讯管理者
     * @param plcParam             查询条件
     * @return 参数设备的值
     */
    JSONObject readParametersOfParaSetting(CommunicationManager communicationManager, PlcParam plcParam);

    /**
     * 写PLC
     *
     * @param communicationManager 通讯管理者
     * @param plcParam             写入条件
     * @return 写入信息
     */
    JSONObject updateParameters(CommunicationManager communicationManager, PlcParam plcParam);

    /**
     * 远程就地切换
     *
     * @param communicationManager 通讯管理者
     * @param plcParam             写入条件
     * @return 写入信息
     */
    JSONObject remoteOrLocalControl(CommunicationManager communicationManager, PlcParam plcParam);

}
