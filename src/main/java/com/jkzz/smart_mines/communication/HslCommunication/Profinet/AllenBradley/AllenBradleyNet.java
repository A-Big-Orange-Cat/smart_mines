package com.jkzz.smart_mines.communication.HslCommunication.Profinet.AllenBradley;

import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;
import com.jkzz.smart_mines.communication.HslCommunication.Core.IMessage.AllenBradleyMessage;
import com.jkzz.smart_mines.communication.HslCommunication.Core.IMessage.INetMessage;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.ByteTransformHelper;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.RegularByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.*;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * AB PLC的数据通信类，使用CIP协议实现，适用1756，1769等型号，支持使用标签的形式进行读写操作，支持标量数据，一维数组，二维数组，三维数组等等。如果是局部变量，那么使用 Program:MainProgram.[变量名]。<br />
 * The data communication class of AB PLC is implemented using the CIP protocol. It is suitable for 1756, 1769 and other models.
 * It supports reading and writing in the form of tags, scalar data, one-dimensional array, two-dimensional array,
 * three-dimensional array, and so on. If it is a local variable, use the Program:MainProgram.[Variable name].
 */
public class AllenBradleyNet extends NetworkDeviceBase {

    //region Constructor

    public int ArraySegment = 100;
    public byte[] PortSlot = null;
    private int SessionHandle = 0;
    private byte Slot = 0;

    /**
     * Instantiate a communication object for a Allenbradley PLC protocol
     */
    public AllenBradleyNet() {
        WordLength = 2;
        setByteTransform(new RegularByteTransform());
    }

    //endregion

    //region Public Properties

    /**
     * Instantiate a communication object for a Allenbradley PLC protocol
     *
     * @param ipAddress Ip address
     */
    public AllenBradleyNet(String ipAddress) {
        this(ipAddress, 44818);
    }

    /**
     * Instantiate a communication object for a Allenbradley PLC protocol
     *
     * @param ipAddress ip address
     * @param port      port
     */
    public AllenBradleyNet(String ipAddress, int port) {
        this();
        setIpAddress(ipAddress);
        setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new AllenBradleyMessage();
    }

    protected byte[] PackCommandWithHeader(byte[] command) {
        return AllenBradleyHelper.PackRequestHeader(0x6F, SessionHandle, command);
    }

    /**
     * The current session handle, which is determined by the PLC when communicating with the PLC handshake
     *
     * @return int value
     */
    public int getSessionHandle() {
        return SessionHandle;
    }

    /**
     * Gets the slot number information for the current plc, which should be set before connections
     *
     * @return value
     */
    public byte getSlot() {
        return Slot;
    }

    /**
     * Sets the slot number information for the current plc, which should be set before connections
     *
     * @param value value
     */
    public void setSlot(byte value) {
        Slot = value;
    }

    //endregion

    //region Double Mode Override

    /**
     * when read array type, this means the segment length. when data type is 8-byte data, it should set to be 50
     *
     * @return value
     */
    public int getArraySegment() {
        return ArraySegment;
    }

    /**
     * when read array type, this means the segment length. when data type is 8-byte data, it should set to be 50
     *
     * @param value value
     */
    public void setArraySegment(int value) {
        ArraySegment = value;
    }

    //endregion

    //region Build Command

    /**
     * port and slot information
     *
     * @return
     */
    public byte[] getPortSlot() {
        return PortSlot;
    }

    /**
     * port and slot information
     *
     * @param value
     */
    public void setPortSlot(byte[] value) {
        PortSlot = value;
    }

    protected OperateResult InitializationOnConnect(Socket socket) {
        // Registering Session Information
        OperateResultExOne<byte[]> read = ReadFromCoreServer(socket, AllenBradleyHelper.RegisterSessionHandle(), true, false);
        if (!read.IsSuccess) return read;

        // Check the returned status
        OperateResult check = AllenBradleyHelper.CheckResponse(read.Content);
        if (!check.IsSuccess) return check;

        // Extract session ID
        SessionHandle = getByteTransform().TransInt32(read.Content, 4);

        return OperateResult.CreateSuccessResult();
    }

