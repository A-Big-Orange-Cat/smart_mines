package com.jkzz.smart_mines.communication.HslCommunication.Robot.FANUC;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.IByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

import java.nio.charset.Charset;

/**
 * Fanuc机器人的任务类
 */
public class FanucTask {
    /**
     * ProgramName
     */
    public String ProgramName = "";

    /**
     * LineNumber
     */
    public short LineNumber = 0;

    /**
     * State
     */
    public short State = 0;

    /**
     * ParentProgramName
     */
    public String ParentProgramName = "";

    /**
     * 从原始的数据信息初始化一个任务对象
     *
     * @param byteTransform 字节变换
     * @param content       原始的字节数据
     * @param index         索引信息
     * @param encoding      编码
     * @return 任务对象
     */
    public static FanucTask ParseFrom(IByteTransform byteTransform, byte[] content, int index, Charset encoding) {
        FanucTask fanucTask = new FanucTask();
        fanucTask.LoadByContent(byteTransform, content, index, encoding);
        return fanucTask;
    }

    /**
     * 从原始的数据对象加载数据信息
     *
     * @param byteTransform 字节变换
     * @param content       原始的字节数据
     * @param index         索引信息
     * @param encoding      编码
     */
    public void LoadByContent(IByteTransform byteTransform, byte[] content, int index, Charset encoding) {
        ProgramName = new String(content, index, 16, encoding).trim();
        LineNumber = Utilities.getShort(content, index + 16);
        State = Utilities.getShort(content, index + 18);
        ParentProgramName = new String(content, index + 20, 16, encoding).trim();
    }

    public String toString() {
        return "ProgramName[" + ProgramName + "] LineNumber[" + LineNumber + "] State[" + State + "] ParentProgramName[" + ParentProgramName + "]";
    }
}
