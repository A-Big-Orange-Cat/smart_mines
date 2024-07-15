package com.jkzz.smart_mines.hikvision;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class MyBlockingQueue {

    protected static final Map<Integer, HikWebSocket> LUserIdToWebSocketMap = new ConcurrentHashMap<>();
    protected static final Map<Integer, Integer> LPlayIdToLUserIdMap = new ConcurrentHashMap<>();
    protected static final Map<Integer, BlockingQueue<byte[]>> bpMap = new HashMap<>();
    private MyBlockingQueue() {

    }

    /**
     * 通过摄像头用户句柄进行数据清理
     *
     * @param lUserId 用户句柄
     */
    public static void clearByLUserId(Integer lUserId) {
        for (Map.Entry<Integer, Integer> entry : LPlayIdToLUserIdMap.entrySet()) {
            if (entry.getValue().equals(lUserId)) {
                LPlayIdToLUserIdMap.remove(entry.getKey());
                break;
            }
        }
        LUserIdToWebSocketMap.remove(lUserId);
        Optional.ofNullable(bpMap.remove(lUserId))
                .ifPresent(Collection::clear);
    }

    /**
     * 通过摄像头用户句柄查询当前用户是否正在预览
     *
     * @param lUserId 用户句柄
     * @return 预览句柄
     */
    public static Integer findLPlayIdByUserId(Integer lUserId) {
        for (Map.Entry<Integer, Integer> entry : LPlayIdToLUserIdMap.entrySet()) {
            if (entry.getValue().equals(lUserId)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