    /**
     * A next step handshake agreement is required before disconnecting the Allenbradley plc
     *
     * @param socket socket before connection close
     * @return Whether the disconnect operation was successful
     */
    protected OperateResult ExtraOnDisconnect(Socket socket) {
        // Unregister session Information
        OperateResultExOne<byte[]> read = ReadFromCoreServer(socket, AllenBradleyHelper.UnRegisterSessionHandle(SessionHandle), true, false);
        if (!read.IsSuccess) return read;

        return OperateResult.CreateSuccessResult();
    }

    /**
     * Build a read command bytes
     *
     * @param address the address of the tag name
     * @param length  Array information, if not arrays, is 1
     * @return Message information that contains the result object
     */
    public OperateResultExOne<byte[]> BuildReadCommand(String[] address, int[] length) {
        if (address == null || length == null) return new OperateResultExOne<byte[]>("address or length is null");
        if (address.length != length.length)
            return new OperateResultExOne<byte[]>("address and length is not same array");

        try {
            ArrayList<byte[]> cips = new ArrayList<>();
            for (int i = 0; i < address.length; i++) {
                cips.add(AllenBradleyHelper.PackRequsetRead(address[i], length[i]));
            }

            byte[] commandSpecificData = AllenBradleyHelper.PackCommandSpecificData(new byte[4],
                    AllenBradleyHelper.PackCommandService(PortSlot == null ? new byte[]{0x01, Slot} : PortSlot, Utilities.ToArray(cips)));

            return OperateResultExOne.CreateSuccessResult(commandSpecificData);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address Wrong:" + ex.getMessage());
        }
    }

//endregion

//region Override Read

    /**
     * Build a read command bytes
     *
     * @param address The address of the tag name
     * @return Message information that contains the result object
     */
    public OperateResultExOne<byte[]> BuildReadCommand(String[] address) {
        if (address == null) return new OperateResultExOne<byte[]>("address or length is null");

        int[] length = new int[address.length];
        for (int i = 0; i < address.length; i++) {
            length[i] = 1;
        }

        return BuildReadCommand(address, length);
    }

    /**
     * Create a written message instruction
     *
     * @param address  The address of the tag name
     * @param typeCode Data type
     * @param data     Source Data
     * @return Message information that contains the result object
     */
    public OperateResultExOne<byte[]> BuildWriteCommand(String address, short typeCode, byte[] data) {
        return BuildWriteCommand(address, typeCode, data, 1);
    }

    /**
     * Create a written message instruction
     *
     * @param address  The address of the tag name
     * @param typeCode Data type
     * @param data     Source Data
     * @param length   In the case of arrays, the length of the array
     * @return Message information that contains the result object
     */
    public OperateResultExOne<byte[]> BuildWriteCommand(String address, short typeCode, byte[] data, int length) {
        try {
            byte[] cip = AllenBradleyHelper.PackRequestWrite(address, typeCode, data, length);
            byte[] commandSpecificData = AllenBradleyHelper.PackCommandSpecificData(new byte[4], PackCommandService(PortSlot != null ? PortSlot : new byte[]{0x01, Slot}, cip));

            return OperateResultExOne.CreateSuccessResult(commandSpecificData);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address Wrong:" + ex.getMessage());
        }
    }

    /**
     * Create a written message instruction
     *
     * @param address The address of the tag name
     * @param data    Bool Data
     * @return Message information that contains the result object
     */
    public OperateResultExOne<byte[]> BuildWriteCommand(String address, Boolean data) {
        try {
            byte[] cip = AllenBradleyHelper.PackRequestWrite(address, data);
            byte[] commandSpecificData = AllenBradleyHelper.PackCommandSpecificData(new byte[4],
                    PackCommandService(PortSlot != null ? PortSlot : new byte[]{0x01, Slot}, cip));

            return OperateResultExOne.CreateSuccessResult(commandSpecificData);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address Wrong:" + ex.getMessage());
        }
    }

    /**
     * Read data information, data length for read array length information
     *
     * @param address Address format of the node
     * @param length  In the case of arrays, the length of the array
     * @return Result data with result object
     */
    public OperateResultExOne<byte[]> Read(String address, short length) {
        if (length > 1) {
            return ReadSegment(address, 0, length);
        } else {
            return Read(new String[]{address}, new int[]{length});
        }
    }

