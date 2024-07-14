package com.jkzz.smart_mines.communication.HslCommunication.Robot.FANUC;

import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.IByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.RegularByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fanuc的数据信息
 */
public class FanucData {

    public FanucAlarm[] AlarmList = null;

    public FanucAlarm AlarmCurrent = null;

    public FanucAlarm AlarmPassword = null;

    public FanucPose CurrentPose = null;

    public FanucPose CurrentPoseUF = null;

    public FanucPose CurrentPose2 = null;

    public FanucPose CurrentPose3 = null;

    public FanucPose CurrentPose4 = null;

    public FanucPose CurrentPose5 = null;

    public FanucTask Task = null;

    public FanucTask TaskIgnoreMacro = null;

    public FanucTask TaskIgnoreKarel = null;

    public FanucTask TaskIgnoreMacroKarel = null;

    public FanucPose[] PosRegGP1 = null;

    public FanucPose[] PosRegGP2 = null;

    public FanucPose[] PosRegGP3 = null;

    public FanucPose[] PosRegGP4 = null;

    public FanucPose[] PosRegGP5 = null;

    public int FAST_CLOCK = 0;

    public int Timer10_TIMER_VAL = 0;

    public float MOR_GRP_CURRENT_ANG = 0;

    public float DUTY_TEMP = 0;

    public String TIMER10_COMMENT = "";

    public String TIMER2_COMMENT = "";

    public FanucPose MNUTOOL1_1 = null;

    public String HTTPKCL_CMDS = "";

    public int[] NumReg1 = null;

    public float[] NumReg2 = null;

    public FanucPose[] DataPosRegMG = null;

    public String[] DIComment = null;
    public String[] DOComment = null;
    public String[] RIComment = null;
    public String[] ROComment = null;
    public String[] UIComment = null;
    public String[] UOComment = null;
    public String[] SIComment = null;
    public String[] SOComment = null;
    public String[] WIComment = null;
    public String[] WOComment = null;
    public String[] WSIComment = null;
    public String[] AIComment = null;
    public String[] AOComment = null;
    public String[] GIComment = null;
    public String[] GOComment = null;
    public String[] STRREGComment = null;
    public String[] STRREG_COMMENT_Comment = null;
    private boolean isIni = false;

    /**
     * 从字节数组解析出fanuc的数据信息
     *
     * @param content 原始的字节数组
     * @return fanuc数据
     */
    public static OperateResultExOne<FanucData> PraseFrom(byte[] content) {
        FanucData fanucData = new FanucData();
        fanucData.LoadByContent(content);
        return OperateResultExOne.CreateSuccessResult(fanucData);
    }

    private static void AppendStringBuilder(StringBuilder sb, String name, String value) {
        AppendStringBuilder(sb, name, new String[]{value});
    }

