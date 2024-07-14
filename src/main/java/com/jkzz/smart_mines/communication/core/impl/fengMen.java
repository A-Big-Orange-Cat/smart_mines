package com.jkzz.smart_mines.communication.core.impl;

import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.Result.WriteResult;
import com.jkzz.smart_mines.communication.core.Monitor;
import com.jkzz.smart_mines.enumerate.impl.ControlModeEnum;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import com.jkzz.smart_mines.pojo.domain.LogOperation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class fengMen extends Monitor {

    /**
     * 操作指令
     */
    private final Set<String> parameterOfCommand = new HashSet<>();
    /**
     * 风门开关记录
     */
    private final JSONObject switchLog = new JSONObject();
    /**
     * A风门开关记录
     */
    private final List<LogOperation> aSwitchLog = new ArrayList<>(6);
    /**
     * B风门开关记录
     */
    private final List<LogOperation> bSwitchLog = new ArrayList<>(6);

    @Override
    protected void reset() {
        switchLog.put("aSwitchLog", aSwitchLog);
        switchLog.put("bSwitchLog", bSwitchLog);
        // 风门开关记录
        super.getResultMap().put("switchLog", switchLog);

        // 风门开闭状态信号参数保存数据库
        super.getParameterLogOfOperation().put("FM_XH_KB1", true);
        super.getParameterLogOfOperation().put("FM_XH_KB2", true);

        // 控制两道风门开闭的指令参数
        parameterOfCommand.add("FM_CZ_OPEN1");
        parameterOfCommand.add("FM_CZ_CLOSE1");
        parameterOfCommand.add("FM_CZ_OPEN2");
        parameterOfCommand.add("FM_CZ_CLOSE2");
    }

    @Override
    protected boolean writePLCBefore(JSONObject result, BaseDeviceTypeParameter parameter, String value, Integer userId) {
        // 如果是开启风门
        if ("FM_CZ_OPEN1".equals(parameter.getBaseDeviceTypeParameterCode()) || "FM_CZ_OPEN2".equals(parameter.getBaseDeviceTypeParameterCode())) {
            // 获取“允许2道风门同时打开”参数的值
            String a = (String) super.getParameterValueOfParaSettingMap().get("FM_CS_TK");
            // 如果参数值不为空且允许返回true
            if ("true".equals(a)) {
                return true;
            } else {
                // 获取两道风门的开闭状态
                String b = (String) super.getParameterValueOfSignalMap().get("FM_XH_KB1");
                String c = (String) super.getParameterValueOfSignalMap().get("FM_XH_KB2");
                // 两道风门的开闭状态都不为空且都是关闭状态返回true
                if ("true".equals(b) & "true".equals(c)) {
                    return true;
                } else {
                    // 返回false
                    result.put(parameter.getBaseDeviceTypeParameterCode(), new WriteResult(false, parameter.getBaseDeviceTypeParameterName(), value, "两道风门不允许同时打开！"));
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void writePLCAfter(BaseDeviceTypeParameter parameter, String value) {
        // 如果是控制两道风门开闭的指令参数
        if (parameterOfCommand.contains(parameter.getBaseDeviceTypeParameterCode())) {
            // 如果是一道风门开闭
            if ("FM_CZ_OPEN1".equals(parameter.getBaseDeviceTypeParameterCode()) || "FM_CZ_CLOSE1".equals(parameter.getBaseDeviceTypeParameterCode())) {
                // 将一道风门开闭状态信号的标记改为false
                super.getParameterLogOfOperation().replace("FM_XH_KB1", false);
            }
            // 如果是二道风门开闭
            if ("FM_CZ_OPEN2".equals(parameter.getBaseDeviceTypeParameterCode()) || "FM_CZ_CLOSE2".equals(parameter.getBaseDeviceTypeParameterCode())) {
                // 将二道风门开闭状态信号的标记改为false
                super.getParameterLogOfOperation().replace("FM_XH_KB2", false);
            }
        }
    }

    @Override
    protected void writePLCAndLogAfter(BaseDeviceTypeParameter parameter, String writeValue, long timeMillis) {
        // 如果是控制两道风门开闭的指令参数，添加6条风门开关记录
        if (parameterOfCommand.contains(parameter.getBaseDeviceTypeParameterCode())) {
            LogOperation logOperation = LogOperation.builder()
                    .operationValue(String.valueOf(true))
                    .controlMode(ControlModeEnum.REMOTE)
                    .timeMillis(timeMillis)
                    .build();
            if ("FM_CZ_OPEN1".equals(parameter.getBaseDeviceTypeParameterCode()) || "FM_CZ_CLOSE1".equals(parameter.getBaseDeviceTypeParameterCode())) {
                if ("FM_CZ_OPEN1".equals(parameter.getBaseDeviceTypeParameterCode())) {
                    logOperation.setOperationValue(String.valueOf(false));
                }
                if (aSwitchLog.size() == 6) {
                    aSwitchLog.remove(0);
                }
                aSwitchLog.add(logOperation);
            }
            if ("FM_CZ_OPEN2".equals(parameter.getBaseDeviceTypeParameterCode()) || "FM_CZ_CLOSE2".equals(parameter.getBaseDeviceTypeParameterCode())) {
                if ("FM_CZ_OPEN2".equals(parameter.getBaseDeviceTypeParameterCode())) {
                    logOperation.setOperationValue(String.valueOf(false));
                }
                if (bSwitchLog.size() == 6) {
                    bSwitchLog.remove(0);
                }
                bSwitchLog.add(logOperation);
            }
        }
    }

    @Override
    protected void readPLCAfter(JSONObject result, BaseDeviceTypeParameter parameter, String newValue, String oldValue) {
    }

    @Override
    protected void readPLCAndLogAfter(BaseDeviceTypeParameter parameter, String readValue, long timeMillis) {
        // 如果是两道风门开闭状态信号的参数，添加6条风门开关记录
        if ("FM_XH_KB1".equals(parameter.getBaseDeviceTypeParameterCode())) {
            LogOperation logOperation = LogOperation.builder()
                    .operationValue(readValue)
                    .controlMode(ControlModeEnum.LOCAL)
                    .timeMillis(timeMillis)
                    .build();
            if (aSwitchLog.size() == 6) {
                aSwitchLog.remove(0);
            }
            aSwitchLog.add(logOperation);
        }
        if ("FM_XH_KB2".equals(parameter.getBaseDeviceTypeParameterCode())) {
            LogOperation logOperation = LogOperation.builder()
                    .operationValue(readValue)
                    .controlMode(ControlModeEnum.LOCAL)
                    .timeMillis(timeMillis)
                    .build();
            if (bSwitchLog.size() == 6) {
                bSwitchLog.remove(0);
            }
            bSwitchLog.add(logOperation);
        }
    }

}
