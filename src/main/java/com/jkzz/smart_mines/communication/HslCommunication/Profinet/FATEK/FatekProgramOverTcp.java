package com.jkzz.smart_mines.communication.HslCommunication.Profinet.FATEK;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.RegularByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;

public class FatekProgramOverTcp extends NetworkDeviceBase {
    // region Constructor

    /// <summary>
    /// PLC的站号信息，需要和实际的设置值一致，默认为1<br />
    /// The station number information of the PLC needs to be consistent with the actual setting value. The default is 1.
    /// </summary>
    private byte Station = 0;
    private byte station = 0x01;                 // PLC的站号信息

    // endregion

    // region Public Member

    /**
     * 实例化默认的构造方法<br />
     * Instantiate the default constructor
     */
    public FatekProgramOverTcp() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
    }

    /**
     * 使用指定的ip地址和端口来实例化一个对象<br />
     * Instantiate an object with the specified IP address and port
     *
     * @param ipAddress 设备的Ip地址
     * @param port      设备的端口号
     */
    public FatekProgramOverTcp(String ipAddress, int port) {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    public byte getStation() {
        return Station;
    }

    // endregion

    // region Read Write Support

    public void setStation(byte station) {
        Station = station;
    }

    public OperateResultExOne<byte[]> Read(String address, short length) {
        return FatekProgramHelper.Read(this, getStation(), address, length);
    }

    // endregion

    // region Bool Read Write

    public OperateResult Write(String address, byte[] value) {
        return FatekProgramHelper.Write(this, getStation(), address, value);
    }

    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return FatekProgramHelper.ReadBool(this, getStation(), address, length);
    }

    // endregion

    // region Object Override

    public OperateResult Write(String address, boolean[] value) {
        return FatekProgramHelper.Write(this, getStation(), address, value);
    }

    // endregion

    // region Private Member

    public String toString() {
        return "FatekProgramOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }

    // endregion


}
