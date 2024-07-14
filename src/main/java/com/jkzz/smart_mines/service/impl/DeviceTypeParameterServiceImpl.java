package com.jkzz.smart_mines.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.mapper.DeviceTypeParameterMapper;
import com.jkzz.smart_mines.pojo.domain.DeviceTypeParameter;
import com.jkzz.smart_mines.service.DeviceTypeParameterService;
import com.jkzz.smart_mines.utils.VUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【device_type_parameter】的数据库操作Service实现
 * @createDate 2024-07-01 10:10:55
 */
@Service
public class DeviceTypeParameterServiceImpl extends ServiceImpl<DeviceTypeParameterMapper, DeviceTypeParameter>
        implements DeviceTypeParameterService {

    @Autowired
    private DeviceTypeParameterMapper deviceTypeParameterMapper;

    @Override
    public List<DeviceTypeParameter> queryByDeviceTypeId(Integer deviceTypeId) {
        LambdaQueryWrapper<DeviceTypeParameter> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DeviceTypeParameter::getDeviceTypeId, deviceTypeId)
                .orderBy(true, true, DeviceTypeParameter::getSortNumber);
        return deviceTypeParameterMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public Map<String, DeviceTypeParameter> queryMapByDeviceTypeId(Integer deviceTypeId) {
        return queryByDeviceTypeId(deviceTypeId).stream()
                .collect(Collectors.toMap(DeviceTypeParameter::getDeviceTypeParameterCode, deviceTypeParameter -> deviceTypeParameter));
    }

    @Override
    public DeviceTypeParameter queryByDeviceTypeIdAndCode(Integer deviceTypeId, String deviceTypeParameterCode) {
        LambdaQueryWrapper<DeviceTypeParameter> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DeviceTypeParameter::getDeviceTypeId, deviceTypeId)
                .eq(DeviceTypeParameter::getDeviceTypeParameterCode, deviceTypeParameterCode);
        return deviceTypeParameterMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public void updateValue(CommunicationManager communicationManager, DeviceTypeParameter deviceTypeParameter) {
        LambdaUpdateWrapper<DeviceTypeParameter> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(DeviceTypeParameter::getDeviceTypeParameterId, deviceTypeParameter.getDeviceTypeParameterId())
                .set(DeviceTypeParameter::getValue, deviceTypeParameter.getValue());
        VUtil.isTrue(!this.update(lambdaUpdateWrapper))
                .throwAppException(AppExceptionCodeMsg.FAILURE_UPDATE);
        Optional.ofNullable(communicationManager.getMonitorManagerMap().get(deviceTypeParameter.getDeviceTypeId().toString()))
                .ifPresent(monitorManager -> monitorManager.setDeviceTypeParameters(queryMapByDeviceTypeId(deviceTypeParameter.getDeviceTypeId())));
    }

}




