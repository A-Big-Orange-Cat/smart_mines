package com.jkzz.smart_mines.communication.HslCommunication.Robot.FANUC;

import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;
import com.jkzz.smart_mines.communication.HslCommunication.Core.IMessage.FanucRobotMessage;
import com.jkzz.smart_mines.communication.HslCommunication.Core.IMessage.INetMessage;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.IReadWriteNet;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.RegularByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExTwo;
import com.jkzz.smart_mines.communication.HslCommunication.StringResources;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Fanuc机器人的PC Interface实现，在R-30iB mate plus型号上测试通过，支持读写任意的数据，写入操作务必谨慎调用，写入数据不当造成生命财产损失，作者概不负责。读写任意的地址见api文档信息<br />
 * The Fanuc robot's PC Interface implementation has been tested on R-30iB mate plus models. It supports reading and writing arbitrary data. The writing operation must be called carefully.
 * Improper writing of data will cause loss of life and property. The author is not responsible. Read and write arbitrary addresses see api documentation information
 */
public class FanucInterfaceNet extends NetworkDeviceBase implements IReadWriteNet {
    /**
     * 获取或设置当前客户端的ID信息，默认为1024<br />
     * Gets or sets the ID information of the current client. The default is 1024.
     */
    public int ClientId = 1024;
    private byte[] connect_req = new byte[56];
    private byte[] session_req = new byte[]{8, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, (byte) 192, 0, 0, 0, 0, 16, 14, 0, 0, 1, 1, 79, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    /**
     * 实例化一个默认的对象<br />
     */
    public FanucInterfaceNet() {
        WordLength = 1;
        setByteTransform(new RegularByteTransform());
    }

    /**
     * 指定ip及端口来实例化一个默认的对象，端口默认60008<br />
     * Specify the IP and port to instantiate a default object, the port defaults to 60008
     *
     * @param ipAddress ip地址
     * @param port      端口号
     */
    public FanucInterfaceNet(String ipAddress, int port) {
        this();
        setIpAddress(ipAddress);
        setPort(port);
    }

    protected INetMessage GetNewNetMessage() {
        return new FanucRobotMessage();
    }

    private OperateResult ReadCommandFromRobot(Socket socket, String[] cmds) {
        for (int i = 0; i < cmds.length; i++) {
            byte[] buffer = cmds[i].getBytes(StandardCharsets.US_ASCII);
            OperateResultExOne<byte[]> write = ReadFromCoreServer(socket,
                    FanucHelper.BuildWriteData(FanucHelper.SELECTOR_G, 1, buffer, buffer.length), true, true);
            if (!write.IsSuccess) return write;
        }

        return OperateResult.CreateSuccessResult();
    }

    protected OperateResult InitializationOnConnect(Socket socket) {
        System.arraycopy(Utilities.getBytes(ClientId), 0, connect_req, 1, 4);

        OperateResultExOne<byte[]> receive = ReadFromCoreServer(socket, connect_req, true, true);
        if (!receive.IsSuccess) return receive;

        receive = ReadFromCoreServer(socket, session_req, true, true);
        if (!receive.IsSuccess) return receive;

        return ReadCommandFromRobot(socket, FanucHelper.GetFanucCmds());
    }

    public OperateResultExOne<byte[]> Read(String address) {
        return Read(FanucHelper.SELECTOR_D, 1, (short) 6130);
    }

    /**
     * 按照字为单位批量读取设备的原始数据，需要指定地址及长度，地址示例：D1，AI1，AQ1，共计3个区的数据，注意地址的起始为1<br />
     * Read the raw data of the device in batches in units of words. You need to specify the address and length.
     * Example addresses: D1, AI1, AQ1, a total of 3 areas of data. Note that the start of the address is 1.
     *
     * @param address 起始地址，地址示例：D1，AI1，AQ1，共计3个区的数据，注意起始的起始为1
     * @param length  读取的长度，字为单位
     * @return 返回的数据信息结果
     */
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExTwo<Byte, Integer> analysis = FanucHelper.AnalysisFanucAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (analysis.Content1 == FanucHelper.SELECTOR_D || analysis.Content1 == FanucHelper.SELECTOR_AI || analysis.Content1 == FanucHelper.SELECTOR_AQ)
            return Read(analysis.Content1, analysis.Content2, length);
        else
            return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
    }

