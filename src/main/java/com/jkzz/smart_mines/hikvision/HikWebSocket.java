package com.jkzz.smart_mines.hikvision;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 前后端交互的类实现消息的接收推送
 * "/videoSocket/{lUserId}" 前端通过此URI和后端建立连接
 */
@Slf4j
@Data
@ServerEndpoint(value = "/videoSocket/{lUserId}")
@Component
public class HikWebSocket {

    /**
     * 记录当前在线网页数量
     */
    private static final AtomicInteger onlineCount = new AtomicInteger(0);
    private static ThreadPoolTaskExecutor threadPoolTaskExecutor;
    /**
     * 用户句柄
     */
    private Integer lUserId;
    /**
     * 会话
     */
    private Session session;

    private Future future;

    @Autowired
    public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        HikWebSocket.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("lUserId") Integer lUserId) {
        onlineCount.addAndGet(1);
        this.lUserId = lUserId;
        this.session = session;
        //log.info("当前已经登录用户句柄S：[{}]，有新连接socket[{}]加入，摄像头登录用户的句柄为：{}，当前在线socket（视频路数）数量：{}", MyBlockingQueue.bpMap.keySet(), session.getId(), lUserId, onlineCount);
        if (MyBlockingQueue.bpMap.containsKey(lUserId)) {
            if (null == MyBlockingQueue.findLPlayIdByUserId(lUserId)) {
                log.error("警告：根据用户句柄{}，没有找到预览句柄", lUserId);
            }
            BlockingQueue<byte[]> blockingQueue = MyBlockingQueue.bpMap.get(lUserId);
            MyBlockingQueue.LUserIdToWebSocketMap.put(lUserId, this);
            future = threadPoolTaskExecutor.submit(() -> sendMessage(blockingQueue));
        } else {
            log.error("当前没有找到用户登录句柄，无法播放:{}", lUserId);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() throws InterruptedException {
        future.cancel(true);
        // 在线数减1
        onlineCount.decrementAndGet();
        // 执行退出操作
        //log.info(String.format("socket[%s]断开链接，用户句柄[%s]停止预览并退出登录", session.getId(), lUserId));
        Future<Boolean> logoutFuture = threadPoolTaskExecutor.submit(() -> PreviewUntil.logoutPlayView(lUserId));
        Boolean flag = null;
        try {
            flag = logoutFuture.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("socket[{}]摄像头关闭被中断", session.getId());
            throw e;
        } catch (ExecutionException e) {
            log.error("socket[{}]摄像头关闭发生异常,异常信息是：{}", session.getId(), e.getMessage());
        } catch (TimeoutException e) {
            log.error("socket[{}]摄像头关闭超时", session.getId());
        }
        // 如果没有正确关闭就再关闭一次
        if (!Optional.ofNullable(flag).orElse(false)) {
            logoutFuture.cancel(true);
            PreviewUntil.logoutPlayView(lUserId);
        }
        //log.info(String.format("socket[%s]摄像头已关闭", session.getId()));
    }

    @OnError
    public void onError(Throwable error, Session session) {
        log.error("socket[{}]发生错误,错误消息是：{}", session.getId(), error.getMessage());
        try {
            session.close();
        } catch (IOException e) {
            log.error("socket[{}]关闭失败,原因是：{}", session.getId(), error.getMessage());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMsg(String message) {
        log.info("服务端收到客户端[{}]的消息：{}", session.getId(), message);
    }

    /**
     * 服务端发送消息给客户端
     */
    public void sendMessage(BlockingQueue<byte[]> blockingQueue) {
        while (session.isOpen() && null != blockingQueue) {
            try {
                byte[] esBytes = blockingQueue.take();
                if (esBytes.length < 1) {
                    log.error("取流失败，无内容");
                    continue;
                }
                ByteBuffer data = ByteBuffer.wrap(esBytes);
                session.getBasicRemote().sendBinary(data);
            } catch (IOException e) {
                //log.error("socket[" + session.getId() + "]数据发失败，错误信息为：" + e.getMessage());
                return;
            } catch (InterruptedException e) {
                return;
            }
        }
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
