package com.jkzz.smart_mines.communication.net;

import com.jkzz.smart_mines.communication.Result.ReadResult;
import com.jkzz.smart_mines.communication.Result.WriteResult;

public interface CommunicationNet {

    /**
     * 创建通信对象
     *
     * @param ip   ip地址
     * @param port 端口号
     */
    void createCommunicationNet(String ip, int port);

    /**
     * 修改ip地址或端口号
     *
     * @param ip   ip地址
     * @param port 端口号
     */
    void setCommunicationNet(String ip, int port);

    /**
     * 获取连接状态
     */
    boolean getConn();

    /**
     * 判断是否处于连接状态
     */
    void isConn();

    /**
     * 关闭长连接
     */
    void disconnect();

    /**
     * 读16位有符号整数
     *
     * @param address 读取地址
     * @return 读取的值
     */
    ReadResult<Short> readInt16(String address);

    /**
     * 读16位无符号整数
     *
     * @param address 读取地址
     * @return 读取的值
     */
    ReadResult<Integer> readUInt16(String address);

    /**
     * 读多个16位无符号整数
     *
     * @param address 读取起始地址
     * @param length  读取长度
     * @return 读取的值列表
     */
    ReadResult<int[]> readUInt16(String address, Integer length);

    /**
     * 读bool
     *
     * @param address 读取地址
     * @return 读取的值
     */
    ReadResult<Boolean> readBool(String address);

    /**
     * 写16位有符号整数
     *
     * @param address 写入地址
     * @param value   写入的值
     * @return 写入结果
     */
    WriteResult write(String address, Integer value);

    /**
     * 写16位无符号整数
     *
     * @param address 写入地址
     * @param value   写入的值
     * @return 写入结果
     */
    WriteResult write(String address, Long value);

    /**
     * 写bool
     *
     * @param address 写入地址
     * @param value   写入的值
     * @return 写入结果
     */
    WriteResult write(String address, Boolean value);

}