    /**
     * 写入原始的byte数组数据到指定的地址，返回是否写入成功，地址示例：D1，AI1，AQ1，共计3个区的数据，注意起始的起始为1<br />
     * Write the original byte array data to the specified address, and return whether the write was successful.
     * Example addresses: D1, AI1, AQ1, a total of 3 areas of data. Note that the start of the address is 1.
     *
     * @param address 起始地址，地址示例：D1，AI1，AQ1，共计3个区的数据，注意起始的起始为1
     * @param value   写入值
     * @return 带有成功标识的结果类对象
     */
    public OperateResult Write(String address, byte[] value) {
        OperateResultExTwo<Byte, Integer> analysis = FanucHelper.AnalysisFanucAddress(address);
        if (!analysis.IsSuccess) return analysis;

        if (analysis.Content1 == FanucHelper.SELECTOR_D || analysis.Content1 == FanucHelper.SELECTOR_AI || analysis.Content1 == FanucHelper.SELECTOR_AQ)
            return Write(analysis.Content1, analysis.Content2, value);
        else
            return new OperateResult(StringResources.Language.NotSupportedDataType());
    }

    /**
     * 按照位为单位批量读取设备的原始数据，需要指定地址及长度，地址示例：M1，I1，Q1，共计3个区的数据，注意地址的起始为1<br />
     * Read the raw data of the device in batches in units of boolean. You need to specify the address and length.
     * Example addresses: M1，I1，Q1, a total of 3 areas of data. Note that the start of the address is 1.
     *
     * @param address 起始地址，地址示例：M1，I1，Q1，共计3个区的数据，注意地址的起始为1
     * @param length  读取的长度，位为单位
     * @return 返回的数据信息结果
     */
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExTwo<Byte, Integer> analysis = FanucHelper.AnalysisFanucAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (analysis.Content1 == FanucHelper.SELECTOR_I || analysis.Content1 == FanucHelper.SELECTOR_Q || analysis.Content1 == FanucHelper.SELECTOR_M)
            return ReadBool(analysis.Content1, analysis.Content2, length);
        else
            return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedDataType());
    }

    /**
     * 批量写入{@link Boolean}数组数据，返回是否写入成功，需要指定起始地址，地址示例：M1，I1，Q1，共计3个区的数据，注意地址的起始为1<br />
     * Write {@link Boolean} array data in batches. If the write success is returned, you need to specify the starting address.
     * Example address: M1, I1, Q1, a total of 3 areas of data. Note that the starting address is 1.
     *
     * @param address 起始地址，地址示例：M1，I1，Q1，共计3个区的数据，注意地址的起始为1
     * @param value   等待写入的数据值
     * @return 是否写入成功
     */
    public OperateResult Write(String address, boolean[] value) {
        OperateResultExTwo<Byte, Integer> analysis = FanucHelper.AnalysisFanucAddress(address);
        if (!analysis.IsSuccess) return analysis;

        if (analysis.Content1 == FanucHelper.SELECTOR_I || analysis.Content1 == FanucHelper.SELECTOR_Q || analysis.Content1 == FanucHelper.SELECTOR_M)
            return WriteBool(analysis.Content1, analysis.Content2, value);
        else
            return new OperateResult(StringResources.Language.NotSupportedDataType());
    }

    /**
     * 按照字为单位批量读取设备的原始数据，需要指定数据块地址，偏移地址及长度，主要针对08, 10, 12的数据块，注意地址的起始为1<br />
     * Read the raw data of the device in batches in units of words. You need to specify the data block address,
     * offset address, and length. It is mainly for data blocks of 08, 10, and 12. Note that the start of the address is 1.
     *
     * @param select  数据块信息
     * @param address 偏移地址
     * @param length  读取的长度，字为单位
     * @return 读取的原始字节数据
     */
    public OperateResultExOne<byte[]> Read(byte select, int address, short length) {
        byte[] send = FanucHelper.BulidReadData(select, address, length);

        OperateResultExOne<byte[]> read = ReadFromCoreServer(send);
        if (!read.IsSuccess) return read;

        if (read.Content[31] == (byte) 148)
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(read.Content, 56));
        else if (read.Content[31] == (byte) 212)
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectMiddle(read.Content, 44, length * 2));
        else
            return new OperateResultExOne<>(read.Content[31], "Error");
    }

    /**
     * 写入原始的byte数组数据到指定的地址，返回是否写入成功，，需要指定数据块地址，偏移地址，主要针对08, 10, 12的数据块，注意起始的起始为1<br />
     * Write the original byte array data to the specified address, and return whether the writing is successful. You need to specify the data block address and offset address,
     * which are mainly for the data blocks of 08, 10, and 12. Note that the start of the start is 1.
     *
     * @param select  数据块信息
     * @param address 偏移地址
     * @param value   原始数据内容
     * @return 是否写入成功
     */
    public OperateResult Write(byte select, int address, byte[] value) {
        byte[] send = FanucHelper.BuildWriteData(select, address, value, value.length / 2);
        OperateResultExOne<byte[]> read = ReadFromCoreServer(send);
        if (!read.IsSuccess) return read;

        if (read.Content[31] == (byte) 212)
            return OperateResult.CreateSuccessResult();
        else
            return new OperateResultExOne<byte[]>(read.Content[31], "Error");
    }

    /**
     * 按照位为单位批量读取设备的原始数据，需要指定数据块地址，偏移地址及长度，主要针对70, 72, 76的数据块，注意地址的起始为1<br />
     *
     * @param select  数据块信息
     * @param address 偏移地址
     * @param length  读取的长度，字为单位
     * @return 读取的结果数据内容
     */
    public OperateResultExOne<boolean[]> ReadBool(byte select, int address, short length) {
        int byteStartIndex = address - 1 - (address - 1) % 8 + 1;
        int byteEndIndex = (address + length - 1) % 8 == 0 ? (address + length - 1) : ((address + length - 1) / 8 * 8 + 8);
        int byteLength = (byteEndIndex - byteStartIndex + 1) / 8;

        byte[] send = FanucHelper.BulidReadData(select, address, (short) (byteLength * 8));

        OperateResultExOne<byte[]> read = ReadFromCoreServer(send);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        if (read.Content[31] == (byte) 148) {
            boolean[] array = SoftBasic.ByteToBoolArray(SoftBasic.BytesArrayRemoveBegin(read.Content, 56));
            boolean[] buffer = new boolean[length];
            System.arraycopy(array, address - byteStartIndex, buffer, 0, length);
            return OperateResultExOne.CreateSuccessResult(buffer);

        } else if (read.Content[31] == (byte) 212) {
            boolean[] array = SoftBasic.ByteToBoolArray(SoftBasic.BytesArraySelectMiddle(read.Content, 44, byteLength));
            boolean[] buffer = new boolean[length];
            System.arraycopy(array, address - byteStartIndex, buffer, 0, length);
            return OperateResultExOne.CreateSuccessResult(buffer);
        } else {
            return new OperateResultExOne<boolean[]>(read.Content[31], "Error");
        }
    }

    /**
     * 批量写入{@link Boolean}数组数据，返回是否写入成功，需要指定数据块地址，偏移地址，主要针对70, 72, 76的数据块，注意起始的起始为1
     *
     * @param select  数据块信息
     * @param address 偏移地址
     * @param value   原始的数据内容
     * @return 是否写入成功
     */
    public OperateResult WriteBool(byte select, int address, boolean[] value) {
        int byteStartIndex = address - 1 - (address - 1) % 8 + 1;
        int byteEndIndex = (address + value.length - 1) % 8 == 0 ? (address + value.length - 1) : ((address + value.length - 1) / 8 * 8 + 8);
        int byteLength = (byteEndIndex - byteStartIndex + 1) / 8;

        boolean[] buffer = new boolean[byteLength * 8];
        System.arraycopy(value, 0, buffer, address - byteStartIndex, value.length);
        byte[] send = FanucHelper.BuildWriteData(select, address, getByteTransform().TransByte(buffer), value.length);

        OperateResultExOne<byte[]> read = ReadFromCoreServer(send);
        if (!read.IsSuccess) return read;

        if (read.Content[31] == (byte) 212)
            return OperateResult.CreateSuccessResult();
        else
            return new OperateResult();
    }

    /**
     * 读取机器人的详细信息，返回解析后的数据类型<br />
     * Read the details of the robot and return the resolved data type
     *
     * @return 结果数据信息
     */
    public OperateResultExOne<FanucData> ReadFanucData() {
        OperateResultExOne<byte[]> read = Read("");
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return FanucData.PraseFrom(read.Content);
    }

    /**
     * 读取机器人的SDO信息<br />
     * Read the SDO information of the robot
     *
     * @param address 偏移地址
     * @param length  读取的长度
     * @return 结果数据
     */
    public OperateResultExOne<boolean[]> ReadSDO(int address, short length) {
        if (address < 11001)
            return ReadBool(FanucHelper.SELECTOR_I, address, length);
        else
            return ReadPMCR2((address - 11000), length);
    }

    /**
     * 写入机器人的SDO信息<br />
     * Write the SDO information of the robot
     *
     * @param address 偏移地址
     * @param value   数据值
     * @return 是否写入成功
     */
    public OperateResult WriteSDO(int address, boolean[] value) {
        if (address < 11001)
            return WriteBool(FanucHelper.SELECTOR_I, address, value);
        else
            return WritePMCR2((address - 11000), value);
    }

    /**
     * 读取机器人的SDI信息<br />
     * Read the SDI information of the robot
     *
     * @param address 偏移地址
     * @param length  读取长度
     * @return 结果内容
     */
    public OperateResultExOne<boolean[]> ReadSDI(int address, short length) {
        return ReadBool(FanucHelper.SELECTOR_Q, address, length);
    }

    /**
     * 写入机器人的SDI信息<br />
     * Write the SDI information of the robot
     *
     * @param address 偏移地址
     * @param value   数据值
     * @return 是否写入成功
     */
    public OperateResult WriteSDI(int address, boolean[] value) {
        return WriteBool(FanucHelper.SELECTOR_Q, address, value);
    }

    /**
     * 读取机器人的RDI信息
     *
     * @param address 偏移地址
     * @param length  读取长度
     * @return 结果信息
     */
    public OperateResultExOne<boolean[]> ReadRDI(int address, short length) {
        return ReadBool(FanucHelper.SELECTOR_Q, (address + 5000), length);
    }

    /**
     * 写入机器人的RDI信息
     *
     * @param address 偏移地址
     * @param value   数据值
     * @return 是否写入成功
     */
    public OperateResult WriteRDI(int address, boolean[] value) {
        return WriteBool(FanucHelper.SELECTOR_Q, (address + 5000), value);
    }

    /**
     * 读取机器人的UI信息
     *
     * @param address 偏移地址
     * @param length  读取长度
     * @return 结果信息
     */
    public OperateResultExOne<boolean[]> ReadUI(int address, short length) {
        return ReadBool(FanucHelper.SELECTOR_Q, (address + 6000), length);
    }

    /**
     * 读取机器人的UO信息
     *
     * @param address 偏移地址
     * @param length  读取长度
     * @return 结果信息
     */
    public OperateResultExOne<boolean[]> ReadUO(int address, short length) {
        return ReadBool(FanucHelper.SELECTOR_I, (address + 6000), length);
    }

    /**
     * 写入机器人的UO信息
     *
     * @param address 偏移地址
     * @param value   数据值
     * @return 是否写入成功
     */
    public OperateResult WriteUO(int address, boolean[] value) {
        return WriteBool(FanucHelper.SELECTOR_I, (address + 6000), value);
    }

    /**
     * 读取机器人的SI信息
     *
     * @param address 偏移地址
     * @param length  读取长度
     * @return 结果信息
     */
    public OperateResultExOne<boolean[]> ReadSI(int address, short length) {
        return ReadBool(FanucHelper.SELECTOR_Q, (address + 7000), length);
    }

    /**
     * 读取机器人的SO信息
     *
     * @param address 偏移地址
     * @param length  读取长度
     * @return 结果信息
     */
    public OperateResultExOne<boolean[]> ReadSO(int address, short length) {
        return ReadBool(FanucHelper.SELECTOR_I, (address + 7000), length);
    }

    /**
     * 写入机器人的SO信息
     *
     * @param address 偏移地址
     * @param value   数据值
     * @return 是否写入成功
     */
    public OperateResult WriteSO(int address, boolean[] value) {
        return WriteBool(FanucHelper.SELECTOR_I, (address + 7000), value);
    }

    /**
     * 读取机器人的GI信息
     *
     * @param address 偏移地址
     * @param length  数据长度
     * @return 结果信息
     */
    public OperateResultExOne<short[]> ReadGI(int address, short length) {
        OperateResultExOne<byte[]> read = Read(FanucHelper.SELECTOR_AQ, address, length);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(getByteTransform().TransInt16(read.Content, 0, length));
    }

    /**
     * 写入机器人的GI信息
     *
     * @param address 偏移地址
     * @param value   数据值
     * @return 是否写入成功
     */
    public OperateResult WriteGI(int address, short[] value) {
        return Write(FanucHelper.SELECTOR_AQ, address, getByteTransform().TransByte(value));
    }

    /**
     * 读取机器人的GO信息
     *
     * @param address 偏移地址
     * @param length  读取长度
     * @return 结果信息
     */
    public OperateResultExOne<short[]> ReadGO(int address, short length) {
        if (address >= 10001) address -= 6000;
        OperateResultExOne<byte[]> read = Read(FanucHelper.SELECTOR_AI, address, length);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(getByteTransform().TransInt16(read.Content, 0, length));
    }

    /**
     * 写入机器人的GO信息
     *
     * @param address 偏移地址
     * @param value   数据值
     * @return 写入结果
     */
    public OperateResult WriteGO(int address, short[] value) {
        if (address >= 10001) address -= 6000;
        return Write(FanucHelper.SELECTOR_AI, address, getByteTransform().TransByte(value));
    }

    /**
     * 读取机器人的PMCR2信息
     *
     * @param address 偏移地址
     * @param length  读取长度
     * @return 结果信息
     */
    public OperateResultExOne<boolean[]> ReadPMCR2(int address, short length) {
        return ReadBool(FanucHelper.SELECTOR_M, address, length);
    }

    /**
     * 写入机器人的PMCR2信息
     *
     * @param address 偏移信息
     * @param value   数据值
     * @return 是否写入成功
     */
    public OperateResult WritePMCR2(int address, boolean[] value) {
        return WriteBool(FanucHelper.SELECTOR_M, address, value);
    }

    /**
     * 读取机器人的RDO信息
     *
     * @param address 偏移地址
     * @param length  读取长度
     * @return 结果信息
     */
    public OperateResultExOne<boolean[]> ReadRDO(int address, short length) {
        return ReadBool(FanucHelper.SELECTOR_I, (address + 5000), length);
    }

    /**
     * 写入机器人的RDO信息
     *
     * @param address 偏移地址
     * @param value   数据值
     * @return 是否写入成功
     */
    public OperateResult WriteRDO(int address, boolean[] value) {
        return WriteBool(FanucHelper.SELECTOR_I, (address + 5000), value);
    }

    /**
     * 写入机器人的Rxyzwpr信息，谨慎调用，
     *
     * @param Address   偏移地址
     * @param Xyzwpr    姿态信息
     * @param Config    设置信息
     * @param UserFrame 参考系
     * @param UserTool  工具
     * @return 是否写入成功
     */
    public OperateResult WriteRXyzwpr(int Address, float[] Xyzwpr, short[] Config, short UserFrame, short UserTool) {
        int num = Xyzwpr.length * 4 + Config.length * 2 + 2;
        byte[] robotBuffer = new byte[num];
        byte[] buffer_xyzwpr = getByteTransform().TransByte(Xyzwpr);
        System.arraycopy(buffer_xyzwpr, 0, robotBuffer, 0, buffer_xyzwpr.length);
        byte[] buffer_config = getByteTransform().TransByte(Config);
        System.arraycopy(buffer_config, 0, robotBuffer, 36, buffer_config.length);

        OperateResult write = Write(FanucHelper.SELECTOR_D, Address, robotBuffer);
        if (!write.IsSuccess) return write;

        if (0 <= UserFrame && UserFrame <= 15) {
            if (0 <= UserTool && UserTool <= 15) {
                write = Write(FanucHelper.SELECTOR_D, (Address + 45), getByteTransform().TransByte(new short[]{UserFrame, UserTool}));
                if (!write.IsSuccess) return write;
            } else {
                write = Write(FanucHelper.SELECTOR_D, (Address + 45), getByteTransform().TransByte(new short[]{UserFrame}));
                if (!write.IsSuccess) return write;
            }
        } else if (0 <= UserTool && UserTool <= 15) {
            write = Write(FanucHelper.SELECTOR_D, (Address + 46), getByteTransform().TransByte(new short[]{UserTool}));
            if (!write.IsSuccess) return write;
        }
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 写入机器人的Joint信息
     *
     * @param address   偏移地址
     * @param joint     关节坐标
     * @param UserFrame 参考系
     * @param UserTool  工具
     * @return 是否写入成功
     */
    public OperateResult WriteRJoint(int address, float[] joint, short UserFrame, short UserTool) {
        OperateResult write = Write(FanucHelper.SELECTOR_D, (address + 26), getByteTransform().TransByte(joint));
        if (!write.IsSuccess) return write;
        if (0 <= UserFrame && UserFrame <= 15) {
            if (0 <= UserTool && UserTool <= 15) {
                write = Write(FanucHelper.SELECTOR_D, (address + 44), getByteTransform().TransByte(new short[]{0, UserFrame, UserTool}));
                if (!write.IsSuccess) return write;
            } else {
                write = Write(FanucHelper.SELECTOR_D, (address + 44), getByteTransform().TransByte(new short[]{0, UserFrame}));
                if (!write.IsSuccess) return write;
            }
        } else {
            write = Write(FanucHelper.SELECTOR_D, (address + 44), getByteTransform().TransByte(new short[]{0}));
            if (!write.IsSuccess) return write;

            if (0 <= UserTool && UserTool <= 15) {
                write = Write(FanucHelper.SELECTOR_D, (address + 44), getByteTransform().TransByte(new short[]{0, UserTool}));
                if (!write.IsSuccess) return write;
            }
        }
        return OperateResult.CreateSuccessResult();
    }

    public String toString() {
        return "FanucInterfaceNet[" + getIpAddress() + ":" + getPort() + "]";
    }

}