    private static void AppendStringBuilder(StringBuilder sb, String name, String[] values) {
        sb.append(name);
        sb.append(":");
        if (values.length > 1) sb.append(System.lineSeparator());
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]);
            sb.append(System.lineSeparator());
        }
        if (values.length > 1) sb.append(System.lineSeparator());
    }

    private static String[] GetStringArray(byte[] content, int index, int length, int arraySize, Charset encoding) {
        String[] array = new String[arraySize];
        for (int i = 0; i < arraySize; i++)
            array[i] = new String(content, index + length * i, length, encoding).trim();
        return array;
    }

    private static FanucPose[] GetFanucPoseArray(IByteTransform byteTransform, byte[] content, int index, int arraySize, Charset encoding) {
        FanucPose[] array = new FanucPose[arraySize];
        for (int i = 0; i < arraySize; i++)
            array[i] = FanucPose.ParseFrom(byteTransform, content, index + i * 100);
        return array;
    }

    private static FanucAlarm[] GetFanucAlarmArray(IByteTransform byteTransform, byte[] content, int index, int arraySize, Charset encoding) {
        FanucAlarm[] array = new FanucAlarm[arraySize];
        for (int i = 0; i < arraySize; i++)
            array[i] = FanucAlarm.PraseFrom(byteTransform, content, index + 200 * i, encoding);
        return array;
    }

    /**
     * 从原始的数据内容加载数据
     *
     * @param content 原始的内容
     */
    public void LoadByContent(byte[] content) {
        IByteTransform byteTransform = new RegularByteTransform();
        Charset encoding;
        try {
            encoding = Charset.forName("Shift_JIS");
        } catch (Exception ex) {
            encoding = StandardCharsets.UTF_8;
        }

        String[] cmds = FanucHelper.GetFanucCmds();
        int[] indexs = new int[cmds.length - 1];
        int[] length = new int[cmds.length - 1];
        for (int i = 1; i < cmds.length; i++) {
            Matcher matches = Pattern.compile("[0-9]+", Pattern.DOTALL).matcher(cmds[i]);
            if (matches.find()) indexs[i - 1] = (Integer.parseInt(matches.group()) - 1) * 2;
            if (matches.find()) length[i - 1] = Integer.parseInt(matches.group()) * 2;
        }

        AlarmList = GetFanucAlarmArray(byteTransform, content, indexs[0], 5, encoding);
        AlarmCurrent = FanucAlarm.PraseFrom(byteTransform, content, indexs[1], encoding);
        AlarmPassword = FanucAlarm.PraseFrom(byteTransform, content, indexs[2], encoding);
        CurrentPose = FanucPose.ParseFrom(byteTransform, content, indexs[3]);
        CurrentPoseUF = FanucPose.ParseFrom(byteTransform, content, indexs[4]);
        CurrentPose2 = FanucPose.ParseFrom(byteTransform, content, indexs[5]);
        CurrentPose3 = FanucPose.ParseFrom(byteTransform, content, indexs[6]);
        CurrentPose4 = FanucPose.ParseFrom(byteTransform, content, indexs[7]);
        CurrentPose5 = FanucPose.ParseFrom(byteTransform, content, indexs[8]);
        Task = FanucTask.ParseFrom(byteTransform, content, indexs[9], encoding);
        TaskIgnoreMacro = FanucTask.ParseFrom(byteTransform, content, indexs[10], encoding);
        TaskIgnoreKarel = FanucTask.ParseFrom(byteTransform, content, indexs[11], encoding);
        TaskIgnoreMacroKarel = FanucTask.ParseFrom(byteTransform, content, indexs[12], encoding);
        PosRegGP1 = GetFanucPoseArray(byteTransform, content, indexs[13], 10, encoding);
        PosRegGP2 = GetFanucPoseArray(byteTransform, content, indexs[14], 4, encoding);
        PosRegGP3 = GetFanucPoseArray(byteTransform, content, indexs[15], 10, encoding);
        PosRegGP4 = GetFanucPoseArray(byteTransform, content, indexs[16], 10, encoding);
        PosRegGP5 = GetFanucPoseArray(byteTransform, content, indexs[17], 10, encoding);
        FAST_CLOCK = Utilities.getInt(content, indexs[18]);
        Timer10_TIMER_VAL = Utilities.getInt(content, indexs[19]);
        MOR_GRP_CURRENT_ANG = Utilities.getFloat(content, indexs[20]);
        DUTY_TEMP = Utilities.getFloat(content, indexs[21]);
        TIMER10_COMMENT = new String(content, indexs[22], 80, encoding).trim();
        TIMER2_COMMENT = new String(content, indexs[23], 80, encoding).trim();
        MNUTOOL1_1 = FanucPose.ParseFrom(byteTransform, content, indexs[24]);
        HTTPKCL_CMDS = new String(content, indexs[25], 80, encoding).trim();
        NumReg1 = byteTransform.TransInt32(content, indexs[26], 5);
        NumReg2 = byteTransform.TransSingle(content, indexs[27], 5);
        DataPosRegMG = new FanucPose[10];
        for (int i = 0; i < DataPosRegMG.length; i++) {
            DataPosRegMG[i] = new FanucPose();
            DataPosRegMG[i].Xyzwpr = byteTransform.TransSingle(content, indexs[29] + i * 50, 9);
            DataPosRegMG[i].Config = FanucPose.TransConfigStringArray(byteTransform.TransInt16(content, indexs[29] + 36 + i * 50, 7));
            DataPosRegMG[i].Joint = byteTransform.TransSingle(content, indexs[30] + i * 36, 9);
        }
        DIComment = GetStringArray(content, indexs[31], 80, 3, encoding);
        DOComment = GetStringArray(content, indexs[32], 80, 3, encoding);
        RIComment = GetStringArray(content, indexs[33], 80, 3, encoding);
        ROComment = GetStringArray(content, indexs[34], 80, 3, encoding);
        UIComment = GetStringArray(content, indexs[35], 80, 3, encoding);
        UOComment = GetStringArray(content, indexs[36], 80, 3, encoding);
        SIComment = GetStringArray(content, indexs[37], 80, 3, encoding);
        SOComment = GetStringArray(content, indexs[38], 80, 3, encoding);
        WIComment = GetStringArray(content, indexs[39], 80, 3, encoding);
        WOComment = GetStringArray(content, indexs[40], 80, 3, encoding);
        WSIComment = GetStringArray(content, indexs[41], 80, 3, encoding);
        AIComment = GetStringArray(content, indexs[42], 80, 3, encoding);
        AOComment = GetStringArray(content, indexs[43], 80, 3, encoding);
        GIComment = GetStringArray(content, indexs[44], 80, 3, encoding);
        GOComment = GetStringArray(content, indexs[45], 80, 3, encoding);
        STRREGComment = GetStringArray(content, indexs[46], 80, 3, encoding);
        STRREG_COMMENT_Comment = GetStringArray(content, indexs[47], 80, 3, encoding);

        isIni = true;
    }

    public String toString() {
        if (!isIni) return "NULL";

        StringBuilder sb = new StringBuilder();
        AppendStringBuilder(sb, "AlarmList", Utilities.TranslateStringArray(AlarmList));
        AppendStringBuilder(sb, "AlarmCurrent", AlarmCurrent.toString());
        AppendStringBuilder(sb, "AlarmPassword", AlarmPassword.toString());
        AppendStringBuilder(sb, "CurrentPose", CurrentPose.toString());
        AppendStringBuilder(sb, "CurrentPoseUF", CurrentPoseUF.toString());
        AppendStringBuilder(sb, "CurrentPose2", CurrentPose2.toString());
        AppendStringBuilder(sb, "CurrentPose3", CurrentPose3.toString());
        AppendStringBuilder(sb, "CurrentPose4", CurrentPose4.toString());
        AppendStringBuilder(sb, "CurrentPose5", CurrentPose5.toString());
        AppendStringBuilder(sb, "Task", Task.toString());
        AppendStringBuilder(sb, "TaskIgnoreMacro", TaskIgnoreMacro.toString());
        AppendStringBuilder(sb, "TaskIgnoreKarel", TaskIgnoreKarel.toString());
        AppendStringBuilder(sb, "TaskIgnoreMacroKarel", TaskIgnoreMacroKarel.toString());
        AppendStringBuilder(sb, "PosRegGP1", Utilities.TranslateStringArray(PosRegGP1));
        AppendStringBuilder(sb, "PosRegGP2", Utilities.TranslateStringArray(PosRegGP2));
        AppendStringBuilder(sb, "PosRegGP3", Utilities.TranslateStringArray(PosRegGP3));
        AppendStringBuilder(sb, "PosRegGP4", Utilities.TranslateStringArray(PosRegGP4));
        AppendStringBuilder(sb, "PosRegGP5", Utilities.TranslateStringArray(PosRegGP5));
        AppendStringBuilder(sb, "FAST_CLOCK", String.valueOf(FAST_CLOCK));
        AppendStringBuilder(sb, "Timer10_TIMER_VAL", String.valueOf(Timer10_TIMER_VAL));
        AppendStringBuilder(sb, "MOR_GRP_CURRENT_ANG", String.valueOf(MOR_GRP_CURRENT_ANG));
        AppendStringBuilder(sb, "DUTY_TEMP", String.valueOf(DUTY_TEMP));
        AppendStringBuilder(sb, "TIMER10_COMMENT", TIMER10_COMMENT);
        AppendStringBuilder(sb, "TIMER2_COMMENT", TIMER2_COMMENT);
        AppendStringBuilder(sb, "MNUTOOL1_1", MNUTOOL1_1.toString());
        AppendStringBuilder(sb, "HTTPKCL_CMDS", HTTPKCL_CMDS);
        AppendStringBuilder(sb, "NumReg1", SoftBasic.ArrayFormat(NumReg1));
        AppendStringBuilder(sb, "NumReg2", SoftBasic.ArrayFormat(NumReg2));
        AppendStringBuilder(sb, "DataPosRegMG", Utilities.TranslateStringArray(DataPosRegMG));
        AppendStringBuilder(sb, "DIComment", SoftBasic.ArrayFormat(DIComment));
        AppendStringBuilder(sb, "DOComment", SoftBasic.ArrayFormat(DOComment));
        AppendStringBuilder(sb, "RIComment", SoftBasic.ArrayFormat(RIComment));
        AppendStringBuilder(sb, "ROComment", SoftBasic.ArrayFormat(ROComment));
        AppendStringBuilder(sb, "UIComment", SoftBasic.ArrayFormat(UIComment));
        AppendStringBuilder(sb, "UOComment", SoftBasic.ArrayFormat(UOComment));
        AppendStringBuilder(sb, "SIComment", SoftBasic.ArrayFormat(SIComment));
        AppendStringBuilder(sb, "SOComment", SoftBasic.ArrayFormat(SOComment));
        AppendStringBuilder(sb, "WIComment", SoftBasic.ArrayFormat(WIComment));
        AppendStringBuilder(sb, "WOComment", SoftBasic.ArrayFormat(WOComment));
        AppendStringBuilder(sb, "WSIComment", SoftBasic.ArrayFormat(WSIComment));
        AppendStringBuilder(sb, "AIComment", SoftBasic.ArrayFormat(AIComment));
        AppendStringBuilder(sb, "AOComment", SoftBasic.ArrayFormat(AOComment));
        AppendStringBuilder(sb, "GIComment", SoftBasic.ArrayFormat(GIComment));
        AppendStringBuilder(sb, "GOComment", SoftBasic.ArrayFormat(GOComment));
        AppendStringBuilder(sb, "STRREGComment", SoftBasic.ArrayFormat(STRREGComment));
        AppendStringBuilder(sb, "STRREG_COMMENT_Comment", SoftBasic.ArrayFormat(STRREG_COMMENT_Comment));

        return sb.toString();
    }

}
