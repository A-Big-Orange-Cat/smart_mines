package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import com.jkzz.smart_mines.pojo.domain.DeviceTypeParameter;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【device_type_parameter】的数据库操作Service
 * @createDate 2024-07-01 10:10:55
 */
public interface DeviceTypeParameterService extends IService<DeviceTypeParameter> {

    /**
     * 查询设备类型参数
     *
     * @param deviceTypeId 设备类型id
     * @return 设备类型参数集合
     */
    List<DeviceTypeParameter> queryByDeviceTypeId(Integer deviceTypeId);

    /**
     * 查询设备类型参数
     *
     * @param deviceTypeId 设备类型id
     * @return 设备类型参数集合
     */
    Map<String, DeviceTypeParameter> queryMapByDeviceTypeId(Integer deviceTypeId);

    /**
     * 查询设备类型参数
     *
     * @param deviceTypeId            设备类型id
     * @param deviceTypeParameterCode 设备类型参数代码
     * @return 设备类型参数
     */
    DeviceTypeParameter queryByDeviceTypeIdAndCode(Integer deviceTypeId, String deviceTypeParameterCode);

    /**
     * 更新设备类型参数值
     *
     * @param deviceTypeParameter 设备类型参数
     */
    void updateValue(CommunicationManager communicationManager, DeviceTypeParameter deviceTypeParameter);

}