    /**
     * Bulk read Data information
     *
     * @param address Name of the node
     * @return Result data with result object
     */
    public OperateResultExOne<byte[]> Read(String[] address) {
        if (address == null) return new OperateResultExOne<byte[]>("address can not be null");

        int[] length = new int[address.length];
        for (int i = 0; i < length.length; i++) {
            length[i] = 1;
        }

        return Read(address, length);
    }

    /**
     * 批量读取数据信息，数据长度为读取的数组长度信息 -> Bulk read data information, data length for read array length information
     *
     * @param address 节点的名称 -> Name of the node
     * @param length  如果是数组，就为数组长度 -> In the case of arrays, the length of the array
     * @return 带有结果对象的结果数据 -> Result data with result object
     */
    public OperateResultExOne<byte[]> Read(String[] address, int[] length) {
        // 指令生成 -> Instruction Generation
        OperateResultExOne<byte[]> command = BuildReadCommand(address, length);
        if (!command.IsSuccess) return command;

        // 核心交互 -> Core Interactions
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 检查反馈 -> Check Feedback
        OperateResult check = AllenBradleyHelper.CheckResponse(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 提取数据 -> Extracting data
        OperateResultExThree<byte[], Short, Boolean> analysis = AllenBradleyHelper.ExtractActualData(read.Content, true);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        return OperateResultExOne.CreateSuccessResult(analysis.Content1);
    }

    /**
     * Read Segment Data Array form plc, use address tag name
     *
     * @param address    Tag name in plc
     * @param startIndex array start index
     * @param length     array length
     * @return Results Bytes
     */
    public OperateResultExOne<byte[]> ReadSegment(String address, int startIndex, int length) {
        try {
            ArrayList<Byte> bytesContent = new ArrayList<Byte>();
            while (true) {
                OperateResultExOne<byte[]> read = ReadCipFromServer((AllenBradleyHelper.PackRequestReadSegment(address, startIndex, length)));
                if (!read.IsSuccess) return read;

                // 提取数据 -> Extracting data
                OperateResultExThree<byte[], Short, Boolean> analysis = AllenBradleyHelper.ExtractActualData(read.Content, true);
                if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

                startIndex += analysis.Content1.length;
                for (int i = 0; i < analysis.Content1.length; i++) {
                    bytesContent.add(analysis.Content1[i]);
                }

                if (!analysis.Content3) break;
            }

            byte[] buffer = new byte[bytesContent.size()];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = bytesContent.get(i);
            }
            return OperateResultExOne.CreateSuccessResult(buffer);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address Wrong:" + ex.getMessage());
        }
    }

    private OperateResultExOne<byte[]> ReadByCips(byte[]... cips) {
        OperateResultExOne<byte[]> read = ReadCipFromServer(cips);
        if (!read.IsSuccess) return read;

        // 提取数据 -> Extracting data
        OperateResultExThree<byte[], Short, Boolean> analysis = AllenBradleyHelper.ExtractActualData(read.Content, true);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        // 提取数据 -> Extracting data
        return OperateResultExOne.CreateSuccessResult(analysis.Content1);
    }

    /**
     * 使用CIP报文和服务器进行核心的数据交换
     *
     * @param cips Cip commands
     * @return Results Bytes
     */
    public OperateResultExOne<byte[]> ReadCipFromServer(byte[]... cips) {
        try {

            byte[] commandSpecificData = AllenBradleyHelper.PackCommandSpecificData(new byte[4], AllenBradleyHelper.PackCommandService(
                    PortSlot == null ? new byte[]{0x01, Slot} : PortSlot, cips));

            // 核心交互 -> Core Interactions
            OperateResultExOne<byte[]> read = ReadFromCoreServer(commandSpecificData);
            if (!read.IsSuccess) return read;

            // 检查反馈 -> Check Feedback
            OperateResult check = AllenBradleyHelper.CheckResponse(read.Content);
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

            return OperateResultExOne.CreateSuccessResult(read.Content);
        } catch (Exception ex) {
            return new OperateResultExOne<>(ex.getMessage());
        }
    }

//endregion

//region Device Override

