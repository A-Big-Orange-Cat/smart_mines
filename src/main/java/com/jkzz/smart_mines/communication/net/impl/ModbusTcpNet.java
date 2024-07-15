package com.jkzz.smart_mines.communication.net.impl;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.net.CommunicationNet;
import com.jkzz.smart_mines.communication.result.ReadResult;
import com.jkzz.smart_mines.communication.result.WriteResult;
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
public class ModbusTcpNet implements CommunicationNet {

    @Resource(name = "commThreadPoolExecutor")
    private TaskScheduler commThreadPoolExecutor;

    private com.jkzz.smart_mines.communication.HslCommunication.ModBus.ModbusTcpNet modbusTcpNet;

    private ScheduledFuture<?> scheduledFuture;

    private boolean isConn = false;

    @Value("${time.fixedDelay.reconnect}")
    private String reconnectTime;

    /**
     * 创建一个与PLC服务器ModbusTCP通讯协议对象
     *
     * @param ip   ip地址
     * @param port 端口号
     */
    @Override
    public void createCommunicationNet(String ip, int port) {
        modbusTcpNet = CUtil.getModbusTcpNet(ip, port);
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
            OperateResult operateResult = modbusTcpNet.ReadInt16("1");
            VUtil.handler(isConn != operateResult.IsSuccess).handler(() -> {
                VUtil.isTrueOrFalse(operateResult.IsSuccess).trueOrFalseHandler(
                        () -> log.info("ModbusTcpNet长连接设置成功，ip：{}", modbusTcpNet.getIpAddress()),
                        () -> log.error("ModbusTcpNet连接失败，ip：{}，信息：{}", modbusTcpNet.getIpAddress(), operateResult.Message)
                );
                isConn = operateResult.IsSuccess;
            });
        }
    }

    /**
     * 关闭长连接
     */
    @Override
    public void disconnect() {
        scheduledFuture.cancel(true);
        VUtil.handler(isConn).handler(() ->
                VUtil.handler(modbusTcpNet.ConnectClose().IsSuccess).handler(() -> {
                    isConn = false;
                    log.info("ip：{}----->ModbusTcpNet关闭连接。", modbusTcpNet.getIpAddress());
                })
        );
    }

    /**
     * 读取PLC指定地址数据(读16位有符号整数)
     *
     * @param address 读取地址
     * @return 读取的值
     */
    @Override
    public ReadResult<Short> readInt16(String address) {
        return CUtil.read(address, 1, () -> modbusTcpNet.ReadInt16(address));
    }

    /**
     * 读取PLC指定地址数据(读16位无符号整数)
     *
     * @param address 读取地址
     * @return 读取的值
     */
    @Override
    public ReadResult<Integer> readUInt16(String address) {
        return CUtil.read(address, 1, () -> modbusTcpNet.ReadUInt16(address));
    }

    /**
     * 读取PLC指定地址数据(读多个16位无符号整数)
     *
     * @param address 读取起始地址
     * @param length  读取长度
     * @return 读取的值列表
     */
    @Override
    public ReadResult<int[]> readUInt16(String address, Integer length) {
        return CUtil.read(address, length, () -> modbusTcpNet.ReadUInt16(address, length.shortValue()));
    }

    /**
     * 读取PLC指定地址数据(读bool)
     *
     * @param address 读取地址
     * @return 读取的值
     */
    @Override
    public ReadResult<Boolean> readBool(String address) {
        return CUtil.read(address, 1, () -> modbusTcpNet.ReadCoil(address));
    }

    /**
     * 写入子PLC指定地址数据(写16位有符号整数)
     *
     * @param address 写入地址
     * @param value   写入的值
     * @return 写入结果
     */
    @Override
    public WriteResult write(String address, Integer value) {
        return CUtil.write(address, value, () -> modbusTcpNet.Write(address, value.shortValue()));
    }

    /**
     * 写入子PLC指定地址数据(写16位无符号整数)
     *
     * @param address 写入地址
     * @param value   写入的值
     * @return 写入结果
     */
    @Override
    public WriteResult write(String address, Long value) {
        return CUtil.write(address, value, () -> modbusTcpNet.Write(address, value.intValue()));
    }

    /**
     * 写入PLC指定地址数据(写bool)
     *
     * @param address 写入地址
     * @param value   写入的值
     * @return 写入结果
     */
    @Override
    public WriteResult write(String address, Boolean value) {
        return CUtil.write(address, value, () -> modbusTcpNet.Write(address, value));
    }

}
