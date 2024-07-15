package com.jkzz.smart_mines.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import com.jkzz.smart_mines.pojo.domain.DeviceType;
import com.jkzz.smart_mines.pojo.domain.DeviceTypeParameter;
import com.jkzz.smart_mines.pojo.vo.DeviceTypeVO;
import com.jkzz.smart_mines.response.Resp;
import com.jkzz.smart_mines.service.DeviceTypeParameterService;
import com.jkzz.smart_mines.service.DeviceTypeService;
import com.jkzz.smart_mines.verification.group.SelectGroup;
import com.jkzz.smart_mines.verification.group.UpdateGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备类型控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/deviceType")
public class DeviceTypeController {

    private final CommunicationManager communicationManager;

    private final DeviceTypeService deviceTypeService;

    private final DeviceTypeParameterService deviceTypeParameterService;

    /**
     * 查询所有设备类型
     *
     * @return 结果数据
     */
    @GetMapping("/queryAll")
    public Resp<List<DeviceType>> queryAll() {
        List<DeviceType> deviceTypes = deviceTypeService.queryAll();
        return Resp.success(deviceTypes);
    }

    /**
     * 查询所有设备类型和该设备类型的启用设备数量
     *
     * @return 结果数据
     */
    @GetMapping("/queryDeviceTypeAndEnabledDeviceCount")
    public Resp<List<DeviceTypeVO>> queryDeviceTypeAndEnabledDeviceCount() {
        List<DeviceTypeVO> deviceTypeVOs = deviceTypeService.queryDeviceTypeAndEnabledDeviceCount();
        return Resp.success(deviceTypeVOs);
    }

    /**
     * 按设备类型名称和通讯协议查询设备类型
     *
     * @param deviceType 设备类型名称和通讯协议
     * @return 结果数据
     */
    @PostMapping("/queryByNameOrProtocol")
    public Resp<List<DeviceType>> queryByDeviceTypeNameOrCommunicationProtocol(@RequestBody DeviceType deviceType) {
        List<DeviceType> deviceTypes = deviceTypeService.queryByDeviceTypeNameOrCommunicationProtocol(deviceType.getDeviceTypeName(), deviceType.getCommunicationProtocol());
        return Resp.success(deviceTypes);
    }

    /**
     * 按设备类型id更新设备类型参数的值
     *
     * @param deviceTypeParameter 设备类型参数
     * @return 结果数据
     */
    @PostMapping("/updatePByTypeId")
    @SaCheckRole("admin")
    public Resp<Object> updateParameterByDeviceTypeId(@Validated(value = UpdateGroup.class) @RequestBody DeviceTypeParameter deviceTypeParameter) {
        deviceTypeParameterService.updateValue(communicationManager, deviceTypeParameter);
        return Resp.success();
    }

    /**
     * 按设备类型id查询设备类型参数
     *
     * @param deviceTypeParameter 设备类型id
     * @return 结果数据
     */
    @PostMapping("/queryPByTypeId")
    public Resp<List<DeviceTypeParameter>> queryParameterByDeviceTypeId(@Validated(value = SelectGroup.class) @RequestBody DeviceTypeParameter deviceTypeParameter) {
        List<DeviceTypeParameter> parameters = deviceTypeParameterService.queryByDeviceTypeId(deviceTypeParameter.getDeviceTypeId());
        return Resp.success(parameters);
    }

}

