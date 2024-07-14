package com.jkzz.smart_mines.communication.HslCommunication.Profinet.Siemens;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.IReadWriteDevice;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.ReverseBytesTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Profinet.Siemens.Helper.SiemensPPIHelper;

public class SiemensPPIOverTcp extends NetworkDeviceBase {


    private byte station = 0x02;            // PLC的站号信息
    private Object communicationLock;       // 通讯锁

    public SiemensPPIOverTcp() {
        this.WordLength = 2;
        this.setByteTransform(new ReverseBytesTransform());
        this.communicationLock = new Object();
        this.setSleepTime(20);
    }

    /**
     * 使用指定的ip地址和端口号来实例化对象<br />
     * Instantiate the object with the specified IP address and port number
     *
     * @param ipAddress Ip地址信息
     * @param port      端口号信息
     */
    public SiemensPPIOverTcp(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    /**
     * 获取站号信息
     *
     * @return 站号数据
     */
    public byte getStation() {
        return station;
    }

    /**
     * 设置站号信息
     *
     * @param station 新的站号的数据
     */
    public void setStation(byte station) {
        this.station = station;
    }

    /**
     * @see SiemensPPIHelper#Read(IReadWriteDevice, String, short, byte, Object)
     */
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return SiemensPPIHelper.Read(this, address, length, this.station, this.communicationLock);
    }

    /**
     * @see SiemensPPIHelper#ReadBool(IReadWriteDevice, String, short, byte, Object)
     */
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return SiemensPPIHelper.ReadBool(this, address, length, this.station, this.communicationLock);
    }

    /**
     * @see SiemensPPIHelper#Write(IReadWriteDevice, String, byte[], byte, Object)
     */
    public OperateResult Write(String address, byte[] value) {
        return SiemensPPIHelper.Write(this, address, value, this.station, this.communicationLock);
    }

    /**
     * @see SiemensPPIHelper#Write(IReadWriteDevice, String, boolean[], byte, Object)
     */
    public OperateResult Write(String address, boolean[] value) {
        return SiemensPPIHelper.Write(this, address, value, this.station, this.communicationLock);
    }

    /**
     * @see SiemensS7Net#ReadByte(String)
     */
    public OperateResultExOne<Byte> ReadByte(String address) {
        OperateResultExOne<byte[]> read = Read(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content[0]);
    }

    public OperateResult Write(String address, byte value) {
        return Write(address, new byte[]{value});
    }

    /**
     * @see SiemensPPIHelper#Start(IReadWriteDevice, String, byte, Object)
     */
    public OperateResult Start(String parameter) {
        return SiemensPPIHelper.Start(this, parameter, this.station, this.communicationLock);
    }

    /**
     * @see SiemensPPIHelper#Stop(IReadWriteDevice, String, byte, Object)
     */
    public OperateResult Stop(String parameter) {
        return SiemensPPIHelper.Stop(this, parameter, this.station, this.communicationLock);
    }

    public String toString() {
        return "SiemensPPIOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }
}
