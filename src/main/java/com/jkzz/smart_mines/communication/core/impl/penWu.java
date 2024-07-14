package com.jkzz.smart_mines.communication.core.impl;

import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.core.Monitor;
import com.jkzz.smart_mines.enumerate.impl.ControlModeEnum;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import com.jkzz.smart_mines.pojo.domain.LogOperation;

import java.util.*;

public class penWu extends Monitor {

    /**
     * 操作指令
     */
    private final Set<String> parameterOfCommand = new HashSet<>();

    /**
     * 操作记录
     */
    private final List<LogOperation> logOperations = new ArrayList<>(10);

    /**
     * 定时时间的地址只有在大巷定时的时候才读取
     */
    private final Map<String, BaseDeviceTypeParameter> parametersOfParaSetting = new HashMap<>();

    @Override
    protected void reset() {
        // 喷雾操作记录
        super.getResultMap().put("operationLogs", logOperations);

        // 实时粉尘记录数据库
        super.getParameterLogOfSignal().add("PW_XH_FCND");
        // 喷雾开闭状态信号参数保存数据库
        super.getParameterLogOfOperation().put("PW_XH_GZZT", true);

        // 定时时间的地址只有在大巷定时的时候才读取
        super.getMonitorManager().getParametersOfParaSetting().forEach((code, parameter) -> {
            if ("PW_CS_PWGNMS".equals(code) || "PW_CS_PWYS".equals(code) || "PW_CS_FCNDSX".equals(code)) {
                parametersOfParaSetting.put(code, parameter);
            }
        });

        // 控制喷雾洒水启动停止的指令参数
        parameterOfCommand.add("PW_CZ_YCKZPWSS");

    }

    @Override
    protected boolean writePLCBefore(JSONObject result, BaseDeviceTypeParameter parameter, String value, Integer userId) {
        return true;
    }

    @Override
    protected void writePLCAfter(BaseDeviceTypeParameter parameter, String value) {
        if (parameterOfCommand.contains(parameter.getBaseDeviceTypeParameterCode())) {
            super.getParameterLogOfOperation().replace("PW_XH_GZZT", false);
        }
    }

    @Override
    protected void writePLCAndLogAfter(BaseDeviceTypeParameter parameter, String writeValue, long timeMillis) {
        // 如果是操作指令，添加10条操作记录
        if (parameterOfCommand.contains(parameter.getBaseDeviceTypeParameterCode())) {
            if (logOperations.size() == 10) {
                logOperations.remove(0);
            }
            LogOperation logOperation = LogOperation.builder()
                    .operationValue(writeValue)
                    .controlMode(ControlModeEnum.REMOTE)
                    .timeMillis(timeMillis)
                    .build();
            logOperations.add(logOperation);
        }
    }

    @Override
    protected void readPLCAfter(JSONObject result, BaseDeviceTypeParameter parameter, String newValue, String oldValue) {

    }

    @Override
    protected void readPLCAndLogAfter(BaseDeviceTypeParameter parameter, String readValue, long timeMillis) {
        // 如果是喷雾工作状态信号的参数，添加10条喷雾工作状态记录
        if ("PW_XH_GZZT".equals(parameter.getBaseDeviceTypeParameterCode())) {
            if (logOperations.size() == 10) {
                logOperations.remove(0);
            }
            LogOperation logOperation = LogOperation.builder()
                    .operationValue(readValue)
                    .controlMode(ControlModeEnum.LOCAL)
                    .timeMillis(timeMillis)
                    .build();
            logOperations.add(logOperation);
        }
    }

    /**
     * 读取参数设置
     */
    @Override
    protected void readDeviceParametersOfParaSetting() {
        // 定时时间的地址只有在大巷定时的时候才读取
        if ("0".equals(super.getParameterValueOfSignalMap().get("PW_CS_PWGNMS"))) {
            readDeviceParameters(new ArrayList<>(super.getMonitorManager().getParametersOfParaSetting().values()),
                    super.getParameterValueOfParaSettingMap());
        } else {
            readDeviceParameters(new ArrayList<>(parametersOfParaSetting.values()),
                    super.getParameterValueOfParaSettingMap());
        }
    }

}
