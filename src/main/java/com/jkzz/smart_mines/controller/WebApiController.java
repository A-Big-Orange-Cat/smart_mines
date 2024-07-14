package com.jkzz.smart_mines.controller;

import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import com.jkzz.smart_mines.pojo.domain.Device;
import com.jkzz.smart_mines.pojo.param.PlcParam;
import com.jkzz.smart_mines.response.Resp;
import com.jkzz.smart_mines.service.BaseDeviceTypeParameterService;
import com.jkzz.smart_mines.service.DeviceService;
import com.jkzz.smart_mines.service.DeviceTypeRelationService;
import com.jkzz.smart_mines.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/webApi")
public class WebApiController {

    @Autowired
    private CommunicationManager communicationManager;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceTypeRelationService deviceTypeRelationService;
    @Autowired
    private BaseDeviceTypeParameterService baseDeviceTypeParameterService;
    @Autowired
    private MonitorService monitorService;

    /**
     * 打开设备详情页
     *
     * @param response 响应
     * @param ip       设备ip
     */
    @GetMapping("/viewDevice")
    public void getDevice(HttpServletResponse response, String ip) {
        Device device = deviceService.getDeviceByIp(ip);
        try {
            // 要打开的网页URL
            String url = String.format("http://localhost:8080/#/layout/device-detail/%s/%s/%s",
                    URLEncoder.encode(device.getDeviceName(), "UTF-8"), device.getDeviceId(), device.getDeviceTypeId());
            response.setCharacterEncoding("UTF-8");
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取设备信息代码
     *
     * @param ip 设备ip
     * @return 结果数据
     */
    @GetMapping("/getDeviceCode")
    public Resp<JSONObject> getDeviceCode(String ip) {
        Device device = deviceService.getDeviceByIp(ip);
        List<Integer> baseDeviceTypeIds = deviceTypeRelationService.queryBaseDeviceTypeIdsByDeviceTypeId(device.getDeviceTypeId());
        Map<String, String> parameters = baseDeviceTypeParameterService.queryByBaseDeviceTypeId(baseDeviceTypeIds)
                .stream()
                .collect(Collectors.toMap(BaseDeviceTypeParameter::getBaseDeviceTypeParameterCode, BaseDeviceTypeParameter::getBaseDeviceTypeParameterName));
        JSONObject result = new JSONObject(parameters);
        return Resp.success(result);
    }

    /**
     * 获取所有设备信息
     *
     * @return 结果数据
     */
    @GetMapping("/getAllDevice")
    public Resp<JSONObject> getAllDevice() {
        JSONObject result = new JSONObject();
        List<Device> devices = deviceService.queryNormalByDeviceTypeIdOrDeviceName(null, null);
        devices.forEach(device -> result.put(device.getIp(), getJSONObject(device)));
        return Resp.success(result);
    }

    /**
     * 获取设备信息
     *
     * @param ip 设备ip
     * @return 结果数据
     */
    @GetMapping("/getDevice")
    public Resp<JSONObject> getDevice(String ip) {
        JSONObject result = new JSONObject();
        result.put(ip, getJSONObject(deviceService.getDeviceByIp(ip)));
        return Resp.success(result);
    }

    /**
     * 更改设备的远程就地控制状态
     *
     * @param ip    设备ip
     * @param code  参数代码
     * @param value 写入值
     * @return 结果数据
     */
    @GetMapping("/setDeviceCM")
    public Resp<JSONObject> setDeviceCM(String ip, String code, String value) {
        PlcParam plcParam = getPlcParam(ip, code, value);
        return Resp.success(monitorService.remoteOrLocalControl(communicationManager, plcParam));
    }

    /**
     * 更改设备参数值
     *
     * @param ip    设备ip
     * @param code  参数代码
     * @param value 写入值
     * @return 结果数据
     */
    @GetMapping("/setDevice")
    public Resp<JSONObject> setDevice(String ip, String code, String value) {
        PlcParam plcParam = getPlcParam(ip, code, value);
        return Resp.success(monitorService.updateParameters(communicationManager, plcParam));
    }

    private JSONObject getJSONObject(Device device) {
        JSONObject result = new JSONObject();
        Optional.ofNullable(communicationManager.getMonitorManagerMap().get(device.getDeviceTypeId().toString()))
                .map(monitorManager -> monitorManager.getMonitorMap().get(device.getDeviceId().toString()))
                .ifPresent(monitor -> {
                    result.put("connStatus", monitor.isConnStatus());
                    result.put("data", new JSONObject() {{
                        putAll(monitor.getParameterValueOfSignalMap());
                        putAll(monitor.getParameterValueOfParaSettingMap());
                    }});
                    result.put("alarm", monitor.getParameterValueOfAlarmMap());
                });
        return result;
    }

    private PlcParam getPlcParam(String ip, String code, String value) {
        Device device = deviceService.getDeviceByIp(ip);
        return PlcParam.builder()
                .deviceTypeId(device.getDeviceTypeId())
                .deviceId(device.getDeviceId())
                .codesAndValues(new HashMap<String, String>() {{
                    put(code, value);
                }})
                .build();
    }

}
