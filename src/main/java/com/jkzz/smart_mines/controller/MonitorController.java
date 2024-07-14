package com.jkzz.smart_mines.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import com.jkzz.smart_mines.pojo.param.PlcParam;
import com.jkzz.smart_mines.response.Resp;
import com.jkzz.smart_mines.service.MonitorService;
import com.jkzz.smart_mines.verification.group.PlcWriteGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PLC通讯控制器
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    CommunicationManager communicationManager;

    @Autowired
    private MonitorService monitorService;

    /**
     * 按设备类型id和设备id查询参数设置的值
     *
     * @param plcParam 设备类型id,设备id
     * @return 结果数据
     */
    @PostMapping("/queryParaSettingValues")
    public Resp<JSONObject> queryParaSettingValuesByDeviceTypeIdAndDeviceId(@Validated @RequestBody PlcParam plcParam) {
        return Resp.success(monitorService.readParametersOfParaSetting(communicationManager, plcParam));
    }

    /**
     * 更新设备的参数的值
     *
     * @param plcParam 设备类型id,设备id,要更新的设备参数的信息列表
     * @return 结果数据
     */
    @PostMapping("/updateParameterValues")
    @SaCheckRole("admin")
    public Resp<JSONObject> updateParaSettingValuesByDeviceTypeIdAndDeviceId(@Validated(value = PlcWriteGroup.class) @RequestBody PlcParam plcParam) {
        return Resp.success(monitorService.updateParameters(communicationManager, plcParam));
    }

    /**
     * 更改 远程/就地 控制方式
     *
     * @param plcParam 设备类型id,设备id,远程/就地控制方式
     * @return 结果数据
     */
    @PostMapping("/remoteOrLocal")
    @SaCheckRole("admin")
    public Resp<Object> remoteOrLocalControl(@Validated(value = PlcWriteGroup.class) @RequestBody PlcParam plcParam) {
        return Resp.success(monitorService.remoteOrLocalControl(communicationManager, plcParam));
    }

}
