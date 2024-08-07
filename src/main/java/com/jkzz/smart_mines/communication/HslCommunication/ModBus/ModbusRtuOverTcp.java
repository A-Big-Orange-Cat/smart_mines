package com.jkzz.smart_mines.communication.HslCommunication.ModBus;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.DataFormat;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.ReverseWordTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;

/**
 * Modbus-Rtu通讯协议的TCP实现，多项式码0xA001，支持标准的功能码，也支持扩展的功能码实现，地址采用富文本的形式，详细见备注说明<br />
 * The TCP implementation of Modbus-Rtu communication protocol, polynomial code 0xA001, supports standard function codes,
 * and also supports extended function code realization. The address is in the form of rich text. See the remarks for details.
 */
public class ModbusRtuOverTcp extends NetworkDeviceBase implements IModbus {

    //region Constructor

    private byte station = 0x01;                                 // 本客户端的站号
    private boolean isAddressStartWithZero = true;               // 线圈值的地址值是否从零开始

    //endregion

    //region Private Member

    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public ModbusRtuOverTcp() {
        setByteTransform(new ReverseWordTransform());
        WordLength = 1;
        station = 1;
        setSleepTime(20);
    }

    /**
     * 通过指定IP地址，端口，站号来实例化一个对象<br />
     * Instantiate an object by specifying IP address, port, station number
     *
     * @param ipAddress IP地址
     * @param port      端口号
     * @param station   站号
     */
    public ModbusRtuOverTcp(String ipAddress, int port, byte station) {
        setByteTransform(new ReverseWordTransform());
        setIpAddress(ipAddress);
        setPort(port);
        WordLength = 1;
        this.station = station;
        setSleepTime(20);
    }

    //endregion

    /**
     * 获取起始地址是否从0开始
     *
     * @return bool值
     */
    public boolean getAddressStartWithZero() {
        return isAddressStartWithZero;
    }

    /**
     * 设置起始地址是否从0开始
     *
     * @param addressStartWithZero true代表从0开始，false代表从1开始
     */
    public void setAddressStartWithZero(boolean addressStartWithZero) {
        this.isAddressStartWithZero = addressStartWithZero;
    }

    /**
     * 获取站号
     *
     * @return 站号
     */
    public byte getStation() {
        return station;
    }

    /**
     * 设置站号
     *
     * @param station 站号
     */
    public void setStation(byte station) {
        this.station = station;
    }

    /**
     * 获取多字节数据的反转类型，适用于int,float,double,long类型的数据
     *
     * @return
     */
    public DataFormat getDataFormat() {
        return getByteTransform().getDataFormat();
    }

    /**
     * 设置多字节数据的反转类型，适用于int,float,double,long类型的数据
     *
     * @param dataFormat 数据类型
     */
    public void setDataFormat(DataFormat dataFormat) {
        getByteTransform().setDataFormat(dataFormat);
    }

    /**
     * 字符串数据是否发生反转
     *
     * @return bool值
     */
    public boolean isStringReverse() {
        return getByteTransform().getIsStringReverse();
    }

    /**
     * 设置字符串数据是否反转
     *
     * @param stringReverse bool值
     */
    public void setStringReverse(boolean stringReverse) {
        getByteTransform().setIsStringReverse(stringReverse);
    }


    // region Core Interative

