package com.jkzz.smart_mines.communication.HslCommunication.Profinet.Omron;

import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Address.OmronFinsAddress;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.IReadWriteDevice;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.StringResources;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

import java.util.ArrayList;

/**
 * Profinet.Omron PLC的FINS协议相关的辅助类，主要是一些地址解析，读写的指令生成。<br />
 * The auxiliary classes related to the FINS protocol of Profinet.Omron PLC are mainly some address resolution and the generation of read and write instructions.
 */
public class OmronFinsNetHelper {

    // region Static Method Helper

    /**
     * 根据读取的地址，长度，是否位读取创建Fins协议的核心报文<br />
     * According to the read address, length, whether to read the core message that creates the Fins protocol
     *
     * @param address 地址，具体格式请参照示例说明
     * @param length  读取的数据长度
     * @param isBit   是否使用位读取
     * @return 带有成功标识的Fins核心报文
     */
    public static OperateResultExOne<byte[][]> BuildReadCommand(String address, short length, boolean isBit, int splitLength) {
        OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(address, length);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        ArrayList<byte[]> cmds = new ArrayList<byte[]>();
        int[] lengths = SoftBasic.SplitIntegerToArray(length, isBit ? Integer.MAX_VALUE : splitLength);
        for (int i = 0; i < lengths.length; i++) {
            byte[] _PLCCommand = new byte[8];
            _PLCCommand[0] = 0x01;    // 读取存储区数据
            _PLCCommand[1] = 0x01;
            if (isBit)
                _PLCCommand[2] = analysis.Content.getBitCode();
            else
                _PLCCommand[2] = analysis.Content.getWordCode();
            _PLCCommand[3] = (byte) (analysis.Content.getAddressStart() / 16 / 256);
            _PLCCommand[4] = (byte) (analysis.Content.getAddressStart() / 16 % 256);
            _PLCCommand[5] = (byte) (analysis.Content.getAddressStart() % 16);
            _PLCCommand[6] = (byte) (lengths[i] / 256);                           // 长度
            _PLCCommand[7] = (byte) (lengths[i] % 256);

            cmds.add(_PLCCommand);
            // 起始地址偏移
            analysis.Content.setAddressStart(analysis.Content.getAddressStart() + (isBit ? lengths[i] : lengths[i] * 16));
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.ToArray(cmds));
    }

    /**
     * 根据写入的地址，数据，是否位写入生成Fins协议的核心报文<br />
     * According to the written address, data, whether the bit is written to generate the core message of the Fins protocol
     *
     * @param address 地址内容，具体格式请参照示例说明
     * @param value   实际的数据
     * @param isBit   是否位数据
     * @return 带有成功标识的Fins核心报文
     */
    public static OperateResultExOne<byte[]> BuildWriteWordCommand(String address, byte[] value, boolean isBit) {
        OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(address, (short) 0);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] _PLCCommand = new byte[8 + value.length];
        _PLCCommand[0] = 0x01;
        _PLCCommand[1] = 0x02;

        if (isBit)
            _PLCCommand[2] = analysis.Content.getBitCode();
        else
            _PLCCommand[2] = analysis.Content.getWordCode();

        _PLCCommand[3] = (byte) (analysis.Content.getAddressStart() / 16 / 256);
        _PLCCommand[4] = (byte) (analysis.Content.getAddressStart() / 16 % 256);
        _PLCCommand[5] = (byte) (analysis.Content.getAddressStart() % 16);
        if (isBit) {
            _PLCCommand[6] = (byte) (value.length / 256);
            _PLCCommand[7] = (byte) (value.length % 256);
        } else {
            _PLCCommand[6] = (byte) (value.length / 2 / 256);
            _PLCCommand[7] = (byte) (value.length / 2 % 256);
        }

