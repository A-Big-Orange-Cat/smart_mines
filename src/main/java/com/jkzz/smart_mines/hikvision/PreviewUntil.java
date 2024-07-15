package com.jkzz.smart_mines.hikvision;

import com.jkzz.smart_mines.pojo.domain.DeviceDvr;
import com.jkzz.smart_mines.utils.LibLoaderUtil;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
public class PreviewUntil {

    /**
     * 是否初始化
     */
    static boolean isInit = false;
    /**
     * 异常捕获回调
     */
    static FExceptionCallBackImp fExceptionCallBack;
    static HCNetSDK hCNetSDK = null;
    static PlayCtrl playControl = null;
    /**
     * 是否加载海康SDK
     */
    private static boolean loadOn;

    /**
     * 动态库加载
     *
     * @return 是否加载成功
     */
    private static boolean createSDKInstance() {
        if (hCNetSDK == null) {
            synchronized (HCNetSDK.class) {
                String strDllPath = CommonKit.getHikPath();
                try {
                    // win系统加载库路径
                    strDllPath = strDllPath + "HCNetSDK.dll";
                    hCNetSDK = (HCNetSDK) Native.loadLibrary(strDllPath, HCNetSDK.class);
                } catch (Exception ex) {
                    log.error("loadLibrary: {},Error:{}", strDllPath, ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 播放库加载
     *
     * @return 是否加载成功
     */
    private static boolean createPlayInstance() {
        if (playControl == null) {
            synchronized (PlayCtrl.class) {
                String strPlayPath = CommonKit.getHikPath();
                try {
                    // win系统加载库路径
                    strPlayPath = strPlayPath + "PlayCtrl.dll";
                    playControl = (PlayCtrl) Native.loadLibrary(strPlayPath, PlayCtrl.class);
                } catch (Exception ex) {
                    log.error("loadLibrary: {},Error:{}", strPlayPath, ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 初始化
     */
    @PostConstruct
    public static void init() {
        if (!loadOn) {
            return;
        }
        try {
            LibLoaderUtil.loader("lib");
        } catch (IOException e) {
            LibLoaderUtil.loadStatus = false;
            log.error("加载lib文件出现异常，原因：{}", e.getMessage());
        }
        if (!LibLoaderUtil.loadStatus) {
            log.error("加载lib文件失败");
            return;
        }
        // SDK初始化，一个程序只需要调用一次
        if (hCNetSDK == null && playControl == null) {
            if (!createSDKInstance()) {
                log.error("加载SDK失败");
                return;
            }
            if (!createPlayInstance()) {
                log.error("加载PlayCtrl失败");
                return;
            }
        }

        //log.info("海康威视SDK dll加载成功");
        log.info("海康威视SDK初始化{}", hCNetSDK.NET_DVR_Init() ? "成功" : "失败");

        // 异常消息回调
        if (fExceptionCallBack == null) {
            fExceptionCallBack = new FExceptionCallBackImp();
        }
        if (!hCNetSDK.NET_DVR_SetExceptionCallBack_V30(0, 0, fExceptionCallBack, null)) {
            return;
        }
        //log.info("设置异常消息回调成功");

        // 启动SDK写日志
        hCNetSDK.NET_DVR_SetLogToFile(3, ".\\Application\\sdkLog\\", false);
        isInit = true;
    }

    /**
     * 设备登录并播放摄像头视频
     *
     * @param dvr 摄像头信息
     */
    public static void loginAndPlayView(DeviceDvr dvr) {
        if (!isInit) {
            return;
        }
        // 登录
        loginV40(dvr);
        playView(dvr);
    }

    /**
     * 多设备登录V40，与V30功能一致（支持批量操作）
     *
     * @param dvrs 摄像头信息列表：ip地址，端口号，用户名，密码
     */
    public static void loginV40(List<DeviceDvr> dvrs) {
        if (!isInit) {
            return;
        }
        if (null == dvrs || dvrs.isEmpty()) {
            return;
        }
        for (DeviceDvr dvr : dvrs) {
            // 如果已经登录，就先退出登陆
            /*Integer lUserId = MyBlockingQueue.findUserIdByIp(dvr.getIp());
            if (null != lUserId) {
                // 自动判断是否在预览并退出
                PreviewUntil.logoutPlayView(dvr.getLUserId());
            }*/
            loginV40(dvr);
        }
    }

    /**
     * 设备登录V40，与V30功能一致
     *
     * @param dvr 摄像头信息：ip地址，端口号，用户名，密码
     */
    private static void loginV40(DeviceDvr dvr) {
        if (!isInit) {
            return;
        }
        // 登录设备，每一台设备分别登录; 登录句柄是唯一的，可以区分设备
        // 设备登录信息
        HCNetSDK.NET_DVR_USER_LOGIN_INFO mStrLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();
        // 设备信息
        HCNetSDK.NET_DVR_DEVICEINFO_V40 mStrDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();

        // 设备ip地址
        String mSDeviceIp = dvr.getIp();
        mStrLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(mSDeviceIp.getBytes(), 0, mStrLoginInfo.sDeviceAddress, 0, mSDeviceIp.length());

        // 设备用户名
        String mSUsername = dvr.getUserName();
        mStrLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(mSUsername.getBytes(), 0, mStrLoginInfo.sUserName, 0, mSUsername.length());

        // 设备密码
        String mSPassword = dvr.getPassword();
        mStrLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(mSPassword.getBytes(), 0, mStrLoginInfo.sPassword, 0, mSPassword.length());

        // SDK端口号
        mStrLoginInfo.wPort = dvr.getPort().shortValue();
        // 是否异步登录：0- 否，1- 是
        mStrLoginInfo.bUseAsynLogin = false;
        // 0- SDK私有协议，1- ISAPI协议
        mStrLoginInfo.byLoginMode = 0;
        mStrLoginInfo.write();

        int lUserId = hCNetSDK.NET_DVR_Login_V40(mStrLoginInfo, mStrDeviceInfo);
        if (lUserId == -1) {
            String errorStr = dvr.getIp() + "登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError();
            log.error(errorStr);
            dvr.setLoginStatus("0");
            dvr.setLoginMessage(errorStr);
            dvr.setLUserId(-1);
            dvr.setLDChannel(-1);
        } else {
            // 这里直接认为登陆成功就能预览吧。。
            String successStr = mSDeviceIp + ":设备登录成功! " + "设备序列号:" +
                    new String(mStrDeviceInfo.struDeviceV30.sSerialNumber).trim();
            //log.error(successStr);
            dvr.setLoginStatus("1");
            dvr.setLoginMessage(successStr);
            dvr.setLUserId(lUserId);
            dvr.setLDChannel(1);
            mStrDeviceInfo.read();
            // 记录ip已经登录
            // MyBlockingQueue.IpToLUserIdMap.put(dvr.getIp(), dvr.getLUserId());
            // 相机一般只有一个通道号，热成像相机有2个通道号，通道号为1或1,2
            // byStartDChan为IP通道起始通道号, 预览回放NVR的IP通道时需要根据起始通道号进行取值
            if (mStrDeviceInfo.struDeviceV30.byStartChan == 1 || mStrDeviceInfo.struDeviceV30.byStartChan == 33) {
                // byStartDChan为IP通道起始通道号, 预览回放NVR的IP通道时需要根据起始通道号进行取值,NVR起始通道号一般是33或者1开始
                int lDChannel = mStrDeviceInfo.struDeviceV30.byStartChan;
                dvr.setLDChannel(lDChannel);
            }
        }
    }

    /**
     * 实时预览某个摄像头
     *
     * @param dvr 摄像头信息
     */
    public static void playView(DeviceDvr dvr) {
        if (!isInit) {
            return;
        }
        // 预览
        VidePreview.realPlay(dvr.getLUserId(), dvr.getLDChannel());
        if (VidePreview.lPlayStatus) {
            dvr.setPlayStatus("1");
            dvr.setPlayMessage("预览请求成功！");
            dvr.setLPlayId(VidePreview.lPlay);
            // 创建数据体，等待视频流实时回调
            BlockingQueue<byte[]> bq = new ArrayBlockingQueue<>(10);
            MyBlockingQueue.bpMap.put(dvr.getLUserId(), bq);
            MyBlockingQueue.LPlayIdToLUserIdMap.put(dvr.getLPlayId(), dvr.getLUserId());
        } else {
            String errorStr = dvr.getIp() + "预览失败，错误码为" + VidePreview.lPlayErrorMassage;
            log.error(errorStr);
            dvr.setPlayStatus("0");
            dvr.setPlayMessage(errorStr);
            dvr.setLPlayId(-1);
            PreviewUntil.logoutPlayView(dvr.getLUserId());
        }
    }

    /**
     * 某个摄像头退出登录，自动判断是否在预览并停止
     *
     * @param lUserId 用户句柄
     */
    public static boolean logoutPlayView(Integer lUserId) {
        if (null != hCNetSDK && MyBlockingQueue.bpMap.containsKey(lUserId)) {
            Integer lPlayId = MyBlockingQueue.findLPlayIdByUserId(lUserId);
            //log.info("开始停止预览并退出登录");
            if (null == lPlayId || !hCNetSDK.NET_DVR_StopRealPlay(lPlayId)) { // 这个方法可能会卡死
                log.error("停止预览失败");
            }
            // 退出程序时调用，注销登录
            if (!hCNetSDK.NET_DVR_Logout(lUserId)) {
                log.error("注销登录失败");
            }
            //log.info("停止预览并退出登录成功");
            // 清理数据体
            MyBlockingQueue.clearByLUserId(lUserId);
        }
        return true;
    }

    /**
     * 只关闭某个摄像头预览，但不退出登录
     *
     * @param dvr 摄像头信息
     */
    public static void logoutPlayViewOnly(DeviceDvr dvr) {
        if (null != hCNetSDK) {
            if (null != dvr.getLPlayId()) {
                if (hCNetSDK.NET_DVR_StopRealPlay(dvr.getLPlayId())) {
                    log.info("停止预览成功");
                    dvr.setLoginStatus("0");
                    dvr.setPlayMessage("停止预览成功");
                    dvr.setLPlayId(null);
                } else {
                    // 接口返回失败请调用NET_DVR_GetLastError获取错误码，通过错误码判断出错原因。
                    dvr.setPlayMessage("停止预览失败：接口返回失败请调用NET_DVR_GetLastError获取错误码，通过错误码判断出错原因。");
                }
            }
            // 清理数据体
            MyBlockingQueue.clearByLUserId(dvr.getLUserId());
        }
    }

    /**
     * 某个摄像头关闭websocket
     *
     * @param lUserId 用户句柄
     */
    public static void shutdown(Integer lUserId) {
        if (MyBlockingQueue.LUserIdToWebSocketMap.containsKey(lUserId)) {
            HikWebSocket hikWebSocket = MyBlockingQueue.LUserIdToWebSocketMap.get(lUserId);
            try {
                hikWebSocket.getSession().close();
            } catch (IOException e) {
                log.error("socket[{}]关闭失败,原因是：{}", hikWebSocket.getSession().getId(), e.getMessage());
            }
        }
    }

    /**
     * 全部摄像头关闭websocket
     */
    public static void shutdown() {
        List<Integer> lUserIds = new ArrayList<>(MyBlockingQueue.LUserIdToWebSocketMap.keySet());
        lUserIds.forEach(PreviewUntil::shutdown);
    }

    @Value("#{T(java.lang.Boolean).parseBoolean('${system.hik.sdk.load}')}")
    public void setLoadOn(boolean loadOn) {
        PreviewUntil.loadOn = loadOn;
    }

    /**
     * 释放资源
     */
    @PreDestroy
    public void cleanup() {
        if (null != hCNetSDK) {
            log.info("SDK反初始化，释放资源{}", hCNetSDK.NET_DVR_Cleanup() ? "成功" : "失败");
        }
    }

    /**
     * 异常信息捕获接受类
     */
    static class FExceptionCallBackImp implements HCNetSDK.FExceptionCallBack {
        public void invoke(int dwType, int lUserId, int lHandle, Pointer pUser) {
            switch (dwType) {
                case HCNetSDK.EXCEPTION_AUDIOEXCHANGE:      // 语音对讲时网络异常
                    log.error("用户句柄:{}语音对讲异常", lUserId);
                    break;
                case HCNetSDK.EXCEPTION_ALARM:              // 报警上传时网络异常
                    log.error("用户句柄:{}报警上传时网络异常", lUserId);
                    break;
                case HCNetSDK.EXCEPTION_PREVIEW:            // 网络预览时异常
                    log.error("用户句柄:{}网络预览时异常", lUserId);
                    logoutPlayView(lUserId);
                    break;
                case HCNetSDK.EXCEPTION_SERIAL:             // 透明通道传输时异常
                    log.error("用户句柄:{}透明通道传输时异常", lUserId);
                    logoutPlayView(lUserId);
                    break;
                case HCNetSDK.EXCEPTION_RECONNECT:          // 预览时重连
                    log.error("用户句柄:{}预览时重连", lUserId);
                    break;
                default:
                    log.error("用户句柄:{},异常事件类型:{}", lUserId, Integer.toHexString(dwType));
                    log.error("具体错误参照 SDK网络使用手册中：NET_DVR_SetExceptionCallBack_V30 方法中的异常定义！");
                    break;
            }
        }
    }

}
