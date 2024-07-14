package com.jkzz.smart_mines.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.Result.WriteResult;
import com.jkzz.smart_mines.communication.core.Monitor;
import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.exception.AppException;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import com.jkzz.smart_mines.pojo.param.PlcParam;
import com.jkzz.smart_mines.service.BaseDeviceTypeParameterService;
import com.jkzz.smart_mines.service.DeviceTypeRelationService;
import com.jkzz.smart_mines.service.MonitorService;
import com.jkzz.smart_mines.utils.VUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    BaseDeviceTypeParameterService baseDeviceTypeParameterService;
    @Autowired
    private DeviceTypeRelationService deviceTypeRelationService;

    @Override
    public JSONObject readParametersOfParaSetting(CommunicationManager communicationManager, PlcParam plcParam) {
        return getMonitor(communicationManager, plcParam).getParameterValueOfParaSettingMap();
    }

    @Override
    public JSONObject updateParameters(CommunicationManager communicationManager, PlcParam plcParam) {
        Monitor monitor = getMonitor(communicationManager, plcParam);
        return updateParameters(plcParam, monitor::writeDeviceParameter);
    }

    @Override
    public JSONObject remoteOrLocalControl(CommunicationManager communicationManager, PlcParam plcParam) {
        Monitor monitor = getMonitor(communicationManager, plcParam);
        return updateParameters(plcParam, monitor::remoteOrLocalControl);
    }

    private JSONObject updateParameters(PlcParam plcParam, Function function) {
        JSONObject result = new JSONObject();
        plcParam.getCodesAndValues().forEach((code, value) ->
                VUtil.handler(checkoutValue(result, code, value)).handler(() ->
                        Optional.ofNullable(getBaseDeviceTypeParameter(plcParam, code)).ifPresent(parameter ->
                                function.execute(result, parameter, value, Integer.valueOf(StpUtil.getLoginId().toString()))
                        )
                )
        );
        return result;
    }

    private Monitor getMonitor(CommunicationManager communicationManager, PlcParam plcParam) {
        Monitor monitor = Optional.ofNullable(communicationManager.getMonitorManagerMap().get(plcParam.getDeviceTypeId().toString()))
                .map(monitorManager -> monitorManager.getMonitorMap().get(plcParam.getDeviceId().toString()))
                .orElseThrow(() -> new AppException(AppExceptionCodeMsg.PLC_MONITOR_NOT_EXIST));
        VUtil.isTrue(!monitor.isConnStatus())
                .throwAppException(AppExceptionCodeMsg.PLC_OFF_LINE);
        return monitor;
    }

    private BaseDeviceTypeParameter getBaseDeviceTypeParameter(PlcParam plcParam, String code) {
        List<Integer> baseDeviceTypeIds = deviceTypeRelationService.queryBaseDeviceTypeIdsByDeviceTypeId(plcParam.getDeviceTypeId());
        return baseDeviceTypeParameterService.queryByBaseDeviceTypeIdAndCode(baseDeviceTypeIds, code);
    }

    /**
     * 校验写入值是否为布尔或数字，并初始化写入结果
     *
     * @param result 写入结果
     * @param code   参数代码
     * @param value  写入值
     */
    private boolean checkoutValue(JSONObject result, String code, String value) {
        if (!"true".equals(value) && !"false".equals(value)) {
            try {
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                result.put(code, new WriteResult(false, null, value, "参数值只能为数字"));
                return false;
            }
        }
        result.put(code, new WriteResult(false, null, value, "未定义参数代码"));
        return true;
    }

    interface Function {
        void execute(JSONObject result, BaseDeviceTypeParameter parameter, String value, Integer userId);
    }

}
