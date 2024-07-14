package com.jkzz.smart_mines.communication.core.impl;

import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.enumerate.impl.ControlModeEnum;
import com.jkzz.smart_mines.enumerate.impl.ParameterTypeEnum;
import com.jkzz.smart_mines.exception.AppException;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;

import java.util.HashSet;
import java.util.Set;

public class fengMenBaiYe extends fengMen {

    private final Set<String> parameterOfCommand = new HashSet<>();

    public fengMenBaiYe() {
        parameterOfCommand.add("FC_CZ_OPEN1");
        parameterOfCommand.add("FC_CZ_CLOSE1");
        parameterOfCommand.add("FC_CZ_OPEN2");
        parameterOfCommand.add("FC_CZ_CLOSE2");
        parameterOfCommand.add("FC_CZ_OPEN3");
        parameterOfCommand.add("FC_CZ_CLOSE3");
        parameterOfCommand.add("FC_CZ_OPEN4");
        parameterOfCommand.add("FC_CZ_CLOSE4");
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

}
