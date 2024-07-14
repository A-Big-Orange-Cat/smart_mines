package com.jkzz.smart_mines.communication.core.impl;

import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.core.Monitor;
import com.jkzz.smart_mines.enumerate.impl.ControlModeEnum;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import com.jkzz.smart_mines.pojo.domain.LogOperation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class fangHuoMen extends Monitor {

    /**
     * 操作指令
     */
    private final Set<String> parameterOfCommand = new HashSet<>();

    /**
     * 防火门开关记录
     */
    private final List<LogOperation> switchLog = new ArrayList<>(6);

    @Override
    protected void reset() {
        // 防火门开关记录
        super.getResultMap().put("switchLog", switchLog);

        // 控制防火门开闭的指令参数
        parameterOfCommand.add("FHM_CZ_OPEN");
        parameterOfCommand.add("FHM_CZ_CLOSE");
    }

    @Override
    protected boolean writePLCBefore(JSONObject result, BaseDeviceTypeParameter parameter, String value, Integer userId) {
        return true;
    }

    @Override
    protected void writePLCAfter(BaseDeviceTypeParameter parameter, String value) {
        if (parameterOfCommand.contains(parameter.getBaseDeviceTypeParameterCode())) {
            // 将防火门开闭状态信号的标记改为false
            super.getParameterLogOfOperation().replace("FHM_XH_KB", false);
        }
    }

    @Override
    protected void writePLCAndLogAfter(BaseDeviceTypeParameter parameter, String writeValue, long timeMillis) {
        // 如果是控制防火门开闭的指令参数，添加6条风门开关记录
        if (parameterOfCommand.contains(parameter.getBaseDeviceTypeParameterCode())) {
            LogOperation logOperation = LogOperation.builder()
                    .operationValue(String.valueOf(true))
                    .controlMode(ControlModeEnum.REMOTE)
                    .timeMillis(timeMillis)
                    .build();
            if ("FHM_CZ_OPEN".equals(parameter.getBaseDeviceTypeParameterCode())) {
                logOperation.setOperationValue(String.valueOf(false));
            }
            if (switchLog.size() == 6) {
                switchLog.remove(0);
            }
            switchLog.add(logOperation);
        }
    }

    @Override
    protected void readPLCAfter(JSONObject result, BaseDeviceTypeParameter parameter, String newValue, String oldValue) {

    }

    @Override
    protected void readPLCAndLogAfter(BaseDeviceTypeParameter parameter, String readValue, long timeMillis) {
        // 如果是防火门开闭状态信号的参数，添加6条风门开关记录
        if ("FHM_XH_KB".equals(parameter.getBaseDeviceTypeParameterCode())) {
            LogOperation logOperation = LogOperation.builder()
                    .operationValue(readValue)
                    .controlMode(ControlModeEnum.LOCAL)
                    .timeMillis(timeMillis)
                    .build();
            if (switchLog.size() == 6) {
                switchLog.remove(0);
            }
            switchLog.add(logOperation);
        }
    }

}
