package com.jkzz.smart_mines.communication.HslCommunication.Core.Types;

import com.jkzz.smart_mines.communication.HslCommunication.Authorization;
import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.DataFormat;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.IByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.StringResources;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HslHelper {
    /**
     * 解析地址的附加参数方法，比如你的地址是s=100;D100，可以提取出"s"的值的同时，修改地址本身，如果"s"不存在的话，返回给定的默认值<br />
     * The method of parsing additional parameters of the address, for example, if your address is s=100;D100, you can extract the value of "s" and modify the address itself.
     * If "s" does not exist, return the given default value
     *
     * @param address      复杂的地址格式，比如：s=100;D100
     * @param paraName     等待提取的参数名称
     * @param defaultValue 如果提取的参数信息不存在，返回的默认值信息
     * @return 总是返回成功
     */
    public static OperateResultExTwo<Integer, String> ExtractParameter(String address, String paraName, int defaultValue) {
        OperateResultExTwo<Integer, String> extra = ExtractParameter(address, paraName);
        return extra.IsSuccess ? extra : OperateResultExTwo.CreateSuccessResult(defaultValue, address);
    }

    /**
     * 解析地址的附加参数方法，比如你的地址是s=100;D100，可以提取出"s"的值的同时，修改地址本身，如果"s"不存在的话，返回错误的消息内容<br />
     * The method of parsing additional parameters of the address, for example, if your address is s=100;D100, you can extract the value of "s" and modify the address itself.
     * If "s" does not exist, return the wrong message content
     *
     * @param address  复杂的地址格式，比如：s=100;D100
     * @param paraName 等待提取的参数名称
     * @return 解析后的参数结果内容
     */
    public static OperateResultExTwo<Integer, String> ExtractParameter(String address, String paraName) {
        try {
            Pattern r = Pattern.compile(paraName + "=[0-9A-Fa-fx]+;");
            Matcher match = r.matcher(address);
            if (!match.find()) return new OperateResultExTwo<Integer, String>(
                    "Address [" + address + "] can't find [" + paraName + "] Parameters. for example : " + paraName + "=1;100");

            String number = match.group().substring(paraName.length() + 1, match.group().length() - paraName.length() - 2);
            int value = number.startsWith("0x") ? Integer.parseInt(number.substring(2), 16) :
                    number.startsWith("0") ? Integer.parseInt(number, 8) : Integer.parseInt(number);

            address = address.replace(match.group(), "");
            return OperateResultExTwo.CreateSuccessResult(value, address);
        } catch (Exception ex) {
            return new OperateResultExTwo<Integer, String>("Address [" + address + "] Get [" + paraName + "] Parameters failed: " + ex.getMessage());
        }
    }

    /**
     * 解析地址的起始地址的方法，比如你的地址是 A[1] , 那么将会返回 1，地址修改为 A，如果不存在起始地址，那么就不修改地址，返回 -1<br />
     * The method of parsing the starting address of the address, for example, if your address is A[1], then it will return 1,
     * and the address will be changed to A. If the starting address does not exist, then the address will not be changed and return -1
     *
     * @param address 复杂的地址格式，比如：A[0]
     * @return 如果存在，就起始位置，不存在就返回 -1
     */
    public static OperateResultExTwo<Integer, String> ExtractStartIndex(String address) {
        try {
            Pattern r = Pattern.compile("\\[[0-9]+\\]$");
            Matcher match = r.matcher(address);
            if (!match.find()) return OperateResultExTwo.CreateSuccessResult(-1, address);

            String number = match.group().substring(1, match.group().length() - 2);
            int value = Integer.parseInt(number);

            address = address.substring(match.group().length());
            return OperateResultExTwo.CreateSuccessResult(value, address);
        } catch (Exception ex) {
            return OperateResultExTwo.CreateSuccessResult(-1, address);
        }
    }

    /**
     * 解析地址的附加 {@link DataFormat} 参数方法，比如你的地址是format=ABCD;D100，可以提取出"format"的值的同时，修改地址本身，如果"format"不存在的话，返回默认的 {@link IByteTransform} 对象<br />
     * Parse the additional {@link DataFormat} parameter method of the address. For example, if your address is format=ABCD;D100,
     * you can extract the value of "format" and modify the address itself. If "format" does not exist,
     * Return the default {@link IByteTransform} object
     *
     * @param address          复杂的地址格式，比如：format=BADC;D100
     * @param defaultTransform 默认的数据转换信息
     * @return 解析后的参数结果内容
     */
    public static OperateResultExTwo<IByteTransform, String> ExtractTransformParameter(String address, IByteTransform defaultTransform) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw())
            return OperateResultExTwo.CreateSuccessResult(defaultTransform, address);

        try {
            String paraName = "format";
            Pattern r = Pattern.compile(paraName + "=(ABCD|BADC|DCBA|CDAB);", Pattern.CASE_INSENSITIVE);
            Matcher match = r.matcher(address);
            if (!match.find()) return OperateResultExTwo.CreateSuccessResult(defaultTransform, address);

            String format = match.group(0).substring(paraName.length() + 1, match.group(0).length() - paraName.length() - 2);
            DataFormat dataFormat = defaultTransform.getDataFormat();

            switch (format.toUpperCase()) {
                case "ABCD":
                    dataFormat = DataFormat.ABCD;
                    break;
                case "BADC":
                    dataFormat = DataFormat.BADC;
                    break;
                case "DCBA":
                    dataFormat = DataFormat.DCBA;
                    break;
                case "CDAB":
                    dataFormat = DataFormat.CDAB;
                    break;
                default:
                    break;
            }

            address = address.replace(match.group(0), "");
            if (dataFormat != defaultTransform.getDataFormat()) return OperateResultExTwo.CreateSuccessResult(
                    defaultTransform.CreateByDateFormat(dataFormat), address);
            return OperateResultExTwo.CreateSuccessResult(defaultTransform, address);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 切割当前的地址数据信息，根据读取的长度来分割成多次不同的读取内容，需要指定地址，总的读取长度，切割读取长度<br />
     * Cut the current address data information, and divide it into multiple different read contents according to the read length.
     * You need to specify the address, the total read length, and the cut read length
     *
     * @param address 整数的地址信息
     * @param length  读取长度信息
     * @param segment 切割长度信息
     * @return 切割结果
     */
    public static OperateResultExTwo<int[], int[]> SplitReadLength(int address, short length, short segment) {
        int[] segments = SoftBasic.SplitIntegerToArray(length, segment);
        int[] addresses = new int[segments.length];
        for (int i = 0; i < addresses.length; i++) {
            if (i == 0) addresses[i] = address;
            else addresses[i] = addresses[i - 1] + segments[i - 1];
        }
        return OperateResultExTwo.CreateSuccessResult(addresses, segments);
    }

    /**
     * 根据指定的长度切割数据数组，返回地址偏移量信息和数据分割信息
     *
     * @param tClass        数组类型
     * @param address       起始的地址
     * @param value         实际的数据信息
     * @param segment       分割的基本长度
     * @param addressLength 一个地址代表的数据长度
     * @param <T>           数组类型
     * @return 切割结果内容
     */
    public static <T> OperateResultExTwo<int[], ArrayList<T[]>> SplitWriteData(Class<T> tClass, int address, T[] value, short segment, int addressLength) {
        ArrayList<T[]> segments = SoftBasic.ArraySplitByLength(tClass, value, segment * addressLength);
        int[] addresses = new int[segments.size()];
        for (int i = 0; i < addresses.length; i++) {
            if (i == 0) addresses[i] = address;
            else addresses[i] = addresses[i - 1] + segments.get(i - 1).length / addressLength;
        }
        return OperateResultExTwo.CreateSuccessResult(addresses, segments);
    }

    /**
     * 获取地址信息的位索引，在地址最后一个小数点的位置
     *
     * @param address 地址信息
     * @return 位索引的位置
     */
    public static OperateResultExTwo<Integer, String> GetBitIndexInformation(String address) {
        int bitIndex = 0;
        int lastIndex = address.lastIndexOf('.');
        if (lastIndex > 0 && lastIndex < address.length() - 1) {
            String bit = address.substring(lastIndex + 1);
            if (bit.contains("A") || bit.contains("B") || bit.contains("C") || bit.contains("D") || bit.contains("E") || bit.contains("F")) {
                bitIndex = Integer.parseInt(bit, 16);
            } else {
                bitIndex = Integer.parseInt(bit);
            }
            address = address.substring(0, lastIndex);
        }
        return OperateResultExTwo.CreateSuccessResult(bitIndex, address);
    }

//    /// <summary>
//    /// 从当前的字符串信息获取IP地址数据，如果是ip地址直接返回，如果是域名，会自动解析IP地址，否则抛出异常<br />
//    /// Get the IP address data from the current string information, if it is an ip address, return directly,
//    /// if it is a domain name, it will automatically resolve the IP address, otherwise an exception will be thrown
//    /// </summary>
//    /// <param name="value">输入的字符串信息</param>
//    /// <returns>真实的IP地址信息</returns>
//    public static String GetIpAddressFromInput( String value ) throws Exception {
//        if (!Utilities.IsStringNullOrEmpty( value ))
//        {
//            // 正则表达值校验Ip地址
//            if (value.matches( "^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$" ))
//            {
//                if (!IPAddress.TryParse( value, out IPAddress address ))
//                {
//                    throw new Exception( StringResources.Language.IpAddressError() );
//                }
//                return value;
//            }
//            else
//            {
//                IPHostEntry host = Dns.GetHostEntry( value );
//                IPAddress[] iPs = host.AddressList;
//                if (iPs.Length > 0) return iPs[0].ToString( );
//            }
//        }
//        return "127.0.0.1";
//    }
//
//    /// <summary>
//    /// 从流中接收指定长度的字节数组
//    /// </summary>
//    /// <param name="stream">流</param>
//    /// <param name="length">数据长度</param>
//    /// <returns>二进制的字节数组</returns>
//    public static byte[] ReadSpecifiedLengthFromStream( Stream stream, int length )
//    {
//        byte[] buffer = new byte[length];
//        int receive = 0;
//        while (receive < length)
//        {
//            int count = stream.Read( buffer, receive, buffer.Length - receive );
//            receive += count;
//            if (count == 0) break;
//        }
//        return buffer;
//    }
//
//    /// <summary>
//    /// 将字符串的内容写入到流中去
//    /// </summary>
//    /// <param name="stream">数据流</param>
//    /// <param name="value">字符串内容</param>
//    public static void WriteStringToStream( Stream stream, string value )
//    {
//        byte[] buffer = string.IsNullOrEmpty( value ) ? new byte[0] : Encoding.UTF8.GetBytes( value );
//        WriteBinaryToStream( stream, buffer );
//    }
//
//    /// <summary>
//    /// 从流中读取一个字符串内容
//    /// </summary>
//    /// <param name="stream">数据流</param>
//    /// <returns>字符串信息</returns>
//    public static string ReadStringFromStream( Stream stream )
//    {
//        byte[] buffer = ReadBinaryFromStream( stream );
//        return Encoding.UTF8.GetString( buffer );
//    }
//
//    /// <summary>
//    /// 将二进制的内容写入到数据流之中
//    /// </summary>
//    /// <param name="stream">数据流</param>
//    /// <param name="value">原始字节数组</param>
//    public static void WriteBinaryToStream( Stream stream, byte[] value )
//    {
//        stream.Write( BitConverter.GetBytes( value.Length ), 0, 4 );
//        stream.Write( value, 0, value.Length );
//    }
//
//    /// <summary>
//    /// 从流中读取二进制的内容
//    /// </summary>
//    /// <param name="stream">数据流</param>
//    /// <returns>字节数组</returns>
//    public static byte[] ReadBinaryFromStream( Stream stream )
//    {
//        byte[] lengthBuffer = ReadSpecifiedLengthFromStream( stream, 4 );
//        int length = BitConverter.ToInt32( lengthBuffer, 0 );
//        if (length <= 0) return new byte[0];
//        return ReadSpecifiedLengthFromStream( stream, length );
//    }

    /**
     * 从字符串的内容提取UTF8编码的字节，加了对空的校验
     *
     * @param message 字符串内容
     * @return 结果
     */
    public static byte[] GetUTF8Bytes(String message) {
        return Utilities.IsStringNullOrEmpty(message) ? new byte[0] : message.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 根据当前的位偏移地址及读取位长度信息，计算出实际的字节索引，字节数，字节位偏移
     *
     * @param addressStart 起始地址
     * @param length       读取的长度
     * @return 分别返回 newStart, byteLength, offset
     */
    public static OperateResultExThree<Integer, Short, Integer> CalculateStartBitIndexAndLength(int addressStart, short length) {
        OperateResultExThree<Integer, Short, Integer> result = new OperateResultExThree<>();
        result.IsSuccess = true;
        result.Message = StringResources.Language.SuccessText();

        short byteLength = (short) ((addressStart + length - 1) / 8 - addressStart / 8 + 1);
        int offset = addressStart % 8;
        int newStart = addressStart - offset;

        result.Content1 = newStart;
        result.Content2 = byteLength;
        result.Content3 = offset;
        return result;
    }

    /**
     * 根据字符串内容，获取当前的位索引地址，例如输入 6,返回6，输入15，返回15，输入B，返回11
     *
     * @param bit 位字符串
     * @return 结束数据
     */
    public static int CalculateBitStartIndex(String bit) {
        if (bit.contains("A") || bit.contains("B") || bit.contains("C") || bit.contains("D") || bit.contains("E") || bit.contains("F")) {
            return Integer.parseInt(bit, 16);
        } else {
            return Integer.parseInt(bit);
        }
    }
}
