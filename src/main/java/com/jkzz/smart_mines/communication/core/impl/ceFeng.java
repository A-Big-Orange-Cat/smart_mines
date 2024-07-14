package com.jkzz.smart_mines.communication.core.impl;


import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.core.Monitor;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;

public class ceFeng extends Monitor {

    @Override
    protected void reset() {

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
