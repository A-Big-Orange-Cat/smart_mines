package com.jkzz.smart_mines.communication.HslCommunication.Profinet.Melsec;

import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Address.McAddressData;
import com.jkzz.smart_mines.communication.HslCommunication.Core.IMessage.INetMessage;
import com.jkzz.smart_mines.communication.HslCommunication.Core.IMessage.MelsecQnA3EAsciiMessage;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.RegularByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Profinet.Melsec.Helper.*;


/**
 * 三菱PLC通讯类，采用Qna兼容3E帧协议实现，需要在PLC侧先的以太网模块先进行配置，必须为ASCII通讯格式
 */
public class MelsecMcAsciiNet extends NetworkDeviceBase implements IReadWriteMc {

    private byte NetworkNumber = 0x00;                       // 网络号
    private byte NetworkStationNumber = 0x00;                // 网络站号

    /**
     * 实例化三菱的Qna兼容3E帧协议的通讯对象
     */
    public MelsecMcAsciiNet() {
        WordLength = 1;
        setByteTransform(new RegularByteTransform());
    }

    /**
     * 实例化一个三菱的Qna兼容3E帧协议的通讯对象
     *
     * @param ipAddress PLC的Ip地址
     * @param port      PLC的端口
     */
    public MelsecMcAsciiNet(String ipAddress, int port) {
        WordLength = 1;
        setIpAddress(ipAddress);
        setPort(port);
        setByteTransform(new RegularByteTransform());
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new MelsecQnA3EAsciiMessage();
    }

    /**
     * 获取网络号
     *
     * @return
     */
    public byte getNetworkNumber() {
        return NetworkNumber;
    }

    /**
     * 设置网络号
     *
     * @param networkNumber
     */
    public void setNetworkNumber(byte networkNumber) {
        NetworkNumber = networkNumber;
    }

    /**
     * 获取网络站号
     *
     * @return
     */
    public byte getNetworkStationNumber() {
        return NetworkStationNumber;
    }

    /**
     * 设置网络站号
     *
     * @param networkStationNumber 网络站号信息
     */
    public void setNetworkStationNumber(byte networkStationNumber) {
        NetworkStationNumber = networkStationNumber;
    }

    /**
     * 获取当前的PLC类型
     *
     * @return 类型信息
     */
    public McType getMcType() {
        return McType.MCAscii;
    }

