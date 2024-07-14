package com.jkzz.smart_mines.communication.HslCommunication.Core.Types;

import java.net.Socket;
import java.util.Date;

public class HslTimeOut {

    /**
     * 操作的开始时间
     */
    public Date StartTime = null;
    /**
     * 操作是否成功
     */
    public boolean IsSuccessful = false;
    /**
     * 延时的时间，单位：毫秒
     */
    public int DelayTime = 5000;
    /**
     * 当前的网络通讯的核心
     */
    public Socket WorkSocket = null;
    /**
     * 是否发生了超时的操作
     */
    public boolean IsTimeout = false;

    /**
     * 默认的无参构造函数
     */
    public HslTimeOut() {
        StartTime = new Date();
        IsSuccessful = false;
    }

    @Override
    public String toString() {
        return "HslTimeOut[" + DelayTime + "]";
    }
}