    protected byte[] PackCommandWithHeader(byte[] command) {
        return ModbusInfo.PackCommandToRtu(command);
    }

    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return ModbusHelper.ExtraRtuResponseContent(send, response);
    }

    @Override
    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode) {
        return OperateResultExOne.CreateSuccessResult(address);
    }

    // endregion

    //region Read Write Support


    /**
     * 读取线圈，需要指定起始地址，如果富文本地址不指定，默认使用的功能码是 0x01<br />
     * To read the coil, you need to specify the start address. If the rich text address is not specified, the default function code is 0x01.
     *
     * @param address 起始地址，格式为"1234"
     * @return 带有成功标志的bool对象
     */
    public OperateResultExOne<Boolean> ReadCoil(String address) {
        return ReadBool(address);
    }

    ;

    /**
     * 批量的读取线圈，需要指定起始地址，读取长度，如果富文本地址不指定，默认使用的功能码是 0x01<br />
     * For batch reading coils, you need to specify the start address and read length. If the rich text address is not specified, the default function code is 0x01.
     *
     * @param address 起始地址，格式为"1234"
     * @param length  读取长度
     * @return 带有成功标志的bool数组对象
     */
    public OperateResultExOne<boolean[]> ReadCoil(String address, short length) {
        return ReadBool(address, length);
    }

    /**
     * 读取输入线圈，需要指定起始地址，如果富文本地址不指定，默认使用的功能码是 0x02<br />
     * To read the input coil, you need to specify the start address. If the rich text address is not specified, the default function code is 0x02.
     *
     * @param address 起始地址，格式为"1234"
     * @return 带有成功标志的bool对象
     */
    public OperateResultExOne<Boolean> ReadDiscrete(String address) {
        OperateResultExOne<boolean[]> read = ReadDiscrete(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content[0]);
    }

    /**
     * 批量的读取输入点，需要指定起始地址，读取长度，如果富文本地址不指定，默认使用的功能码是 0x02<br />
     * To read input points in batches, you need to specify the start address and read length. If the rich text address is not specified, the default function code is 0x02
     *
     * @param address 起始地址，格式为"1234"
     * @param length  读取长度
     * @return 带有成功标志的bool数组对象
     */
    public OperateResultExOne<boolean[]> ReadDiscrete(String address, short length) {
        return ModbusHelper.ReadBoolHelper(this, address, length, ModbusInfo.ReadDiscrete);
    }

    /**
     * 从Modbus服务器批量读取寄存器的信息，需要指定起始地址，读取长度，如果富文本地址不指定，默认使用的功能码是 0x03，如果需要使用04功能码，那么地址就写成 x=4;100<br />
     * To read the register information from the Modbus server in batches, you need to specify the start address and read length. If the rich text address is not specified,
     * the default function code is 0x03. If you need to use the 04 function code, the address is written as x = 4; 100
     *
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param length  读取的数量
     * @return 带有成功标志的字节信息
     */
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return ModbusHelper.Read(this, address, length);
    }

    /**
     * 将数据写入到Modbus的寄存器上去，需要指定起始地址和数据内容，如果富文本地址不指定，默认使用的功能码是 0x10<br />
     * To write data to Modbus registers, you need to specify the start address and data content. If the rich text address is not specified, the default function code is 0x10
     *
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param value   写入的数据，长度根据data的长度来指示
     * @return 返回写入结果
     */
    public OperateResult Write(String address, byte[] value) {
        return ModbusHelper.Write(this, address, value);
    }

    /**
     * 将数据写入到Modbus的单个寄存器上去，需要指定起始地址和数据值，如果富文本地址不指定，默认使用的功能码是 0x06<br />
     * To write data to a single register of Modbus, you need to specify the start address and data value. If the rich text address is not specified, the default function code is 0x06.
     *
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param value   写入的short数据
     * @return 是否写入成功
     */
    public OperateResult Write(String address, short value) {
        return ModbusHelper.Write(this, address, value);
    }

    /**
     * 向设备写入掩码数据，使用0x16功能码，需要确认对方是否支持相关的操作，掩码数据的操作主要针对寄存器。<br />
     * To write mask data to the server, using the 0x16 function code, you need to confirm whether the other party supports related operations.
     * The operation of mask data is mainly directed to the register.
     *
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param andMask 等待与操作的掩码数据
     * @param orMask  等待或操作的掩码数据
     * @return 是否写入成功
     */
    public OperateResult WriteMask(String address, short andMask, short orMask) {
        return ModbusHelper.WriteMask(this, address, andMask, orMask);
    }

    //endregion

    //region Write One Registe

    /**
     * 将数据写入到Modbus的单个寄存器上去，需要指定起始地址和数据值，如果富文本地址不指定，默认使用的功能码是 0x06<br />
     * To write data to a single register of Modbus, you need to specify the start address and data value. If the rich text address is not specified, the default function code is 0x06.
     *
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param value   写入的short数据
     * @return 是否写入成功
     */
    public OperateResult WriteOneRegister(String address, short value) {
        return Write(address, value);
    }

    //endregion

    // region Bool Support

    /**
     * 批量读取线圈或是离散的数据信息，需要指定地址和长度，具体的结果取决于实现，如果富文本地址不指定，默认使用的功能码是 0x01<br />
     * To read coils or discrete data in batches, you need to specify the address and length. The specific result depends on the implementation. If the rich text address is not specified, the default function code is 0x01.
     *
     * @param address 数据地址
     * @param length  数据长度
     * @return 带有成功标识的bool[]数组
     */
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return ModbusHelper.ReadBoolHelper(this, address, length, ModbusInfo.ReadCoil);
    }

    /**
     * 向线圈中写入bool数组，返回是否写入成功，如果富文本地址不指定，默认使用的功能码是 0x0F<br />
     * Write the bool array to the coil, and return whether the writing is successful. If the rich text address is not specified, the default function code is 0x0F.
     *
     * @param address 要写入的数据地址
     * @param values  要写入的实际数组
     * @return 返回写入结果
     */
    public OperateResult Write(String address, boolean[] values) {
        return ModbusHelper.Write(this, address, values);
    }

    /**
     * 向线圈中写入bool数值，返回是否写入成功，如果富文本地址不指定，默认使用的功能码是 0x05<br />
     * Write bool value to the coil and return whether the writing is successful. If the rich text address is not specified, the default function code is 0x05.
     *
     * @param address 要写入的数据地址
     * @param value   要写入的实际数据
     * @return 返回写入结果
     */
    public OperateResult Write(String address, boolean value) {
        return ModbusHelper.Write(this, address, value);
    }

    // endregion

    // region Object Override

    public String ToString() {
        return "ModbusRtuOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }

    //endregion
}
