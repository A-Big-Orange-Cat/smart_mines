package com.jkzz.smart_mines.communication.core.impl;

import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.core.Monitor;
import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.enumerate.impl.ControlModeEnum;
import com.jkzz.smart_mines.enumerate.impl.ParameterTypeEnum;
import com.jkzz.smart_mines.exception.AppException;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;

import java.util.HashSet;
import java.util.Set;

public class fuJingKouFengMen extends Monitor {

    private final Set<String> parameterOfCommand = new HashSet<>();

    @Override
    protected void reset() {
        parameterOfCommand.add("FC_CZ_OPEN");
        parameterOfCommand.add("FC_CZ_CLOSE");
        parameterOfCommand.add("FFM_FC_CZ_JDQD1");
        parameterOfCommand.add("FFM_FC_CZ_JDQD2");
        parameterOfCommand.add("FFM_FC_CZ_JDQD3");
        parameterOfCommand.add("FFM_FC_CZ_JDQD4");
    }

    @Override
    protected void remoteOrLocalControl(BaseDeviceTypeParameter parameter) throws AppException {
        if (parameterOfCommand.contains(parameter.getBaseDeviceTypeParameterCode())) return;
        if (ParameterTypeEnum.COMMAND.equals(parameter.getType())) {
            if (ControlModeEnum.LOCAL.equals(getControlMode())) {
                throw new AppException(AppExceptionCodeMsg.PLC_CONTROL_NOT_REMOTE);
            }
        }
    }

    @Override
    protected boolean writePLCBefore(JSONObject result, BaseDeviceTypeParameter parameter, String value, Integer userId) {
        return true;
    }

    @Override
    protected void writePLCAfter(BaseDeviceTypeParameter parameter, String value) {

    }

    @Override
    protected void writePLCAndLogAfter(BaseDeviceTypeParameter parameter, String writeValue, long timeMillis) {

    }

    @Override
    protected void readPLCAfter(JSONObject result, BaseDeviceTypeParameter parameter, String newValue, String oldValue) {

    }

    @Override
    protected void readPLCAndLogAfter(BaseDeviceTypeParameter parameter, String readValue, long timeMillis) {

    }

}