    /**
     * 读取单个的bool数据信息 -> Read a single BOOL data information
     *
     * @param address 节点的名称 -> Name of the node
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<Boolean> ReadBool(String address) {
        if (address.startsWith("i=")) {
            address = address.substring(2);
            OperateResultExTwo<String, Integer> analysis = AllenBradleyHelper.AnalysisArrayIndex(address);
            address = analysis.Content1;
            int bitIndex = analysis.Content2;

            OperateResultExOne<boolean[]> read = ReadBoolArray(address + "[" + bitIndex / 32 + "]");
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            return OperateResultExOne.CreateSuccessResult(read.Content[bitIndex % 32]);
        } else {
            OperateResultExOne<byte[]> read = Read(address, (short) 1);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            return OperateResultExOne.CreateSuccessResult(getByteTransform().TransBool(read.Content, 0));
        }
    }

    /**
     * 批量读取的bool数组信息 -> Bulk read of bool array information
     *
     * @param address 节点的名称 -> Name of the node
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<boolean[]> ReadBoolArray(String address) {
        OperateResultExOne<byte[]> read = Read(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(getByteTransform().TransBool(read.Content, 0, read.Content.length));
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExOne<byte[]> read = Read(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray(read.Content, length));
    }

    /**
     * 读取PLC的byte类型的数据 -> Read the byte type of PLC data
     *
     * @param address 节点的名称 -> Name of the node
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<Byte> ReadByte(String address) {
        OperateResultExOne<byte[]> read = Read(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(getByteTransform().TransByte(read.Content, 0));
    }

    /**
     * 读取PLC的short类型的数组 -> Read an array of the short type of the PLC
     *
     * @param address 节点的名称 -> Name of the node
     * @param length  数组长度 -> Array length
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<short[]> ReadInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, length), new FunctionOperateExOne<byte[], short[]>() {
            @Override
            public short[] Action(byte[] content) {
                return getByteTransform().TransInt16(content, 0, length);
            }
        });
    }

    /**
     * 读取PLC的ushort类型的数组 -> Read an array of the ushort type of the PLC
     *
     * @param address 节点的名称 -> Name of the node
     * @param length  数组长度 -> Array length
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<int[]> ReadUInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, length), new FunctionOperateExOne<byte[], int[]>() {
            @Override
            public int[] Action(byte[] content) {
                return getByteTransform().TransUInt16(content, 0, length);
            }
        });
    }

    /**
     * 读取PLC的int类型的数组 -> An array that reads the int type of the PLC
     *
     * @param address 节点的名称 -> Name of the node
     * @param length  数组长度 -> Array length
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<int[]> ReadInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, length), new FunctionOperateExOne<byte[], int[]>() {
            @Override
            public int[] Action(byte[] content) {
                return getByteTransform().TransInt32(content, 0, length);
            }
        });
    }

    /**
     * 读取PLC的int类型的数组 -> An array that reads the int type of the PLC
     *
     * @param address 节点的名称 -> Name of the node
     * @param length  数组长度 -> Array length
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<long[]> ReadUInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, length), new FunctionOperateExOne<byte[], long[]>() {
            @Override
            public long[] Action(byte[] content) {
                return getByteTransform().TransUInt32(content, 0, length);
            }
        });
    }

    /**
     * 读取PLC的float类型的数组 -> An array that reads the float type of the PLC
     *
     * @param address 节点的名称 -> Name of the node
     * @param length  数组长度 -> Array length
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<float[]> ReadFloat(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, length), new FunctionOperateExOne<byte[], float[]>() {
            @Override
            public float[] Action(byte[] content) {
                return getByteTransform().TransSingle(content, 0, length);
            }
        });
    }

    /**
     * 读取PLC的long类型的数组 -> An array that reads the long type of the PLC
     *
     * @param address 节点的名称 -> Name of the node
     * @param length  数组长度 -> Array length
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<long[]> ReadInt64(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, length), new FunctionOperateExOne<byte[], long[]>() {
            @Override
            public long[] Action(byte[] content) {
                return getByteTransform().TransInt64(content, 0, length);
            }
        });
    }

//endregion

//region Write Support

    /**
     * 读取PLC的double类型的数组 -> An array that reads the double type of the PLC
     *
     * @param address 起始地址 节点的名称 -> Name of the node
     * @param length  数组长度 -> Array length
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<double[]> ReadDouble(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, length), new FunctionOperateExOne<byte[], double[]>() {
            @Override
            public double[] Action(byte[] content) {
                return getByteTransform().TransDouble(content, 0, length);
            }
        });
    }

    /**
     * 读取PLC的string类型的数据 -> read plc string type value
     *
     * @param address Name of the node
     * @return Result data with result info
     */
    public OperateResultExOne<String> ReadString(String address) {
        return ReadString(address, (short) 1, "ascii");
    }

