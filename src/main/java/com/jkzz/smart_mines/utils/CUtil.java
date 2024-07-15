package com.jkzz.smart_mines.utils;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.ModBus.ModbusTcpNet;
import com.jkzz.smart_mines.communication.HslCommunication.Profinet.AllenBradley.AllenBradleyNet;
import com.jkzz.smart_mines.communication.HslCommunication.Profinet.Siemens.SiemensPLCS;
import com.jkzz.smart_mines.communication.HslCommunication.Profinet.Siemens.SiemensS7Net;
import com.jkzz.smart_mines.communication.result.ReadResult;
import com.jkzz.smart_mines.communication.result.WriteResult;
import com.jkzz.smart_mines.utils.utilsinterface.ReadCallback;
import com.jkzz.smart_mines.utils.utilsinterface.WriteCallback;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

public final class CUtil {

    private static int timeOut;

    public static AllenBradleyNet getAllenBradleyNet(String ip, int port) {
        AllenBradleyNet abNet = new AllenBradleyNet(ip, port);
        abNet.setConnectTimeOut(timeOut);
        abNet.setReceiveTimeOut(timeOut);
        abNet.SetPersistentConnection();
        return abNet;
    }

    public static ModbusTcpNet getModbusTcpNet(String ip, int port) {
        ModbusTcpNet modbusTcpNet = new ModbusTcpNet(ip, port, (byte) 1);
        modbusTcpNet.setConnectTimeOut(timeOut);
        modbusTcpNet.setReceiveTimeOut(timeOut);
        modbusTcpNet.SetPersistentConnection();
        return modbusTcpNet;
    }

    public static SiemensS7Net getSiemensS7Net(String ip, int port) {
        SiemensS7Net s7Net = new SiemensS7Net(SiemensPLCS.S200Smart, ip);
        s7Net.setPort(port);
        s7Net.setConnectTimeOut(timeOut);
        s7Net.setReceiveTimeOut(timeOut);
        s7Net.SetPersistentConnection();
        return s7Net;
    }

    public static <T extends Serializable> ReadResult<T> read(String address, Integer length, ReadCallback<T> readCallback) {
        if (null == address || address.isEmpty()) {
            return new ReadResult<>(false, null, "读取地址不能为空");
        }
        if (null == length || length < 1) {
            return new ReadResult<>(false, null, "读取长度不能小于1");
        }
        OperateResultExOne<T> read = readCallback.execute();
        return new ReadResult<>(read.IsSuccess, read.Content, read.Message);
    }

    public static WriteResult write(String address, Object value, WriteCallback writeCallback) {
        if (null == address || null == value || address.isEmpty()) {
            return new WriteResult(false, null, null, "写入地址和写入值不能为空");
        }
        OperateResult write = writeCallback.execute();
        return new WriteResult(write.IsSuccess, null, null, write.Message);
    }

    @Value("${plc.time.out}")
    public void setTimeOut(int timeOut) {
        CUtil.timeOut = timeOut;
    }

}
