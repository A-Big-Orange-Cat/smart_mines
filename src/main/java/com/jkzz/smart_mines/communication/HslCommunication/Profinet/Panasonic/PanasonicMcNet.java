package com.jkzz.smart_mines.communication.HslCommunication.Profinet.Panasonic;

import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Address.McAddressData;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Profinet.Melsec.MelsecMcNet;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

public class PanasonicMcNet extends MelsecMcNet {

    /**
     * 实例化松下的的Qna兼容3E帧协议的通讯对象<br />
     * Instantiate Panasonic's Qna compatible 3E frame protocol communication object
     */
    public PanasonicMcNet() {

    }

    /**
     * 指定ip地址及端口号来实例化一个松下的Qna兼容3E帧协议的通讯对象<br />
     * Specify an IP address and port number to instantiate a Panasonic Qna compatible 3E frame protocol communication object
     *
     * @param ipAddress PLC的Ip地址
     * @param port      PLC的端口
     */
    public PanasonicMcNet(String ipAddress, int port) {
        super(ipAddress, port);
    }

    public OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length) {
        return McAddressData.ParsePanasonicFrom(address, length);
    }

    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        int errorCode = Utilities.getUShort(response, 9);
        if (errorCode != 0)
            return new OperateResultExOne<byte[]>(errorCode, PanasonicHelper.GetMcErrorDescription(errorCode));

        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 11));
    }

    public String toString() {
        return "PanasonicMcNet[" + getIpAddress() + ":" + getPort() + "]";
    }

}
