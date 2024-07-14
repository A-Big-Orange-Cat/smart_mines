package com.jkzz.smart_mines.communication.websocket;

import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@Slf4j
@Component
@ServerEndpoint(value = "/infoWebSocket")
public class InfoWebSocket {

    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean

    /**
     * 通讯管理者对象
     */
    private static CommunicationManager communicationManager;
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    @Autowired
    public void setCommunicationManager(CommunicationManager communicationManager) {
        InfoWebSocket.communicationManager = communicationManager;
    }

    /**
     * 客户端与服务端连接成功
     *
     * @param session 会话
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        communicationManager.getInfoWebSockets().add(this);
        //log.info("infoWebSocket连接成功！");
    }

    /**
     * 客户端与服务端连接关闭
     */
    @OnClose
    public void onClose() {
        //log.info("infoWebSocket连接关闭！");
        try {
            communicationManager.getInfoWebSockets().remove(this);
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
        log.error("infoWebSocket错误,原因:" + error.getMessage());
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
    public void sendOneMessage(String message) {
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                log.error("服务端发送消息给客户端失败：{}", e.getMessage());
            }
        }
    }

}
