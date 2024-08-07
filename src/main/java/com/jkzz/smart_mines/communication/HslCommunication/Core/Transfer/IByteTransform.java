package com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer;

import java.nio.charset.Charset;

public interface IByteTransform {

    /**
     * 从缓存中提取出bool结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @return boolean值
     */
    boolean TransBool(byte[] buffer, int index);

    /**
     * 从缓存中提取出bool数组结果
     *
     * @param buffer 缓存数据
     * @param index  位的索引
     * @param length bool长度
     * @return bool数组
     */
    boolean[] TransBool(byte[] buffer, int index, int length);

    /**
     * 缓存中提取byte结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @return byte对象
     */
    byte TransByte(byte[] buffer, int index);

    /**
     * 从缓存中提取byte数组结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @param length 读取的数组长度
     * @return byte数组
     */
    byte[] TransByte(byte[] buffer, int index, int length);

    /**
     * 从缓存中提取short结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @return short对象
     */
    short TransInt16(byte[] buffer, int index);

    /**
     * 从缓存中提取short结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @param length 读取的数组长度
     * @return short数组对象
     */
    short[] TransInt16(byte[] buffer, int index, int length);

    /**
     * 从缓存中提取ushort结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @return short对象
     */
    int TransUInt16(byte[] buffer, int index);

    /**
     * 从缓存中提取ushort结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @param length 读取的数组长度
     * @return short数组对象
     */
    int[] TransUInt16(byte[] buffer, int index, int length);

    /**
     * 从缓存中提取int结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @return int对象
     */
    int TransInt32(byte[] buffer, int index);

    /**
     * 从缓存中提取int数组结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @param length 读取的数组长度
     * @return int数组对象
     */
    int[] TransInt32(byte[] buffer, int index, int length);

    /**
     * 从缓存中提取uint结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @return int对象
     */
    long TransUInt32(byte[] buffer, int index);

    /**
     * 从缓存中提取uint数组结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @param length 读取的数组长度
     * @return int数组对象
     */
    long[] TransUInt32(byte[] buffer, int index, int length);

    /**
     * 从缓存中提取long结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @return long对象
     */
    long TransInt64(byte[] buffer, int index);

    /**
     * 从缓存中提取long数组结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @param length 读取的数组长度
     * @return long数组对象
     */
    long[] TransInt64(byte[] buffer, int index, int length);

    /**
     * 从缓存中提取float结果
     *
     * @param buffer 缓存对象
     * @param index  索引位置
     * @return float对象
     */
    float TransSingle(byte[] buffer, int index);

    /**
     * 从缓存中提取float数组结果
     *
     * @param buffer 缓存数据
     * @param index  索引位置
     * @param length 读取的数组长度
     * @return float数组对象
     */
    float[] TransSingle(byte[] buffer, int index, int length);

    /**
     * 从缓存中提取double结果
     *
     * @param buffer 缓存对象
     * @param index  索引位置
     * @return double对象
     */
    double TransDouble(byte[] buffer, int index);

    /**
     * 从缓存中提取double数组结果
     *
     * @param buffer 缓存对象
     * @param index  索引位置
     * @param length 读取的数组长度
     * @return double数组
     */
    double[] TransDouble(byte[] buffer, int index, int length);

    /**
     * 从缓存中提取string结果，使用指定的编码
     *
     * @param buffer   缓存对象
     * @param index    索引位置
     * @param length   byte数组长度
     * @param encoding 字符串的编码
     * @return string对象
     */
    String TransString(byte[] buffer, int index, int length, Charset encoding);


    /**
     * bool变量转化缓存数据
     *
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte(boolean value);

    /**
     * bool数组变量转化缓存数据
     *
     * @param values 等待转化的数组
     * @return buffer数据
     */
    byte[] TransByte(boolean[] values);

    /**
     * byte变量转化缓存数据
     *
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte(byte value);

    /**
     * short变量转化缓存数据
     *
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte(short value);

    /**
     * short数组变量转化缓存数据
     *
     * @param values 等待转化的数组
     * @return buffer数据
     */
    byte[] TransByte(short[] values);

    /**
     * int变量转化缓存数据
     *
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte(int value);

    /**
     * int数组变量转化缓存数据
     *
     * @param values 等待转化的数组
     * @return buffer数据
     */
    byte[] TransByte(int[] values);

    /**
     * long变量转化缓存数据
     *
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte(long value);

    /**
     * long数组变量转化缓存数据
     *
     * @param values 等待转化的数组
     * @return v
     */
    byte[] TransByte(long[] values);

    /**
     * float变量转化缓存数据
     *
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte(float value);

    /**
     * float数组变量转化缓存数据
     *
     * @param values 等待转化的数组
     * @return buffer数据
     */
    byte[] TransByte(float[] values);

    /**
     * double变量转化缓存数据
     *
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte(double value);

    /**
     * double数组变量转化缓存数据
     *
     * @param values 等待转化的数组
     * @return buffer数据
     */
    byte[] TransByte(double[] values);

    /**
     * 使用指定的编码字符串转化缓存数据
     *
     * @param value    等待转化的数据
     * @param encoding 字符串的编码方式
     * @return buffer数据
     */
    byte[] TransByte(String value, Charset encoding);

    /**
     * 获取数据解析的格式，默认ABCD，可选BADC，CDAB，DCBA格式
     *
     * @return 数据格式
     */
    DataFormat getDataFormat();

    /**
     * 设置数据解析的格式，ABCD，BADC，CDAB，DCBA格式
     *
     * @param dataFormat 数据格式
     */
    void setDataFormat(DataFormat dataFormat);

    /**
     * 获取字符串是否按照反转来设置
     *
     * @return 是否反转
     */
    boolean getIsStringReverse();

    /**
     * 设置当前的字符串是否需要按照字进行反转
     *
     * @param value 设置值
     */
    void setIsStringReverse(boolean value);

    /**
     * 根据指定的 {@link DataFormat} 格式，来实例化一个新的对象，除了{@link DataFormat}不同，其他都相同<br />
     * ccording to the specified {@link DataFormat} format, to instantiate a new object, except that {@link DataFormat} is different, everything else is the same
     *
     * @param dataFormat 数据格式
     * @return 对象
     */
    IByteTransform CreateByDateFormat(DataFormat dataFormat);

}
