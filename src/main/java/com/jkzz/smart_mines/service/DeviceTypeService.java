package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.enumerate.impl.CommunicationProtocolEnum;
import com.jkzz.smart_mines.pojo.domain.DeviceType;
import com.jkzz.smart_mines.pojo.vo.DeviceTypeVO;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【device_type】的数据库操作Service
 * @createDate 2024-07-01 10:10:55
 */
public interface DeviceTypeService extends IService<DeviceType> {

    /**
     * 查询设备类型
     *
     * @return 设备类型集合
     */
    List<DeviceType> queryAll();

    /**
     * 查询设备类型和该类型非禁用设备数量
     *
     * @return 所有设备类型和该类型非禁用设备数量
     */
    List<DeviceTypeVO> queryDeviceTypeAndEnabledDeviceCount();

    /**
     * 查询设备类型
     *
     * @param deviceTypeName        设备类型名称
     * @param communicationProtocol 通讯协议
     * @return 设备类型集合
     */
    List<DeviceType> queryByDeviceTypeNameOrCommunicationProtocol(String deviceTypeName, CommunicationProtocolEnum communicationProtocol);

}
