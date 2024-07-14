package com.jkzz.smart_mines.communication.HslCommunication.Profinet.Melsec.Helper;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Address.McAddressData;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.IReadWriteDevice;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.IByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;

/**
 * 三菱MC协议的设备接口对象
 */
public interface IReadWriteMc extends IReadWriteDevice {

    /**
     * 获取网络号
     *
     * @return 网络号
     */
    public byte getNetworkNumber();

    /**
     * 设置网络号
     *
     * @param networkNumber 网络号
     */
    public void setNetworkNumber(byte networkNumber);

    /**
     * 获取网络站号
     *
     * @return 网络站号
     */
    public byte getNetworkStationNumber();

    /**
     * 设置网络站号
     *
     * @param networkStationNumber 网络站号
     */
    public void setNetworkStationNumber(byte networkStationNumber);

    /**
     * 当前MC协议的分析地址的方法，对传入的字符串格式的地址进行数据解析。<br />
     * The current MC protocol's address analysis method performs data parsing on the address of the incoming string format.
     *
     * @param address 地址信息
     * @param length  数据长度
     * @return 解析后的数据信息
     */
    OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length);


    /**
     * 获取当前的数据变换信息
     *
     * @return 数据变换信息
     */
    public IByteTransform getByteTransform();

    /**
     * 当前的MC协议的格式类型<br />
     * The format type of the current MC protocol
     *
     * @return PLC的协议类型
     */
    McType getMcType();

    /**
     * 从PLC反馈的数据中提取出实际的数据内容，需要传入反馈数据，是否位读取
     *
     * @param response 反馈的数据内容
     * @param isBit    是否位读取
     * @return 解析后的结果对象
     */
    byte[] ExtractActualData(byte[] response, boolean isBit);
}
