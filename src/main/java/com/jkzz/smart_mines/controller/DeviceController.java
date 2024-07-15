package com.jkzz.smart_mines.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import com.jkzz.smart_mines.pojo.domain.Device;
import com.jkzz.smart_mines.pojo.param.DeviceParam;
import com.jkzz.smart_mines.response.Resp;
import com.jkzz.smart_mines.service.DeviceDvrService;
import com.jkzz.smart_mines.service.DeviceService;
import com.jkzz.smart_mines.verification.group.DeleteGroup;
import com.jkzz.smart_mines.verification.group.InsertGroup;
import com.jkzz.smart_mines.verification.group.UpdateGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device")
public class DeviceController {

    private final CommunicationManager communicationManager;

    private final DeviceService deviceService;

    private final DeviceDvrService deviceDvrService;

    /**
     * 添加设备
     *
     * @param device 设备信息
     * @return 结果数据
     */
    @PostMapping("/insert")
    @SaCheckRole("admin")
    public Resp<Object> insert(@Validated(value = InsertGroup.class) @RequestBody Device device) {
        deviceService.insert(device);
        deviceService.createMonitor(communicationManager, device);
        return Resp.success();
    }

    /**
     * 删除设备
     *
     * @param deviceParam 设备id
     * @return 结果数据
     */
    @PostMapping("/delete")
    @SaCheckRole("admin")
    public Resp<Object> delete(@Validated(value = DeleteGroup.class) @RequestBody DeviceParam deviceParam) {
        deviceService.deleteMonitor(communicationManager, deviceParam.getDeviceId());
        deviceDvrService.deleteByDeviceId(deviceParam.getDeviceId());
        deviceService.delete(deviceParam.getDeviceId());
        return Resp.success();
    }

    /**
     * 更新设备信息
     *
     * @param device 要更新的设备信息
     * @return 结果数据
     */
    @PostMapping("/update")
    @SaCheckRole("admin")
    public Resp<Object> update(@Validated(value = UpdateGroup.class) @RequestBody Device device) {
        deviceService.updateMonitor(communicationManager, deviceService.update(device));
        return Resp.success();
    }

    /**
     * 更新设备位置信息
     *
     * @param deviceParam 设备id和位置信息
     * @return 结果数据
     */
    @PostMapping("/updateLocation")
    @SaCheckRole("admin")
    public Resp<Object> updateLocation(@Validated(value = UpdateGroup.class) @RequestBody DeviceParam deviceParam) {
        deviceService.updateLocation(deviceParam.getDeviceId(), deviceParam.getDeviceLocation());
        return Resp.success();
    }

    /**
     * 按设备名称或设备类型id查询设备
     *
     * @param deviceParam 设备名称和设备类型id信息
     * @return 结果数据
     */
    @PostMapping("/queryByNameOrTypeId")
    public Resp<List<Device>> queryByDeviceTypeIdOrDeviceName(@RequestBody DeviceParam deviceParam) {
        List<Device> devices = deviceService.queryByDeviceTypeIdOrDeviceName(deviceParam.getDeviceTypeId(), deviceParam.getDeviceName());
        return Resp.success(devices);
    }

    /**
     * 按设备类型id查询非禁用设备
     *
     * @param deviceParam 设备类型id
     * @return 结果数据
     */
    @PostMapping("/queryNormalByNameOrTypeId")
    public Resp<List<Device>> queryNormalByDeviceTypeIdOrDeviceName(@RequestBody DeviceParam deviceParam) {
        List<Device> devices = deviceService.queryNormalByDeviceTypeIdOrDeviceName(deviceParam.getDeviceTypeId(), deviceParam.getDeviceName());
        return Resp.success(devices);
    }

}
