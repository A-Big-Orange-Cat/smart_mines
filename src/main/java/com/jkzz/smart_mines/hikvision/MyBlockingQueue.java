package com.jkzz.smart_mines.hikvision;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MyBlockingQueue {

    public static final Map<Integer, HikWebSocket> LUserIdToWebSocketMap = new ConcurrentHashMap<>();
    public static final Map<Integer, Integer> LPlayIdToLUserIdMap = new ConcurrentHashMap<>();
    public static final Map<Integer, BlockingQueue<byte[]>> bpMap = new HashMap<>();

    /**
     * 通过摄像头用户句柄进行数据清理
     *
     * @param lUserId 用户句柄
     */
    static public void clearByLUserId(Integer lUserId) {
        for (Integer key : LPlayIdToLUserIdMap.keySet()) {
            Integer value = LPlayIdToLUserIdMap.get(key);
            if (value.equals(lUserId)) {
                LPlayIdToLUserIdMap.remove(key);
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
    static public Integer findLPlayIdByUserId(Integer lUserId) {
        for (Integer key : LPlayIdToLUserIdMap.keySet()) {
            Integer value = LPlayIdToLUserIdMap.get(key);
            if (value.equals(lUserId)) {
                return key;
            }
        }
        return null;
    }

}