    /**
     * 分析地址的方法，允许派生类里进行重写操作
     *
     * @param address 地址信息
     * @param length  读取的长度信息
     * @return 解析后的数据信息
     */
    public OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length) {
        return McAddressData.ParseMelsecFrom(address, length);
    }

    protected byte[] PackCommandWithHeader(byte[] command) {
        return McAsciiHelper.PackMcCommand(command, this.NetworkNumber, this.NetworkStationNumber);
    }

    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        OperateResult check = McAsciiHelper.CheckResponseContentHelper(response);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 22));
    }

    public byte[] ExtractActualData(byte[] response, boolean isBit) {
        return McAsciiHelper.ExtractActualDataHelper(response, isBit);
    }


    public OperateResultExOne<byte[]> Read(String address, short length) {
        return McHelper.Read(this, address, length);
    }

    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return McHelper.ReadBool(this, address, length);
    }

    public OperateResult Write(String address, byte[] value) {
        return McHelper.Write(this, address, value);
    }

    public OperateResult Write(String address, boolean[] values) {
        return McHelper.Write(this, address, values);
    }

    /**
     * 随机读取PLC的数据信息，可以跨地址，跨类型组合，但是每个地址只能读取一个word，也就是2个字节的内容。收到结果后，需要自行解析数据<br />
     * Randomly read PLC data information, which can be combined across addresses and types, but each address can only read one word,
     * which is the content of 2 bytes. After receiving the results, you need to parse the data yourself
     *
     * @param address 所有的地址的集合
     * @return 结果
     */
    public OperateResultExOne<byte[]> ReadRandom(String[] address) {
        return McHelper.ReadRandom(this, address);
    }

    /**
     * 随机读取PLC的数据信息，可以跨地址，跨类型组合，每个地址是任意的长度。收到结果后，需要自行解析数据，目前只支持字地址，比如D区，W区，R区，不支持X，Y，M，B，L等等<br />
     * Read the data information of the PLC randomly. It can be combined across addresses and types. Each address is of any length. After receiving the results,
     * you need to parse the data yourself. Currently, only word addresses are supported, such as D area, W area, R area. X, Y, M, B, L, etc
     *
     * @param address 所有的地址的集合
     * @param length  每个地址的长度信息
     * @return 结果
     */
    public OperateResultExOne<byte[]> ReadRandom(String[] address, short[] length) {
        return McHelper.ReadRandom(this, address, length);
    }

    /**
     * 随机读取PLC的数据信息，可以跨地址，跨类型组合，但是每个地址只能读取一个word，也就是2个字节的内容。收到结果后，自动转换为了short类型的数组<br />
     * Randomly read PLC data information, which can be combined across addresses and types, but each address can only read one word,
     * which is the content of 2 bytes. After receiving the result, it is automatically converted to an array of type short.
     *
     * @param address 所有的地址的集合
     * @return 结果
     */
    public OperateResultExOne<short[]> ReadRandomInt16(String[] address) {
        return McHelper.ReadRandomInt16(this, address);
    }

    /**
     * 读取PLC的标签信息，需要传入标签的名称，读取的字长度，标签举例：A; label[1]; bbb[10,10,10]<br />
     * To read the label information of the PLC, you need to pass in the name of the label,
     * the length of the word read, and an example of the label: A; label [1]; bbb [10,10,10]
     *
     * @param tag    标签名
     * @param length 读取长度
     * @return 是否成功
     */
    public OperateResultExOne<byte[]> ReadTags(String tag, short length) {
        return ReadTags(new String[]{tag}, new short[]{length});
    }

    /**
     * 读取PLC的标签信息，需要传入标签的名称，读取的字长度，标签举例：A; label[1]; bbb[10,10,10]<br />
     * To read the label information of the PLC, you need to pass in the name of the label,
     * the length of the word read, and an example of the label: A; label [1]; bbb [10,10,10]
     *
     * @param tags   标签名
     * @param length 读取长度
     * @return 是否成功
     */
    public OperateResultExOne<byte[]> ReadTags(String[] tags, short[] length) {
        return McBinaryHelper.ReadTags(this, tags, length);
    }

    /**
     * 读取扩展的数据信息，需要在原有的地址，长度信息之外，输入扩展值信息<br />
     * To read the extended data information, you need to enter the extended value information in addition to the original address and length information
     *
     * @param extend  扩展信息
     * @param address 地址
     * @param length  数据长度
     * @return 返回结果
     */
    public OperateResultExOne<byte[]> ReadExtend(short extend, String address, short length) {
        return McHelper.ReadExtend(this, extend, address, length);
    }

    /**
     * 读取缓冲寄存器的数据信息，地址直接为偏移地址<br />
     * Read the data information of the buffer register, the address is directly the offset address
     *
     * @param address 偏移地址
     * @param length  读取长度
     * @return 读取的内容
     */
    public OperateResultExOne<byte[]> ReadMemory(String address, short length) {
        return McHelper.ReadMemory(this, address, length);
    }

    /**
     * 远程Run操作<br />
     * Remote Run Operation
     *
     * @return 是否成功
     */
    public OperateResult RemoteRun() {
        return McHelper.RemoteRun(this);
    }

    /**
     * 远程Stop操作<br />
     * Remote Stop operation
     *
     * @return 是否成功
     */
    public OperateResult RemoteStop() {
        return McHelper.RemoteStop(this);
    }

    /**
     * 远程Reset操作<br />
     * Remote Reset Operation
     *
     * @return 是否成功
     */
    public OperateResult RemoteReset() {
        return McHelper.RemoteReset(this);
    }


    /**
     * 读取PLC的型号信息，例如 Q02HCPU<br />
     * Read PLC model information, such as Q02HCPU
     *
     * @return 返回型号的结果对象
     */
    public OperateResultExOne<String> ReadPlcType() {
        return McHelper.ReadPlcType(this);
    }

    /**
     * LED 熄灭 出错代码初始化<br />
     * LED off Error code initialization
     *
     * @return 是否成功
     */
    public OperateResult ErrorStateReset() {
        return McHelper.ErrorStateReset(this);
    }

    /**
     * 返回表示当前对象的字符串
     *
     * @return 字符串
     */
    @Override
    public String toString() {
        return "MelsecMcAsciiNet[" + getIpAddress() + ":" + getPort() + "]";
    }


}