    /**
     * 读取PLC的string类型的数据 -> read plc string type value
     *
     * @param address  Name of the node
     * @param length   Array length
     * @param encoding Result data with result info
     * @return Result data with result info
     */
    public OperateResultExOne<String> ReadString(String address, short length, String encoding) {
        try {
            OperateResultExOne<byte[]> read = Read(address, length);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            if (read.Content.length >= 6) {
                int strLength = Utilities.getInt(read.Content, 2);
                return OperateResultExOne.CreateSuccessResult(new String(read.Content, 6, strLength, encoding));
            } else {
                return OperateResultExOne.CreateSuccessResult(new String(read.Content, encoding));
            }
        } catch (Exception ex) {
            return new OperateResultExOne<>(ex.getMessage());
        }
    }

    /**
     * 读取PLC的型号信息
     *
     * @return 型号信息
     */
    public OperateResultExOne<String> ReadPlcType() {
        return AllenBradleyHelper.ReadPlcType(this);
    }

//endregion

//region Write Override

    /**
     * 从PLC里读取一个指定标签名的原始数据信息及其数据类型信息<br />
     * Read the original data information of a specified tag name and its data type information from the PLC
     *
     * @param address PLC的标签地址信息
     * @param length  读取的数据长度
     * @return 包含原始数据信息及数据类型的结果对象
     */
    public OperateResultExTwo<Short, byte[]> ReadTag(String address, int length) {
        OperateResultExThree<byte[], Short, Boolean> read = ReadWithType(new String[]{address}, new int[]{length});
        if (!read.IsSuccess) return OperateResultExTwo.CreateFailedResult(read);

        return OperateResultExTwo.CreateSuccessResult(read.Content2, read.Content1);
    }

    private OperateResultExThree<byte[], Short, Boolean> ReadWithType(String[] address, int[] length) {
        // 指令生成 -> Instruction Generation
        OperateResultExOne<byte[]> command = BuildReadCommand(address, length);
        if (!command.IsSuccess) return OperateResultExThree.CreateFailedResult(command);
        ;

        // 核心交互 -> Core Interactions
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExThree.CreateFailedResult(read);
        ;

        // 检查反馈 -> Check Feedback
        OperateResult check = AllenBradleyHelper.CheckResponse(read.Content);
        if (!check.IsSuccess) return OperateResultExThree.CreateFailedResult(check);

        // 提取数据 -> Extracting data
        return AllenBradleyHelper.ExtractActualData(read.Content, true);
    }

    /**
     * 使用指定的类型写入指定的节点数据 -> Writes the specified node data with the specified type
     *
     * @param address  节点的名称 -> Name of the node
     * @param typeCode 类型代码，详细参见 AllenBradleyHelper 上的常用字段 ->  Type code, see the commonly used Fields section on the AllenBradleyHelper in detail
     * @param value    实际的数据值 -> The actual data value
     * @return 是否写入成功 -> Whether to write successfully
     */
    public OperateResult WriteTag(String address, short typeCode, byte[] value) {
        return WriteTag(address, typeCode, value, 1);
    }

