package com.jkzz.smart_mines.communication.HslCommunication.Robot.FANUC;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.IByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * Fanuc机器人的报警对象
 */
public class FanucAlarm {

    /**
     * AlarmID
     */
    public short AlarmID = 0;

    /**
     * AlarmNumber
     */
    public short AlarmNumber = 0;

    /**
     * CauseAlarmID
     */
    public short CauseAlarmID = 0;

    /**
     * CauseAlarmNumber
     */
    public short CauseAlarmNumber = 0;

    /**
     * Severity
     */
    public short Severity = 0;

    /**
     * Time
     */
    public Date Time = new Date();

    /**
     * AlarmMessage
     */
    public String AlarmMessage = "";

    /**
     * CauseAlarmMessage
     */
    public String CauseAlarmMessage = "";

    /**
     * SeverityMessage
     */
    public String SeverityMessage = "";

    /**
     * 从数据内容创建报警信息
     *
     * @param byteTransform 字节变换
     * @param content       原始的字节内容
     * @param index         索引
     * @param encoding      编码
     * @return 报警信息
     */
    public static FanucAlarm PraseFrom(IByteTransform byteTransform, byte[] content, int index, Charset encoding) {
        FanucAlarm fanucAlarm = new FanucAlarm();
        fanucAlarm.LoadByContent(byteTransform, content, index, encoding);
        return fanucAlarm;
    }

    /**
     * 从字节数据加载真实的信息
     *
     * @param byteTransform 字节变换
     * @param content       原始的字节内容
     * @param index         索引
     * @param encoding      编码
     */
    public void LoadByContent(IByteTransform byteTransform, byte[] content, int index, Charset encoding) {
        AlarmID = Utilities.getShort(content, index);
        AlarmNumber = Utilities.getShort(content, index + 2);
        CauseAlarmID = Utilities.getShort(content, index + 4);
        CauseAlarmNumber = Utilities.getShort(content, index + 6);
        Severity = Utilities.getShort(content, index + 8);

        if (Utilities.getShort(content, index + 10) > 0) {
            Time = new Date(Utilities.getShort(content, index + 10), Utilities.getShort(content, index + 12), Utilities.getShort(content, index + 14),
                    Utilities.getShort(content, index + 16), Utilities.getShort(content, index + 18), Utilities.getShort(content, index + 20));
        }

        AlarmMessage = new String(content, index + 22, 80, encoding).trim();
        CauseAlarmMessage = new String(content, index + 102, 80, encoding).trim();
        SeverityMessage = new String(content, index + 182, 18, encoding).trim();
    }

    public String toString() {
        return "FanucAlarm ID[" + AlarmID + "," + AlarmNumber + "," + CauseAlarmID + "," + CauseAlarmNumber +
                "," + Severity + "]" + System.lineSeparator() + AlarmMessage + System.lineSeparator() + CauseAlarmMessage +
                System.lineSeparator() + SeverityMessage;
    }
}
