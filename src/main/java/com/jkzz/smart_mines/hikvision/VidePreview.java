package com.jkzz.smart_mines.hikvision;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static com.jkzz.smart_mines.hikvision.PreviewUntil.hCNetSDK;

@Slf4j
public class VidePreview {

    /**
     * 预览句柄
     */
    public static int lPlay = -1;
    /**
     * 预览是否成功
     */
    public static boolean lPlayStatus = false;
    /**
     * 预览错误信息
     */
    public static String lPlayErrorMassage = "";
    /**
     * 预览回调函数实现
     */
    static FRealDataCallBack fRealDataCallBack;
    /**
     * 裸码流回调函数
     */
    static fPlayEScallback fPlayescallback;

    /**
     * 预览摄像头
     *
     * @param lUserId    登录时返回的id
     * @param iChannelNo 通过哪个通道预览
     */
    public static void RealPlay(Integer lUserId, Integer iChannelNo) {
        if (null == lUserId || null == iChannelNo || lUserId == -1) {
            lPlayStatus = false;
            lPlayErrorMassage = "请先登录";
            return;
        }
        HCNetSDK.NET_DVR_PREVIEWINFO strClientInfo = new HCNetSDK.NET_DVR_PREVIEWINFO();
        strClientInfo.read();
        strClientInfo.hPlayWnd = null;      // 窗口句柄，从回调取流不显示一般设置为空
        strClientInfo.lChannel = iChannelNo;// 通道号
        strClientInfo.dwStreamType = 0;     // 0-主码流，1-子码流，2-三码流，3-虚拟码流，以此类推
        strClientInfo.dwLinkMode = 0;       // 连接方式：0- TCP方式，1- UDP方式，2- 多播方式，3- RTP方式，4- RTP/RTSP，5- RTP/HTTP，6- HRUDP（可靠传输） ，7- RTSP/HTTPS，8- NPQ
        strClientInfo.bBlocked = 0;         // 0-非阻塞取流, 1-阻塞取流, 如果阻塞SDK内部connect失败将会有5s的超时才能够返回,不适合于轮询取流操作
        strClientInfo.byProtoType = 1;      // 应用层取流协议，0-私有协议，1-RTSP协议
        strClientInfo.write();

        // 回调函数定义必须是全局的
        if (fRealDataCallBack == null) {
            fRealDataCallBack = new FRealDataCallBack();
        }

        // 开启预览
        lPlay = hCNetSDK.NET_DVR_RealPlay_V40(lUserId, strClientInfo, fRealDataCallBack, null);
        if (lPlay == -1) {
            int iErr = hCNetSDK.NET_DVR_GetLastError();
            lPlayStatus = false;
            lPlayErrorMassage = "取流失败,错误码：" + iErr;
            return;
        }
        lPlayStatus = true;
    }

    /**
     * 预览回调
     */
    static class FRealDataCallBack implements HCNetSDK.FRealDataCallBack_V30 {
        /**
         * 多路视频的pes数据进行缓存，直到某一路视频的RTP包开头进入时进行取出返给前端
         */
        final Map<Integer, byte[]> EsBytesMap = new HashMap<>();

        /**
         * 预览回调
         *
         * @param lRealHandle 预览句柄
         * @param dwDataType  数据类型
         * @param pBuffer     存放数据的缓冲区指针
         * @param dwBufSize   缓冲区大小
         * @param pUser       用户数据
         */
        public void invoke(int lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, Pointer pUser) {
            // 播放库解码
            switch (dwDataType) {
                case HCNetSDK.NET_DVR_SYSHEAD:      // 系统头
                case HCNetSDK.NET_DVR_STREAMDATA:   // 码流数据
                    if ((dwBufSize > 0)) {
                        byte[] outputData = pBuffer.getPointer().getByteArray(0, dwBufSize);
                        // 将流写入对应的实体
                        writeESH264(outputData, lRealHandle);
                    }
            }
        }

        /**
         * 提取H264的裸流写入文件
         *
         * @param outputData 视频裸流数据
         */
        public void writeESH264(final byte[] outputData, int lRealHandle) {
            if (outputData.length == 0) {
                return;
            }
            // 当前这个通道的一个Rtp包数据
            byte[] allEsBytes = null;
            if (!EsBytesMap.containsKey(lRealHandle)) {
                EsBytesMap.put(lRealHandle, allEsBytes);
            } else {
                allEsBytes = EsBytesMap.get(lRealHandle);
            }

            if ((outputData[0] & 0xff) == 0x00
                    && (outputData[1] & 0xff) == 0x00
                    && (outputData[2] & 0xff) == 0x01
                    && (outputData[3] & 0xff) == 0xBA) {
                // RTP包开头
                // 一个完整的帧解析完成后将解析的数据放入BlockingQueue,websocket获取后发送给前端
                if (allEsBytes != null && allEsBytes.length > 0) {
                    if (MyBlockingQueue.LPlayIdToLUserIdMap.containsKey(lRealHandle)) {
                        Integer lUserId = MyBlockingQueue.LPlayIdToLUserIdMap.get(lRealHandle);
                        BlockingQueue<byte[]> blockingQueue = MyBlockingQueue.bpMap.get(lUserId);
                        // 将当前的某一路视频通道的上一个Rtp包放到队列中去
                        blockingQueue.offer(allEsBytes);
                        allEsBytes = null;
                        // 置空当前通道的RTP包，下次回调就是pes包进行取流追加
                        EsBytesMap.put(lRealHandle, allEsBytes);
                    }
                }
            }

            // 是00 00 01 eo开头的就是视频的pes包
            if ((outputData[0] & 0xff) == 0x00//
                    && (outputData[1] & 0xff) == 0x00
                    && (outputData[2] & 0xff) == 0x01
                    && (outputData[3] & 0xff) == 0xE0) {
                // 去掉包头后的起始位置
                int from = 9 + outputData[8] & 0xff;
                int len = outputData.length - 9 - (outputData[8] & 0xff);
                // 获取es裸流
                byte[] esBytes = new byte[len];
                System.arraycopy(outputData, from, esBytes, 0, len);

                if (allEsBytes == null) {
                    allEsBytes = esBytes;
                } else {
                    byte[] newEsBytes = new byte[allEsBytes.length + esBytes.length];
                    System.arraycopy(allEsBytes, 0, newEsBytes, 0, allEsBytes.length);
                    System.arraycopy(esBytes, 0, newEsBytes, allEsBytes.length, esBytes.length);
                    allEsBytes = newEsBytes;
                }
                // 当前视频通道的部分包数据进行缓存
                EsBytesMap.put(lRealHandle, allEsBytes);
            }
        }
    }

    //设置裸码流回调函数
    static class fPlayEScallback implements HCNetSDK.FPlayESCallBack {
        @Override
        public void invoke(int lPlayHandle, HCNetSDK.NET_DVR_PACKET_INFO_EX struPackInfo, Pointer pUser) {
            System.out.println("进入码流回调");
            System.out.println(struPackInfo.dwPacketSize);

            /*try {
                fileLenth += pstruPackInfo.dwPacketSize;
                fileOutputStream = new FileOutputStream(resultFileName, true);
                //将字节写入文件
                ByteBuffer buffers = pstruPackInfo.pPacketBuffer.getByteBuffer(0, pstruPackInfo.dwPacketSize);
                byte[] bytes = new byte[pstruPackInfo.dwPacketSize];
                buffers.rewind();
                buffers.get(bytes);
                fileOutputStream.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

}
