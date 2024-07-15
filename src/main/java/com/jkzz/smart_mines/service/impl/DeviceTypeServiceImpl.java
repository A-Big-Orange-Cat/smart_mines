package com.jkzz.smart_mines.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.enumerate.impl.CommunicationProtocolEnum;
import com.jkzz.smart_mines.mapper.DeviceTypeMapper;
import com.jkzz.smart_mines.pojo.domain.DeviceType;
import com.jkzz.smart_mines.pojo.vo.DeviceTypeVO;
import com.jkzz.smart_mines.service.DeviceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【device_type】的数据库操作Service实现
 * @createDate 2024-07-01 10:10:55
 */
@Service
@RequiredArgsConstructor
public class DeviceTypeServiceImpl extends ServiceImpl<DeviceTypeMapper, DeviceType>
        implements DeviceTypeService {

    private final DeviceTypeMapper deviceTypeMapper;

    @Override
    public List<DeviceType> queryAll() {
        return selectListByDeviceTypeNameAndCommunicationProtocol(null, null);
    }

    @Override
    public List<DeviceTypeVO> queryDeviceTypeAndEnabledDeviceCount() {
        return deviceTypeMapper.selectDeviceTypeAndEnabledDeviceCount();
    }

    @Override
    public List<DeviceType> queryByDeviceTypeNameOrCommunicationProtocol(String deviceTypeName, CommunicationProtocolEnum communicationProtocol) {
        return selectListByDeviceTypeNameAndCommunicationProtocol(deviceTypeName, communicationProtocol);
    }

    /**
     * 根据设备类型名称和通讯协议查询设备类型
     *
     * @param deviceTypeName        设备类型名称
     * @param communicationProtocol 通讯协议
     * @return 设备类型列表
     */
    private List<DeviceType> selectListByDeviceTypeNameAndCommunicationProtocol(String deviceTypeName, CommunicationProtocolEnum communicationProtocol) {
        LambdaQueryWrapper<DeviceType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(deviceTypeName)) {
            lambdaQueryWrapper.like(DeviceType::getDeviceTypeName, deviceTypeName);
        }
        if (null != communicationProtocol) {
            lambdaQueryWrapper.eq(DeviceType::getCommunicationProtocol, communicationProtocol);
        }
        return deviceTypeMapper.selectList(lambdaQueryWrapper);
    }

}




