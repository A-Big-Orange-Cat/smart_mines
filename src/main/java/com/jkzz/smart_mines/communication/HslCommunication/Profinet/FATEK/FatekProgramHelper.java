package com.jkzz.smart_mines.communication.HslCommunication.Profinet.FATEK;

import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Address.FatekProgramAddress;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.IReadWriteDevice;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.HslHelper;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExTwo;
import com.jkzz.smart_mines.communication.HslCommunication.StringResources;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FatekProgramHelper {

    // region Static Helper

    /**
     * 计算指令的和校验码
     *
     * @param data 指令
     * @return 校验之后的信息
     */
    public static String CalculateAcc(String data) {
        byte[] buffer = data.getBytes(StandardCharsets.US_ASCII);

        int count = 0;
        for (int i = 0; i < buffer.length; i++) {
            count += buffer[i];
        }

        return String.format("%04X", count).substring(2);
    }

    /**
     * 创建一条读取的指令信息，需要指定一些参数
     *
     * @param station PLC的站号
     * @param address 地址信息
     * @param length  数据长度
     * @param isBool  是否位读取
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(byte station, String address, short length, boolean isBool) {
        OperateResultExTwo<Integer, String> extractParameter = HslHelper.ExtractParameter(address, "s", station);
        station = extractParameter.Content1.byteValue();
        address = extractParameter.Content2;

        OperateResultExOne<FatekProgramAddress> addressAnalysis = FatekProgramAddress.ParseFrom(address, length);
        if (!addressAnalysis.IsSuccess) return OperateResultExOne.CreateFailedResult(addressAnalysis);

        ArrayList<byte[]> contentArray = new ArrayList<>();
        int[] splits = SoftBasic.SplitIntegerToArray(length, 255);
        for (int i = 0; i < splits.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append((char) 0x02);
            stringBuilder.append(String.format("%02X", station));

            if (isBool) {
                stringBuilder.append("44");
                stringBuilder.append(String.format("%02X", splits[i]));
            } else {
                stringBuilder.append("46");
                stringBuilder.append(String.format("%02X", splits[i]));
                if (addressAnalysis.Content.getDataCode().startsWith("X") ||
                        addressAnalysis.Content.getDataCode().startsWith("Y") ||
                        addressAnalysis.Content.getDataCode().startsWith("M") ||
                        addressAnalysis.Content.getDataCode().startsWith("S") ||
                        addressAnalysis.Content.getDataCode().startsWith("T") ||
                        addressAnalysis.Content.getDataCode().startsWith("C")) {
                    stringBuilder.append("W");
                }
            }

            stringBuilder.append(addressAnalysis.Content.toString());
            stringBuilder.append(CalculateAcc(stringBuilder.toString()));
            stringBuilder.append((char) 0x03);

            contentArray.add(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
            addressAnalysis.Content.setAddressStart(addressAnalysis.Content.getAddressStart() + splits[i]);
        }

        return OperateResultExOne.CreateSuccessResult(contentArray);
    }

    /**
     * 提取当前的结果数据信息，针对的是字单位的方式
     *
     * @param response PLC返回的数据信息
     * @param length   读取的长度内容
     * @return 结果数组
     */
    public static byte[] ExtraResponse(byte[] response, short length) {
        byte[] Content = new byte[length * 2];
        for (int i = 0; i < Content.length / 2; i++) {
            int tmp = Integer.parseInt(new String(response, i * 4 + 6, 4, StandardCharsets.US_ASCII), 16);
            byte[] buffer = Utilities.getBytes(tmp);
            Content[i * 2] = buffer[0];
            Content[i * 2 + 1] = buffer[1];
        }
        return Content;
    }

    /**
     * 创建一条别入bool数据的指令信息，需要指定一些参数
     *
     * @param station 站号
     * @param address 地址
     * @param value   数组值
     * @return 是否创建成功
     */
    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(byte station, String address, boolean[] value) {
        OperateResultExTwo<Integer, String> extractParameter = HslHelper.ExtractParameter(address, "s", station);
        station = extractParameter.Content1.byteValue();
        address = extractParameter.Content2;

        OperateResultExOne<FatekProgramAddress> addressAnalysis = FatekProgramAddress.ParseFrom(address, 0);
        if (!addressAnalysis.IsSuccess) return OperateResultExOne.CreateFailedResult(addressAnalysis);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((char) 0x02);
        stringBuilder.append(String.format("%02X", station));

        stringBuilder.append("45");
        stringBuilder.append(String.format("%02X", value.length));

        stringBuilder.append(addressAnalysis.Content.toString());

        for (int i = 0; i < value.length; i++) {
            stringBuilder.append(value[i] ? "1" : "0");
        }

        stringBuilder.append(CalculateAcc(stringBuilder.toString()));
        stringBuilder.append((char) 0x03);

        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 创建一条别入byte数据的指令信息，需要指定一些参数，按照字单位
     *
     * @param station 站号
     * @param address 地址
     * @param value   数组值
     * @return 是否创建成功
     */
    public static OperateResultExOne<byte[]> BuildWriteByteCommand(byte station, String address, byte[] value) {
        OperateResultExTwo<Integer, String> extractParameter = HslHelper.ExtractParameter(address, "s", station);
        station = extractParameter.Content1.byteValue();
        address = extractParameter.Content2;

        OperateResultExOne<FatekProgramAddress> addressAnalysis = FatekProgramAddress.ParseFrom(address, 0);
        if (!addressAnalysis.IsSuccess) return OperateResultExOne.CreateFailedResult(addressAnalysis);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((char) 0x02);
        stringBuilder.append(String.format("%02X", station));

        stringBuilder.append("47");
        stringBuilder.append(String.format("%02X", (value.length / 2)));


        if (addressAnalysis.Content.getDataCode().startsWith("X") ||
                addressAnalysis.Content.getDataCode().startsWith("Y") ||
                addressAnalysis.Content.getDataCode().startsWith("M") ||
                addressAnalysis.Content.getDataCode().startsWith("S") ||
                addressAnalysis.Content.getDataCode().startsWith("T") ||
                addressAnalysis.Content.getDataCode().startsWith("C")) {
            stringBuilder.append("W");
        }

        stringBuilder.append(addressAnalysis.Content.toString());

        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length / 2; i++) {
            byte[] tmp = SoftBasic.BuildAsciiBytesFrom(Utilities.getShort(value, i * 2));
            System.arraycopy(tmp, 0, buffer, 4 * i, tmp.length);
        }
        stringBuilder.append(new String(buffer, StandardCharsets.US_ASCII));

        stringBuilder.append(CalculateAcc(stringBuilder.toString()));
        stringBuilder.append((char) 0x03);

        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 检查PLC反馈的报文是否正确，如果不正确，返回错误消息
     *
     * @param content PLC反馈的报文信息
     * @return 反馈的报文是否正确
     */
    public static OperateResult CheckResponse(byte[] content) {
        if (content[0] != 0x02)
            return new OperateResult(content[0], "Write Faild:" + SoftBasic.ByteToHexString(content, ' '));
        if (content[5] != 0x30) return new OperateResult(content[5], GetErrorDescriptionFromCode((char) content[5]));
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 根据错误码获取到真实的文本信息
     *
     * @param code 错误码
     * @return 错误的文本描述
     */
    public static String GetErrorDescriptionFromCode(char code) {
        switch (code) {
            case '2':
                return StringResources.Language.FatekStatus02();
            case '3':
                return StringResources.Language.FatekStatus03();
            case '4':
                return StringResources.Language.FatekStatus04();
            case '5':
                return StringResources.Language.FatekStatus05();
            case '6':
                return StringResources.Language.FatekStatus06();
            case '7':
                return StringResources.Language.FatekStatus07();
            case '9':
                return StringResources.Language.FatekStatus09();
            case 'A':
                return StringResources.Language.FatekStatus10();
            default:
                return StringResources.Language.UnknownError();
        }
    }

    // endregion

    /**
     * 批量读取PLC的字节数据，以字为单位，支持读取X,Y,M,S,D,T,C,R,RT,RC具体的地址范围需要根据PLC型号来确认，地址可以携带站号信息，例如 s=2;D100<br />
     * Read PLC byte data in batches, in word units. Supports reading X, Y, M, S, D, T, C, R, RT, RC.
     * The specific address range needs to be confirmed according to the PLC model, The address can carry station number information, such as s=2;D100
     *
     * @param device  PLC通信的对象
     * @param station 设备的站点信息
     * @param address 地址信息
     * @param length  数据长度
     * @return 读取结果信息
     */
    public static OperateResultExOne<byte[]> Read(IReadWriteDevice device, byte station, String address, short length) {
        // 解析指令
        OperateResultExOne<ArrayList<byte[]>> command = BuildReadCommand(station, address, length, false);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> content = new ArrayList<>();
        int[] splits = SoftBasic.SplitIntegerToArray(length, 255);
        for (int i = 0; i < command.Content.size(); i++) {
            // 核心交互
            OperateResultExOne<byte[]> read = device.ReadFromCoreServer(command.Content.get(i));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 结果验证
            OperateResult check = CheckResponse(read.Content);
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

            // 提取结果
            Utilities.ArrayListAddArray(content, ExtraResponse(read.Content, (short) splits[i]));
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(content));
    }

    /**
     * 批量写入PLC的数据，以字为单位，也就是说最少2个字节信息，支持X,Y,M,S,D,T,C,R,RT,RC具体的地址范围需要根据PLC型号来确认，地址可以携带站号信息，例如 s=2;D100<br />
     * The data written to the PLC in batches, in units of words, that is, at least 2 bytes of information,
     * supporting X, Y, M, S, D, T, C, R, RT, and RC. The specific address range needs to be based on the PLC model To confirm, The address can carry station number information, such as s=2;D100
     *
     * @param device  PLC通信的对象
     * @param station 设备的站号信息
     * @param address 地址信息，举例，D100，R200，RC100，RT200
     * @param value   数据值
     * @return 是否写入成功
     */
    public static OperateResult Write(IReadWriteDevice device, byte station, String address, byte[] value) {
        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteByteCommand(station, address, value);
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 结果验证
        OperateResult check = CheckResponse(read.Content);
        if (!check.IsSuccess) return check;

        // 提取结果
        return OperateResult.CreateSuccessResult();
    }


    /**
     * 批量读取bool类型数据，支持的类型为X,Y,M,S,T,C，具体的地址范围取决于PLC的类型，地址可以携带站号信息，例如 s=2;M100<br />
     * Read bool data in batches. The supported types are X, Y, M, S, T, C. The specific address range depends on the type of PLC,
     * The address can carry station number information, such as s=2;M100
     *
     * @param device  PLC通信对象
     * @param station 设备的站号信息
     * @param address 地址信息，比如X10，Y17，M100
     * @param length  读取的长度
     * @return 读取结果信息
     */
    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteDevice device, byte station, String address, short length) {
        // 解析指令
        OperateResultExOne<ArrayList<byte[]>> command = BuildReadCommand(station, address, length, true);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        // 核心交互
        ArrayList<Boolean> content = new ArrayList<Boolean>();
        int[] splits = SoftBasic.SplitIntegerToArray(length, 255);
        for (int i = 0; i < command.Content.size(); i++) {
            OperateResultExOne<byte[]> read = device.ReadFromCoreServer(command.Content.get(i));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 结果验证
            OperateResult check = CheckResponse(read.Content);
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

            // 提取结果
            byte[] buffer = SoftBasic.BytesArraySelectMiddle(read.Content, 6, splits[i]);
            boolean[] booleans = new boolean[buffer.length];
            for (int j = 0; j < booleans.length; j++) {
                booleans[j] = buffer[j] == 0x31;
            }
            Utilities.ArrayListAddArray(content, booleans);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToBoolArray(content));
    }

    /**
     * 批量写入bool类型的数组，支持的类型为X,Y,M,S,T,C，具体的地址范围取决于PLC的类型，地址可以携带站号信息，例如 s=2;M100<br />
     * Write arrays of type bool in batches. The supported types are X, Y, M, S, T, C. The specific address range depends on the type of PLC,
     * The address can carry station number information, such as s=2;M100
     *
     * @param device  PLC通信对象
     * @param station 站号信息
     * @param address PLC的地址信息
     * @param value   数据信息
     * @return 是否写入成功
     */
    public static OperateResult Write(IReadWriteDevice device, byte station, String address, boolean[] value) {
        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteBoolCommand(station, address, value);
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 结果验证
        OperateResult check = CheckResponse(read.Content);
        if (!check.IsSuccess) return check;

        // 提取结果
        return OperateResult.CreateSuccessResult();
    }
}
