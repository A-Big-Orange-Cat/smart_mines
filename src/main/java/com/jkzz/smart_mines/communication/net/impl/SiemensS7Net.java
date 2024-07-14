package com.jkzz.smart_mines.communication.net.impl;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.Result.ReadResult;
import com.jkzz.smart_mines.communication.Result.WriteResult;
import com.jkzz.smart_mines.communication.net.CommunicationNet;
import com.jkzz.smart_mines.utils.CUtil;
import com.jkzz.smart_mines.utils.VUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@Scope("prototype")
public class SiemensS7Net implements CommunicationNet {

    @Resource(name = "commThreadPoolExecutor")
    private TaskScheduler commThreadPoolExecutor;

    private com.jkzz.smart_mines.communication.HslCommunication.Profinet.Siemens.SiemensS7Net s7Net;

    private ScheduledFuture<?> scheduledFuture;

    private boolean isConn = false;

    @Value("${time.fixedDelay.reconnect}")
    private String reconnectTime;

    /**
     * 创建一个与S200smart型号的PLC服务器S7通讯协议对象
     *
     * @param ip   ip地址
     * @param port 端口号
     */
    @Override
    public void createCommunicationNet(String ip, int port) {
        s7Net = CUtil.getSiemensS7Net(ip, port);
        scheduledFuture = commThreadPoolExecutor.scheduleWithFixedDelay(this::isConn, Long.parseLong(reconnectTime));
    }

    @Override
    public void setCommunicationNet(String ip, int port) {
        disconnect();
        createCommunicationNet(ip, port);
    }

    @Override
    public boolean getConn() {
        return isConn;
    }

    @Override
    public void isConn() {
        if (!Thread.currentThread().isInterrupted()) {
            OperateResult operateResult = s7Net.ReadInt16("1");
            VUtil.handler(isConn != operateResult.IsSuccess).handler(() -> {
                VUtil.isTrueOrFalse(operateResult.IsSuccess).trueOrFalseHandler(
                        () -> log.info("SiemensS7Net长连接设置成功，ip：" + s7Net.getIpAddress()),
                        () -> log.error("SiemensS7Net连接失败，ip：" + s7Net.getIpAddress() + "，信息：" + operateResult.Message)
                );
                isConn = operateResult.IsSuccess;
            });
        }
    }

    @Override
    public void disconnect() {
        scheduledFuture.cancel(true);
        VUtil.handler(s7Net.ConnectClose().IsSuccess).handler(() -> {
            isConn = false;
            log.info("ip：" + s7Net.getIpAddress() + "----->SiemensS7Net关闭连接。");
        });
    }

    @Override
    public ReadResult<Short> readInt16(String address) {
        return CUtil.read(address, 1, () -> s7Net.ReadInt16(address));
    }

    @Override
    public ReadResult<Integer> readUInt16(String address) {
        return CUtil.read(address, 1, () -> s7Net.ReadUInt16(address));
    }

    @Override
    public ReadResult<int[]> readUInt16(String address, Integer length) {
        return CUtil.read(address, length, () -> s7Net.ReadUInt16(address, length.shortValue()));
    }

    @Override
    public ReadResult<Boolean> readBool(String address) {
        return CUtil.read(address, 1, () -> s7Net.ReadBool(address));
    }

    @Override
    public WriteResult write(String address, Integer value) {
        return CUtil.write(address, value, () -> s7Net.Write(address, value.shortValue()));
    }

    @Override
    public WriteResult write(String address, Long value) {
        return CUtil.write(address, value, () -> s7Net.Write(address, value.intValue()));
    }

    @Override
    public WriteResult write(String address, Boolean value) {
        return CUtil.write(address, value, () -> s7Net.Write(address, value));
    }

}
