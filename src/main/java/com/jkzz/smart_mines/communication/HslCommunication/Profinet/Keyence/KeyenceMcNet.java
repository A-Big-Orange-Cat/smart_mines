package com.jkzz.smart_mines.communication.HslCommunication.Profinet.Keyence;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Address.McAddressData;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Profinet.Melsec.MelsecMcNet;

/**
 * 基恩士PLC的数据通信类
 */
public class KeyenceMcNet extends MelsecMcNet {

    /**
     * 基恩士PLC的数据通信类，使用QnA兼容3E帧的通信协议实现，使用二进制的格式，地址格式需要进行转换成三菱的格式，详细参照备注说明<br />
     * Keyence PLC's data communication class is implemented using QnA compatible 3E frame communication protocol.
     * It uses binary format. The address format needs to be converted to Mitsubishi format.
     */
    public KeyenceMcNet() {
        super();
    }

    /**
     * 实例化一个基恩士的Qna兼容3E帧协议的通讯对象
     *
     * @param ipAddress PLC的Ip地址
     * @param port      PLC的端口
     */
    public KeyenceMcNet(String ipAddress, int port) {
        super(ipAddress, port);
    }


    public OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length) {
        return McAddressData.ParseKeyenceFrom(address, length);
    }

    /**
     * 返回表示当前对象的字符串
     *
     * @return 字符串信息
     */
    @Override
    public String toString() {
        return String.format("KeyenceMcNet[%s:%d]", getIpAddress(), getPort());
    }
}
