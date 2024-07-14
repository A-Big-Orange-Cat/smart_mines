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
public class AllenBradleyNet implements CommunicationNet {

    @Resource(name = "commThreadPoolExecutor")
    private TaskScheduler commThreadPoolExecutor;

    private com.jkzz.smart_mines.communication.HslCommunication.Profinet.AllenBradley.AllenBradleyNet abNet;

    private ScheduledFuture<?> scheduledFuture;

    private boolean isConn = false;

    @Value("${time.fixedDelay.reconnect}")
    private String reconnectTime;

    /**
     * 创建一个与AB型号的PLC服务器CIP通讯协议对象
     *
     * @param ip   ip地址
     * @param port 端口号
     */
    @Override
    public void createCommunicationNet(String ip, int port) {
        abNet = CUtil.getAllenBradleyNet(ip, port);
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
            OperateResult operateResult = abNet.ReadInt16("1");
            VUtil.handler(isConn != operateResult.IsSuccess).handler(() -> {
                VUtil.isTrueOrFalse(operateResult.IsSuccess).trueOrFalseHandler(
                        () -> log.info("AllenBradleyNet长连接设置成功，ip：" + abNet.getIpAddress()),
                        () -> log.error("AllenBradleyNet连接失败，ip：" + abNet.getIpAddress() + "，信息：" + operateResult.Message)
                );
                isConn = operateResult.IsSuccess;
            });
        }
    }

    @Override
    public void disconnect() {
        scheduledFuture.cancel(true);
        VUtil.handler(abNet.ConnectClose().IsSuccess).handler(() -> {
            isConn = false;
            log.info("ip：" + abNet.getIpAddress() + "----->AllenBradleyNet关闭连接。");
        });
    }

    @Override
    public ReadResult<Short> readInt16(String address) {
        return CUtil.read(address, 1, () -> abNet.ReadInt16(address));
    }

    @Override
    public ReadResult<Integer> readUInt16(String address) {
        return CUtil.read(address, 1, () -> abNet.ReadUInt16(address));
    }

    @Override
    public ReadResult<int[]> readUInt16(String address, Integer length) {
        return CUtil.read(address, length, () -> abNet.ReadUInt16(address, length.shortValue()));
    }

    @Override
    public ReadResult<Boolean> readBool(String address) {
        return CUtil.read(address, 1, () -> abNet.ReadBool(address));
    }

    @Override
    public WriteResult write(String address, Integer value) {
        return CUtil.write(address, value, () -> abNet.Write(address, value.shortValue()));
    }

    @Override
    public WriteResult write(String address, Long value) {
        return CUtil.write(address, value, () -> abNet.Write(address, value.intValue()));
    }

    @Override
    public WriteResult write(String address, Boolean value) {
        return CUtil.write(address, value, () -> abNet.Write(address, value));
    }

}
