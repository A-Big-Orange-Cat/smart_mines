package com.jkzz.smart_mines.communication.HslCommunication.Profinet.Keyence;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Address.McAddressData;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Profinet.Melsec.MelsecMcAsciiNet;

/**
 * 基恩士PLC的数据通信类
 */
public class KeyenceMcAsciiNet extends MelsecMcAsciiNet {

    /**
     * 实例化基恩士的Qna兼容3E帧协议的通讯对象
     */
    public KeyenceMcAsciiNet() {
        super();
    }

    /**
     * 实例化一个基恩士的Qna兼容3E帧协议的通讯对象
     *
     * @param ipAddress PLC的Ip地址
     * @param port      PLC的端口
     */
    public KeyenceMcAsciiNet(String ipAddress, int port) {
        super(ipAddress, port);
    }

    public OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length) {
        return McAddressData.ParseKeyenceFrom(address, length);
    }

    /**
     * 获取当前对象的字符串标识形式
     *
     * @return 字符串信息
     */
    @Override
    public String toString() {
        return String.format("KeyenceMcAsciiNet[%s:%d]", getIpAddress(), getPort());
    }
}
