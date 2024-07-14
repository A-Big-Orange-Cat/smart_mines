package com.jkzz.smart_mines.communication.HslCommunication.ModBus;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.IReadWriteDevice;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.DataFormat;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;

/**
 * Modbus设备的接口，用来表示Modbus相关的设备对象，{@link ModbusTcpNet},{@link ModbusRtuOverTcp}均实现了该接口信息<br />
 */
public interface IModbus extends IReadWriteDevice {
    /**
     * 获取起始地址是否从0开始
     *
     * @return bool值
     */
    boolean getAddressStartWithZero();

    /**
     * 设置起始地址是否从0开始
     *
     * @param addressStartWithZero true代表从0开始，false代表从1开始
     */
    void setAddressStartWithZero(boolean addressStartWithZero);

    /**
     * 获取站号
     *
     * @return 站号
     */
    byte getStation();

    /**
     * 设置站号
     *
     * @param station 站号
     */
    void setStation(byte station);

    /**
     * 获取多字节数据的反转类型，适用于int,float,double,long类型的数据
     *
     * @return
     */
    DataFormat getDataFormat();

    /**
     * 设置多字节数据的反转类型，适用于int,float,double,long类型的数据
     *
     * @param dataFormat 数据类型
     */
    void setDataFormat(DataFormat dataFormat);

    /**
     * 字符串数据是否发生反转
     *
     * @return bool值
     */
    boolean isStringReverse();

    /**
     * 设置字符串数据是否反转
     *
     * @param stringReverse bool值
     */
    void setStringReverse(boolean stringReverse);

    /**
     * 将当前的地址信息转换成Modbus格式的地址，如果转换失败，返回失败的消息。默认不进行任何的转换。<br />
     * Convert the current address information into a Modbus format address. If the conversion fails, a failure message will be returned. No conversion is performed by default.
     *
     * @param address    传入的地址
     * @param modbusCode Modbus的功能码
     * @return 转换之后Modbus的地址
     */
    OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode);
}