    /**
     * 使用指定的类型写入指定的节点数据 -> Writes the specified node data with the specified type
     *
     * @param address  节点的名称 -> Name of the node
     * @param typeCode 类型代码，详细参见 AllenBradleyHelper 上的常用字段 ->  Type code, see the commonly used Fields section on the AllenBradleyHelper in detail
     * @param value    实际的数据值 -> The actual data value
     * @param length   如果节点是数组，就是数组长度 -> If the node is an array, it is the array length
     * @return 是否写入成功 -> Whether to write successfully
     */
    public OperateResult WriteTag(String address, short typeCode, byte[] value, int length) {
        OperateResultExOne<byte[]> command = BuildWriteCommand(address, typeCode, value, length);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        OperateResult check = AllenBradleyHelper.CheckResponse(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        return AllenBradleyHelper.ExtractActualData(read.Content, false);
    }

    /**
     * 向PLC中写入short数组，返回是否写入成功 -> Writes a short array to the PLC to return whether the write was successful
     *
     * @param address 节点的名称 -> Name of the node
     * @param values  实际数据 -> Actual data
     * @return 是否写入成功 -> Whether to write successfully
     */
    public OperateResult Write(String address, short[] values) {
        return WriteTag(address, (short) AllenBradleyHelper.CIP_Type_Word, getByteTransform().TransByte(values), values.length);
    }

    /**
     * 向PLC中写入int数组，返回是否写入成功 -> Writes an int array to the PLC to return whether the write was successful
     *
     * @param address 节点的名称 -> Name of the node
     * @param values  实际数据 -> Actual data
     * @return 是否写入成功 -> Whether to write successfully
     */
    public OperateResult Write(String address, int[] values) {
        return WriteTag(address, (short) AllenBradleyHelper.CIP_Type_DWord, getByteTransform().TransByte(values), values.length);
    }

    /**
     * Writes an array of float to the PLC to return whether the write was successful
     *
     * @param address Name of the node
     * @param values  Actual data
     * @return Whether to write successfully
     */
    public OperateResult Write(String address, float[] values) {
        return WriteTag(address, (short) AllenBradleyHelper.CIP_Type_Real, getByteTransform().TransByte(values), values.length);
    }

    /**
     * Writes an array of long to the PLC to return whether the write was successful
     *
     * @param address Name of the node
     * @param values  Actual data
     * @return Whether to write successfully
     */
    public OperateResult Write(String address, long[] values) {
        return WriteTag(address, (short) AllenBradleyHelper.CIP_Type_LInt, getByteTransform().TransByte(values), values.length);
    }

    //endregion

    // region PackCommandService

    /**
     * Writes an array of double to the PLC to return whether the write was successful
     *
     * @param address Name of the node
     * @param values  Actual data
     * @return Whether to write successfully
     */
    public OperateResult Write(String address, double[] values) {
        return WriteTag(address, (short) AllenBradleyHelper.CIP_Type_Double, getByteTransform().TransByte(values), values.length);
    }

    // endregion

    //region Private Member

    /**
     * 向PLC中写入string数据，返回是否写入成功，针对的是ASCII编码的数据内容
     *
     * @param address 节点的名称 -> Name of the node
     * @param value   实际数据 -> Actual data
     * @return 是否写入成功 -> Whether to write successfully
     */
    public OperateResult Write(String address, String value) {
        if (Utilities.IsStringNullOrEmpty(value)) value = "";

        try {

            byte[] data = value.getBytes(StandardCharsets.US_ASCII);
            OperateResult write = Write(address + ".LEN", data.length);
            if (!write.IsSuccess) return write;

            byte[] buffer = SoftBasic.ArrayExpandToLengthEven(data);
            return WriteTag(address + ".DATA[0]", (short) AllenBradleyHelper.CIP_Type_Byte, buffer, data.length);
        } catch (Exception ex) {
            return new OperateResult(ex.getMessage());
        }
    }

    /**
     * 向PLC中写入bool数据，返回是否写入成功
     *
     * @param address 节点的名称 -> Name of the node
     * @param value   实际数据 -> Actual data
     * @return 是否写入成功 -> Whether to write successfully
     */
    public OperateResult Write(String address, boolean value) {
        return WriteTag(address, (short) AllenBradleyHelper.CIP_Type_Bool, value ? new byte[]{(byte) 0xFF, (byte) 0xFF} : new byte[]{0x00, 0x00});
    }

    /**
     * 向PLC中写入byte数据，返回是否写入成功
     *
     * @param address 节点的名称 -> Name of the node
     * @param value   实际数据 -> Actual data
     * @return 是否写入成功 -> Whether to write successfully
     */
    public OperateResult Write(String address, byte value) {
        return WriteTag(address, (short) AllenBradleyHelper.CIP_Type_Byte, new byte[]{value, 0x00});
    }

    /// <inheritdoc cref="AllenBradleyHelper.PackCommandService(byte[], byte[][])"/>
    protected byte[] PackCommandService(byte[] portSlot, byte[]... cips) throws IOException {
        return AllenBradleyHelper.PackCommandService(portSlot, cips);
    }

    //endregion

    //region Object Override

    /**
     * 返回表示当前对象的字符串
     *
     * @return 字符串信息
     */
    public String toString() {
        return "AllenBradleyNet[" + getIpAddress() + ":" + getPort() + "]";
    }

    // endregion
}
