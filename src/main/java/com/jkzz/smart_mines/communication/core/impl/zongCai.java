package com.jkzz.smart_mines.communication.core.impl;

import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.core.Monitor;
import com.jkzz.smart_mines.enumerate.impl.ControlModeEnum;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;

public class zongCai extends Monitor {

    @Override
    protected void reset() {
        super.setControlMode(ControlModeEnum.REMOTE);

        // 粉尘浓度记录数据库
        super.getParameterLogOfSignal().add("ZC_XH_FCND1");
        super.getParameterLogOfSignal().add("ZC_XH_FCND2");
        super.getParameterLogOfSignal().add("ZC_XH_FCND3");
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