        System.arraycopy(value, 0, _PLCCommand, 8, value.length);

        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 验证欧姆龙的Fins-TCP返回的数据是否正确的数据，如果正确的话，并返回所有的数据内容<br />
     * Verify that the data returned by Profinet.Omron's Fins-TCP is correct data, if correct, and return all data content
     *
     * @param response 来自欧姆龙返回的数据内容
     * @return 带有是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> ResponseValidAnalysis(byte[] response) {
        if (response.length >= 16) {
            // 提取错误码 -> Extracting error Codes
            byte[] buffer = new byte[4];
            buffer[0] = response[15];
            buffer[1] = response[14];
            buffer[2] = response[13];
            buffer[3] = response[12];

            int err = Utilities.getInt(buffer, 0);
            if (err > 0) return new OperateResultExOne<byte[]>(err, GetStatusDescription(err));

            byte[] result = new byte[response.length - 16];
            System.arraycopy(response, 16, result, 0, result.length);
            return UdpResponseValidAnalysis(result);
        }

        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError());
    }

    /**
     * 验证欧姆龙的Fins-Udp返回的数据是否正确的数据，如果正确的话，并返回所有的数据内容<br />
     * Verify that the data returned by Profinet.Omron's Fins-Udp is correct data, if correct, and return all data content
     *
     * @param response 来自欧姆龙返回的数据内容
     * @return 带有是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> UdpResponseValidAnalysis(byte[] response) {
        if (response.length >= 14) {
            int err = (response[12] & 0xff) * 256 + (response[13] & 0xff);
            // if (err > 0) return new OperateResult<byte[]>( err, StringResources.Language.OmronReceiveDataError );

            if ((response[10] == 0x01 & response[11] == 0x01) ||
                    (response[10] == 0x01 & response[11] == 0x04) ||
                    (response[10] == 0x02 & response[11] == 0x01) ||
                    (response[10] == 0x03 & response[11] == 0x06) ||
                    (response[10] == 0x05 & response[11] == 0x01) ||
                    (response[10] == 0x05 & response[11] == 0x02) ||
                    (response[10] == 0x06 & response[11] == 0x01) ||
                    (response[10] == 0x06 & response[11] == 0x20) ||
                    (response[10] == 0x07 & response[11] == 0x01) ||
                    (response[10] == 0x09 & response[11] == 0x20) ||
                    (response[10] == 0x21 & response[11] == 0x02) ||
                    (response[10] == 0x22 & response[11] == 0x02)) {
                // Read Data
                byte[] content = new byte[response.length - 14];
                if (content.length > 0) System.arraycopy(response, 14, content, 0, content.length);

                OperateResultExOne<byte[]> success = OperateResultExOne.CreateSuccessResult(content);
                if (content.length == 0) success.IsSuccess = false;
                success.ErrorCode = err;
                success.Message = GetStatusDescription(err) + " Received:" + SoftBasic.ByteToHexString(response, ' ');
                return success;
            } else {
                // Write Data
                OperateResultExOne<byte[]> success = OperateResultExOne.CreateSuccessResult(new byte[0]);
                success.ErrorCode = err;
                success.Message = GetStatusDescription(err) + " Received:" + SoftBasic.ByteToHexString(response, ' ');
                return success;
            }
        }

        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError());
    }

    /**
     * 根据欧姆龙返回的错误码，获取错误信息的字符串描述文本<br />
     * According to the error code returned by Profinet.Omron, get the string description text of the error message
     *
     * @param err 错误码
     * @return 文本描述
     */
    public static String GetStatusDescription(int err) {
        switch (err) {
            case 0:
                return StringResources.Language.OmronStatus0();
            case 1:
                return StringResources.Language.OmronStatus1();
            case 2:
                return StringResources.Language.OmronStatus2();
            case 3:
                return StringResources.Language.OmronStatus3();
            case 20:
                return StringResources.Language.OmronStatus20();
            case 21:
                return StringResources.Language.OmronStatus21();
            case 22:
                return StringResources.Language.OmronStatus22();
            case 23:
                return StringResources.Language.OmronStatus23();
            case 24:
                return StringResources.Language.OmronStatus24();
            case 25:
                return StringResources.Language.OmronStatus25();
            default:
                return StringResources.Language.UnknownError();
        }
    }

    // endregion

    // region ReadWriteHelper

    /**
     * 从欧姆龙PLC中读取想要的数据，返回读取结果，读取长度的单位为字，地址格式为"D100","C100","W100","H100","A100"<br />
     * Read the desired data from the Profinet.Omron PLC and return the read result. The unit of the read length is word. The address format is "D100", "C100", "W100", "H100", "A100"
     *
     * @param omron   PLC设备的连接对象
     * @param address 读取地址，格式为"D100","C100","W100","H100","A100"
     * @param length  读取的数据长度
     * @param splits  分割信息
     * @return 带成功标志的结果数据对象
     */
    public static OperateResultExOne<byte[]> Read(IReadWriteDevice omron, String address, short length, int splits) {
        // 获取指令
        OperateResultExOne<byte[][]> command = OmronFinsNetHelper.BuildReadCommand(address, length, false, splits);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> contentArray = new ArrayList<>();
        for (int i = 0; i < command.Content.length; i++) {
            // 核心数据交互
            OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(command.Content[i]);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 读取到了正确的数据
            Utilities.ArrayListAddArray(contentArray, read.Content);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(contentArray));
    }

    /**
     * 向PLC写入数据，数据格式为原始的字节类型，地址格式为"D100","C100","W100","H100","A100"<br />
     * Write data to PLC, the data format is the original byte type, and the address format is "D100", "C100", "W100", "H100", "A100"
     *
     * @param omron   PLC设备的连接对象
     * @param address 初始地址
     * @param value   原始的字节数据
     * @return 是否成功的结果对象
     */
    public static OperateResult Write(IReadWriteDevice omron, String address, byte[] value) {
        // 获取指令
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildWriteWordCommand(address, value, false);
        if (!command.IsSuccess) return command;

        // 核心数据交互
        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 从欧姆龙PLC中批量读取位软元件，地址格式为"D100.0","C100.0","W100.0","H100.0","A100.0"<br />
     * Read bit devices in batches from Profinet.Omron PLC with address format "D100.0", "C100.0", "W100.0", "H100.0", "A100.0"
     *
     * @param omron   PLC设备的连接对象
     * @param address 读取地址，格式为"D100","C100","W100","H100","A100"
     * @param length  读取的长度
     * @param splits  分割信息
     * @return 带成功标志的结果数据对象
     */
    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteDevice omron, String address, short length, int splits) {
        // 获取指令
        OperateResultExOne<byte[][]> command = OmronFinsNetHelper.BuildReadCommand(address, length, true, splits);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Boolean> contentArray = new ArrayList<>();
        for (int i = 0; i < command.Content.length; i++) {
            // 核心数据交互
            OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(command.Content[i]);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 返回正确的数据信息
            boolean[] array = new boolean[read.Content.length];
            for (int j = 0; j < array.length; j++) {
                array[j] = read.Content[j] != 0x00;
            }
            Utilities.ArrayListAddArray(contentArray, array);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.getBools(contentArray));
    }

    /**
     * 向PLC中位软元件写入bool数组，返回是否写入成功，比如你写入D100,values[0]对应D100.0，地址格式为"D100.0","C100.0","W100.0","H100.0","A100.0"<br />
     * Write the bool array to the PLC's median device and return whether the write was successful. For example, if you write D100, values [0] corresponds to D100.0
     *
     * @param omron   PLC设备的连接对象
     * @param address 要写入的数据地址
     * @param values  要写入的实际数据，可以指定任意的长度
     * @return 返回写入结果
     */
    public static OperateResult Write(IReadWriteDevice omron, String address, boolean[] values) {
        byte[] array = new byte[values.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = values[i] ? (byte) 0x01 : (byte) 0x00;
        }
        // 获取指令
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildWriteWordCommand(address, array, true);
        if (!command.IsSuccess) return command;

        // 核心数据交互
        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 写入成功
        return OperateResult.CreateSuccessResult();
    }

    // endregion

}
