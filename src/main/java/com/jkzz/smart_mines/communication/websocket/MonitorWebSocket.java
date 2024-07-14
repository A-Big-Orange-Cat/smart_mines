package com.jkzz.smart_mines.communication.websocket;

import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.core.Monitor;
import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.exception.AppException;
import com.jkzz.smart_mines.hikvision.PreviewUntil;
import com.jkzz.smart_mines.pojo.domain.DeviceDvr;
import com.jkzz.smart_mines.service.DeviceDvrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@ServerEndpoint(value = "/monitorWebSocket/{deviceTypeId}/{deviceId}")
public class MonitorWebSocket {

    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean

    /**
     * 通讯管理者对象
     */
    private static CommunicationManager communicationManager;
    private static DeviceDvrService deviceDvrService;
    /**
     * monitor
     */
    private Monitor monitor;
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    private List<DeviceDvr> dvrs;

    @Autowired
    public void setCommunicationManager(CommunicationManager communicationManager) {
        MonitorWebSocket.communicationManager = communicationManager;
    }

    @Autowired
    public void setDeviceDvrService(DeviceDvrService deviceDvrService) {
        MonitorWebSocket.deviceDvrService = deviceDvrService;
    }

    /**
     * 客户端与服务端连接成功
     *
     * @param session      会话
     * @param deviceTypeId 设备类型id
     * @param deviceId     设备id
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("deviceTypeId") String deviceTypeId, @PathParam("deviceId") String deviceId) {
        this.session = session;
        monitor = Optional.ofNullable(communicationManager.getMonitorManagerMap().get(deviceTypeId))
                .map(monitorManager -> monitorManager.getMonitorMap().get(deviceId))
                .orElseThrow(() -> new AppException(AppExceptionCodeMsg.PLC_MONITOR_NOT_EXIST));
        monitor.getMonitorWebSockets().add(this);
        //dvrs = deviceDvrService.queryByDeviceId(Integer.valueOf(deviceId));
        //PreviewUntil.loginAndPicByPlay(dvrs.get(0), monitor);
        //log.info("monitorWebSocket连接成功！");
    }

    /**
     * 客户端与服务端连接关闭
     */
    @OnClose
    public void onClose() {
        //log.info("monitorWebSocket连接关闭！");
        try {
            monitor.getMonitorWebSockets().remove(this);
            PreviewUntil.shutdown(dvrs.get(0).getLUserId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 客户端与服务端连接异常
     *
     * @param error 异常
     */
    @OnError
    public void onError(Throwable error) {
        log.error("monitorWebSocket错误,原因:" + error.getMessage());
    }

    /**
     * 客户端向服务端发送消息
     *
     * @param message 消息
     */
    @OnMessage
    public void onMsg(String message) {
        log.info("服务端收到客户端[{}]的消息：{}", session.getId(), message);
        /*
            do something for onMessage
            收到来自当前客户端的消息时
        */
    }

    // 此为单点消息
    public void sendOneMessage(JSONObject result) {
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(result.toJSONString());
            } catch (Exception e) {
                log.error("服务端发送消息给客户端失败：{}", e.getMessage());
            }
        }
    }

}
