package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import com.jkzz.smart_mines.pojo.domain.Device;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【device】的数据库操作Service
 * @createDate 2024-07-01 10:10:55
 */
public interface DeviceService extends IService<Device> {

    /**
     * 新增设备
     *
     * @param device 设备
     */
    void insert(Device device);

    /**
     * 删除设备
     *
     * @param deviceId 设备id
     */
    void delete(Integer deviceId);

    /**
     * 更新设备
     *
     * @param device 设备
     * @return 更新后的设备
     */
    Device update(Device device);

    /**
     * 更新设备位置
     *
     * @param deviceId 设备id
     * @param location 设备位置
     */
    void updateLocation(Integer deviceId, String location);

    /**
     * 根据ip查询设备
     *
     * @param ip ip地址
     * @return 设备
     */
    Device getDeviceByIp(String ip);

    /**
     * 查询设备
     *
     * @param deviceTypeId 设备类型id
     * @param deviceName   设备名称
     * @return 设备集合
     */
    List<Device> queryByDeviceTypeIdOrDeviceName(Integer deviceTypeId, String deviceName);

    /**
     * 查询非禁用设备
     *
     * @param deviceTypeId 设备类型id
     * @param deviceName   设备名称
     * @return 设备集合
     */
    List<Device> queryNormalByDeviceTypeIdOrDeviceName(Integer deviceTypeId, String deviceName);

    /**
     * 创建监视器
     *
     * @param communicationManager 通讯管理者
     * @param device               设备
     */
    void createMonitor(CommunicationManager communicationManager, Device device);

    /**
     * 删除监视器
     *
     * @param communicationManager 通讯管理者
     * @param deviceId             设备id
     */
    void deleteMonitor(CommunicationManager communicationManager, Integer deviceId);

    /**
     * 更新监视器
     *
     * @param communicationManager 通讯管理者
     * @param device               设备
     */
    void updateMonitor(CommunicationManager communicationManager, Device device);

}
