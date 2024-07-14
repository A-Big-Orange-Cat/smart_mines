package com.jkzz.smart_mines.communication.HslCommunication.Robot.FANUC;

import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.IByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

/**
 * 机器人的姿态数据
 */
public class FanucPose {

    /**
     * Xyzwpr
     */
    public float[] Xyzwpr = null;

    /**
     * Config
     */
    public String[] Config = null;

    /**
     * Joint
     */
    public float[] Joint = null;

    /**
     * UF
     */
    public short UF = 0;

    /**
     * UT
     */
    public short UT = 0;

    /**
     * ValidC
     */
    public short ValidC = 0;

    /**
     * ValidJ
     */
    public short ValidJ = 0;

    /**
     * 从原始的字节数据创建一个新的姿态数据
     *
     * @param byteTransform 变换对象
     * @param content       原始的内容
     * @param index         索引位置
     * @return 姿态数据
     */
    public static FanucPose ParseFrom(IByteTransform byteTransform, byte[] content, int index) {
        FanucPose fanucPose = new FanucPose();
        fanucPose.LoadByContent(byteTransform, content, index);
        return fanucPose;
    }

    /**
     * 将short类型的config数组转换成string数组类型的config
     *
     * @param value short数组的值
     * @return string数组的值
     */
    public static String[] TransConfigStringArray(short[] value) {
        String[] array = new String[7];
        array[0] = value[0] != 0 ? "F" : "N";
        array[1] = value[1] != 0 ? "L" : "R";
        array[2] = value[2] != 0 ? "U" : "D";
        array[3] = value[3] != 0 ? "T" : "B";
        array[4] = String.valueOf(value[4]);
        array[5] = String.valueOf(value[5]);
        array[6] = String.valueOf(value[6]);
        return array;
    }

    /**
     * 从原始数据解析出当前的姿态数据
     *
     * @param byteTransform 字节变化内容
     * @param content       原始的内容
     * @param index         索引位置
     */
    public void LoadByContent(IByteTransform byteTransform, byte[] content, int index) {
        Xyzwpr = new float[9];
        for (int i = 0; i < Xyzwpr.length; i++) {
            Xyzwpr[i] = Utilities.getFloat(content, index + 4 * i);
        }
        Config = TransConfigStringArray(byteTransform.TransInt16(content, index + 36, 7));

        Joint = new float[9];
        for (int i = 0; i < Joint.length; i++) {
            Joint[i] = Utilities.getFloat(content, index + 52 + 4 * i);
        }

        ValidC = Utilities.getShort(content, index + 50);
        ValidJ = Utilities.getShort(content, index + 88);
        UF = Utilities.getShort(content, index + 90);
        UT = Utilities.getShort(content, index + 92);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("FanucPose UF=" + UF + " UT=" + UT);
        if (ValidC != 0) {
            sb.append(System.lineSeparator()).
                    append("Xyzwpr=").
                    append(SoftBasic.ArrayFormat(Xyzwpr)).
                    append(System.lineSeparator()).
                    append("Config=").
                    append(SoftBasic.ArrayFormat(Config));
        }
        if (ValidJ != 0) {
            sb.append(System.lineSeparator()).
                    append("JOINT=").
                    append(SoftBasic.ArrayFormat(Joint));
        }
        return sb.toString();
    }
}
