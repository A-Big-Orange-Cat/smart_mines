package com.jkzz.smart_mines.communication.HslCommunication.Profinet.Melsec;


/**
 * 三菱的数据类型
 * Data types of Mitsubishi PLC, here contains several commonly used types
 */
public class MelsecMcDataType {

    /**
     * X输入寄存器
     */
    public final static MelsecMcDataType X = new MelsecMcDataType((byte) (0x9C), (byte) (0x01), "X*", 16);
    /**
     * Y输出寄存器
     */
    public final static MelsecMcDataType Y = new MelsecMcDataType((byte) (0x9D), (byte) (0x01), "Y*", 16);
    /**
     * M中间继电器
     */
    public final static MelsecMcDataType M = new MelsecMcDataType((byte) (0x90), (byte) (0x01), "M*", 10);
    /**
     * SM特殊继电器
     */
    public final static MelsecMcDataType SM = new MelsecMcDataType((byte) 0x91, (byte) 0x01, "SM", 10);
    /**
     * S步进继电器
     */
    public final static MelsecMcDataType S = new MelsecMcDataType((byte) (0x98), (byte) (0x01), "S*", 10);
    /**
     * L锁存继电器
     */
    public final static MelsecMcDataType L = new MelsecMcDataType((byte) (0x92), (byte) (0x01), "L*", 10);
    /**
     * F报警器
     */
    public final static MelsecMcDataType F = new MelsecMcDataType((byte) (0x93), (byte) (0x01), "F*", 10);
    /**
     * V边沿继电器
     */
    public final static MelsecMcDataType V = new MelsecMcDataType((byte) (0x94), (byte) (0x01), "V*", 10);
    /**
     * B链接继电器
     */
    public final static MelsecMcDataType B = new MelsecMcDataType((byte) (0xA0), (byte) (0x01), "B*", 16);
    /**
     * SB特殊连接继电器
     */
    public final static MelsecMcDataType SB = new MelsecMcDataType((byte) 0xA1, (byte) 0x01, "SB", 16);
    /**
     * DX直接访问输入
     */
    public final static MelsecMcDataType DX = new MelsecMcDataType((byte) 0xA2, (byte) 0x01, "DX", 16);
    /**
     * DY直接访问输出
     */
    public final static MelsecMcDataType DY = new MelsecMcDataType((byte) 0xA3, (byte) 0x01, "DY", 16);
    /**
     * D数据寄存器
     */
    public final static MelsecMcDataType D = new MelsecMcDataType((byte) (0xA8), (byte) (0x00), "D*", 10);
    /**
     * SD特殊链接寄存器
     */
    public final static MelsecMcDataType SD = new MelsecMcDataType((byte) 0xA9, (byte) 0x00, "SD", 10);
    /**
     * W链接寄存器
     */
    public final static MelsecMcDataType W = new MelsecMcDataType((byte) (0xB4), (byte) (0x00), "W*", 16);
    /**
     * SW特殊链接寄存器
     */
    public final static MelsecMcDataType SW = new MelsecMcDataType((byte) 0xB5, (byte) 0x00, "SW", 16);
    /**
     * R文件寄存器
     */
    public final static MelsecMcDataType R = new MelsecMcDataType((byte) (0xAF), (byte) (0x00), "R*", 10);
    /**
     * 变址寄存器
     */
    public final static MelsecMcDataType Z = new MelsecMcDataType((byte) (0xCC), (byte) (0x00), "Z*", 10);
    /**
     * 文件寄存器ZR区
     */
    public final static MelsecMcDataType ZR = new MelsecMcDataType((byte) 0xB0, (byte) 0x00, "ZR", 16);
    /**
     * 定时器的值
     */
    public final static MelsecMcDataType TN = new MelsecMcDataType((byte) (0xC2), (byte) (0x00), "TN", 10);
    /**
     * 定时器的触点
     */
    public final static MelsecMcDataType TS = new MelsecMcDataType((byte) 0xC1, (byte) 0x01, "TS", 10);
    /**
     * 定时器的线圈
     */
    public final static MelsecMcDataType TC = new MelsecMcDataType((byte) 0xC0, (byte) 0x01, "TC", 10);
    /**
     * 累计定时器的触点
     */
    public final static MelsecMcDataType SS = new MelsecMcDataType((byte) 0xC7, (byte) 0x01, "SS", 10);
    /**
     * 累计定时器的线圈
     */
    public final static MelsecMcDataType SC = new MelsecMcDataType((byte) 0xC6, (byte) 0x01, "SC", 10);
    /**
     * 累计定时器的当前值
     */
    public final static MelsecMcDataType SN = new MelsecMcDataType((byte) 0xC8, (byte) 0x00, "SN", 100);
    /**
     * 计数器的值
     */
    public final static MelsecMcDataType CN = new MelsecMcDataType((byte) (0xC5), (byte) (0x00), "CN", 10);
    /**
     * 计数器的触点
     */
    public final static MelsecMcDataType CS = new MelsecMcDataType((byte) 0xC4, (byte) 0x01, "CS", 10);
    /**
     * 计数器的线圈
     */
    public final static MelsecMcDataType CC = new MelsecMcDataType((byte) 0xC3, (byte) 0x01, "CC", 10);
    /**
     * X输入继电器
     */
    public final static MelsecMcDataType Keyence_X = new MelsecMcDataType((byte) 0x9C, (byte) 0x01, "X*", 16);
    /**
     * Y输出继电器
     */
    public final static MelsecMcDataType Keyence_Y = new MelsecMcDataType((byte) 0x9D, (byte) 0x01, "Y*", 16);
    /**
     * 链接继电器
     */
    public final static MelsecMcDataType Keyence_B = new MelsecMcDataType((byte) 0xA0, (byte) 0x01, "B*", 16);
    /**
     * 内部辅助继电器
     */
    public final static MelsecMcDataType Keyence_M = new MelsecMcDataType((byte) 0x90, (byte) 0x01, "M*", 10);
    /**
     * 内部辅助继电器
     */
    public final static MelsecMcDataType Keyence_L = new MelsecMcDataType((byte) 0x92, (byte) 0x01, "L*", 10);
    /**
     * 控制继电器
     */
    public final static MelsecMcDataType Keyence_SM = new MelsecMcDataType((byte) 0x91, (byte) 0x01, "SM", 10);
    /**
     * 控制存储器
     */
    public final static MelsecMcDataType Keyence_SD = new MelsecMcDataType((byte) 0xA9, (byte) 0x00, "SD", 10);
    /**
     * 数据存储器
     */
    public final static MelsecMcDataType Keyence_D = new MelsecMcDataType((byte) 0xA8, (byte) 0x00, "D*", 10);
    /**
     * 文件寄存器
     */
    public final static MelsecMcDataType Keyence_R = new MelsecMcDataType((byte) 0xAF, (byte) 0x00, "R*", 10);
    /**
     * 文件寄存器
     */
    public final static MelsecMcDataType Keyence_ZR = new MelsecMcDataType((byte) 0xB0, (byte) 0x00, "ZR", 16);
    /**
     * 链路寄存器
     */
    public final static MelsecMcDataType Keyence_W = new MelsecMcDataType((byte) 0xB4, (byte) 0x00, "W*", 16);
    /**
     * 计时器（当前值）
     */
    public final static MelsecMcDataType Keyence_TN = new MelsecMcDataType((byte) 0xC2, (byte) 0x00, "TN", 10);
    /**
     * 计时器（接点）
     */
    public final static MelsecMcDataType Keyence_TS = new MelsecMcDataType((byte) 0xC1, (byte) 0x01, "TS", 10);
    /**
     * 计数器（当前值）
     */
    public final static MelsecMcDataType Keyence_CN = new MelsecMcDataType((byte) 0xC5, (byte) 0x00, "CN", 10);
    /**
     * 计数器（接点）
     */
    public final static MelsecMcDataType Keyence_CS = new MelsecMcDataType((byte) 0xC4, (byte) 0x01, "CS", 10);
    /**
     * 输入继电器
     */
    public final static MelsecMcDataType Panasonic_X = new MelsecMcDataType((byte) 0x9C, (byte) 0x01, "X*", 10);
    /**
     * 输出继电器
     */
    public final static MelsecMcDataType Panasonic_Y = new MelsecMcDataType((byte) 0x9D, (byte) 0x01, "Y*", 10);
    /**
     * 链接继电器
     */
    public final static MelsecMcDataType Panasonic_L = new MelsecMcDataType((byte) 0xA0, (byte) 0x01, "L*", 10);
    /**
     * 内部继电器
     */
    public final static MelsecMcDataType Panasonic_R = new MelsecMcDataType((byte) 0x90, (byte) 0x01, "R*", 10);
    /**
     * 数据存储器
     */
    public final static MelsecMcDataType Panasonic_DT = new MelsecMcDataType((byte) 0xA8, (byte) 0x00, "D*", 10);
    /**
     * 链接存储器
     */
    public final static MelsecMcDataType Panasonic_LD = new MelsecMcDataType((byte) 0xB4, (byte) 0x00, "W*", 10);
    /**
     * 计时器（当前值）
     */
    public final static MelsecMcDataType Panasonic_TN = new MelsecMcDataType((byte) 0xC2, (byte) 0x00, "TN", 10);
    /**
     * 计时器（接点）
     */
    public final static MelsecMcDataType Panasonic_TS = new MelsecMcDataType((byte) 0xC1, (byte) 0x01, "TS", 10);
    /**
     * 计数器（当前值）
     */
    public final static MelsecMcDataType Panasonic_CN = new MelsecMcDataType((byte) 0xC5, (byte) 0x00, "CN", 10);
    /**
     * 计数器（接点）
     */
    public final static MelsecMcDataType Panasonic_CS = new MelsecMcDataType((byte) 0xC4, (byte) 0x01, "CS", 10);
    /**
     * 特殊链接继电器
     */
    public final static MelsecMcDataType Panasonic_SM = new MelsecMcDataType((byte) 0x91, (byte) 0x01, "SM", 10);
    /**
     * 特殊链接存储器
     */
    public final static MelsecMcDataType Panasonic_SD = new MelsecMcDataType((byte) 0xA9, (byte) 0x00, "SD", 10);
    private byte DataCode = 0x00;                   // 类型代号
    private byte DataType = 0x00;                   // 数据类型
    private String AsciiCode = "";                  // ascii格式通信的字符
    private int FromBase = 0;                       // 类型

    /**
     * 实例化一个三菱数据类型对象，如果您清楚类型代号，可以根据值进行扩展
     * Instantiate a Mitsubishi data type object, if you know the type code, you can expand according to the value
     *
     * @param code      数据类型的代号
     * @param type      0或1，默认为0
     * @param asciiCode ASCII格式的类型信息
     * @param fromBase  指示地址的多少进制的，10或是16
     */
    public MelsecMcDataType(byte code, byte type, String asciiCode, int fromBase) {
        DataCode = code;
        AsciiCode = asciiCode;
        FromBase = fromBase;
        if (type < 2) DataType = type;
    }

    /**
     * 数据的类型代号
     *
     * @return
     */
    public byte getDataCode() {
        return DataCode;
    }

    /**
     * 字访问还是位访问，0表示字，1表示位
     *
     * @return
     */
    public byte getDataType() {
        return DataType;
    }

    /**
     * 当以ASCII格式通讯时的类型描述
     *
     * @return
     */
    public String getAsciiCode() {
        return AsciiCode;
    }

    /**
     * 指示地址是10进制，还是16进制的
     *
     * @return
     */
    public int getFromBase() {
        return FromBase;
    }
}
