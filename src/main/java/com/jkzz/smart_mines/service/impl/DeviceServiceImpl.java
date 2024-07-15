package com.jkzz.smart_mines.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import com.jkzz.smart_mines.communication.manager.MonitorManager;
import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.enumerate.impl.EnableStatusEnum;
import com.jkzz.smart_mines.exception.AppException;
import com.jkzz.smart_mines.mapper.DeviceMapper;
import com.jkzz.smart_mines.pojo.domain.Device;
import com.jkzz.smart_mines.service.DeviceService;
import com.jkzz.smart_mines.utils.VUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @description 针对表【device】的数据库操作Service实现
 * @createDate 2024-07-01 10:10:55
 */
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device>
        implements DeviceService {

    private final DeviceMapper deviceMapper;

    @Override
    public void insert(Device device) {
        VUtil.isTrue(null != selectOneByIp(device.getIp(), device.getDeviceId()))
                .throwAppException(AppExceptionCodeMsg.IP_EXIST);
        VUtil.isTrue(1 != deviceMapper.insert(device))
                .throwAppException(AppExceptionCodeMsg.FAILURE_INSERT);
    }

    @Override
    public void delete(Integer deviceId) {
        VUtil.isTrue(1 != deviceMapper.deleteById(deviceId))
                .throwAppException(AppExceptionCodeMsg.FAILURE_DELETE);
    }

    @Override
    public Device update(Device device) {
        VUtil.isTrue(null != selectOneByIp(device.getIp(), device.getDeviceId()))
                .throwAppException(AppExceptionCodeMsg.IP_EXIST);
        VUtil.isTrue(1 != deviceMapper.updateById(device))
                .throwAppException(AppExceptionCodeMsg.FAILURE_UPDATE);
        return deviceMapper.selectById(device.getDeviceId());
    }

    @Override
    public void updateLocation(Integer deviceId, String location) {
        Device device = deviceMapper.selectById(deviceId);
        device.setDeviceLocation(location);
        VUtil.isTrue(!this.updateById(device))
                .throwAppException(AppExceptionCodeMsg.FAILURE_UPDATE);
    }

    @Override
    public Device getDeviceByIp(String ip) {
        return Optional.ofNullable(selectOneByIp(ip, null))
                .orElseThrow(() -> new AppException(AppExceptionCodeMsg.DEVICE_NOT_EXISTS));
    }

    @Override
    public List<Device> queryByDeviceTypeIdOrDeviceName(Integer deviceTypeId, String deviceName) {
        return selectListByDeviceTypeIdAndDeviceName(deviceTypeId, deviceName, false);
    }

    @Override
    public List<Device> queryNormalByDeviceTypeIdOrDeviceName(Integer deviceTypeId, String deviceName) {
        return selectListByDeviceTypeIdAndDeviceName(deviceTypeId, deviceName, true);
    }

    @Override
    public void createMonitor(CommunicationManager communicationManager, Device device) {
        try {
            MonitorManager monitorManager = communicationManager.getMonitorManagerMap().get(device.getDeviceTypeId().toString());
            monitorManager.createMonitor(device);
        } catch (Exception e) {
            log.error("新建Monitor失败，信息：" + device);
        }
    }

    @Override
    public void deleteMonitor(CommunicationManager communicationManager, Integer deviceId) {
        try {
            Integer deviceTypeId = deviceMapper.selectById(deviceId).getDeviceTypeId();
            MonitorManager monitorManager = communicationManager.getMonitorManagerMap().get(deviceTypeId.toString());
            monitorManager.deleteMonitor(deviceId);
        } catch (Exception e) {
            log.error("删除Monitor失败，信息：deviceId=" + deviceId);
        }
    }

    @Override
    public void updateMonitor(CommunicationManager communicationManager, Device device) {
        try {
            VUtil.isTrueOrFalse(EnableStatusEnum.DISABLE.equals(device.getEnableStatus())).trueOrFalseHandler(
                    () -> communicationManager.getMonitorManagerMap().get(device.getDeviceTypeId().toString()).deleteMonitor(device.getDeviceId()),
                    () -> communicationManager.getMonitorManagerMap().get(device.getDeviceTypeId().toString()).updateMonitor(device)
            );
        } catch (Exception e) {
            log.error("更新Monitor失败，信息：" + device);
        }
    }

    /**
     * 根据ip查询设备
     *
     * @param ip       ip地址
     * @param deviceId 排除的设备id
     * @return 设备
     */
    private Device selectOneByIp(String ip, Integer deviceId) {
        LambdaQueryWrapper<Device> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Device::getIp, ip);
        if (null != deviceId) {
            lambdaQueryWrapper.ne(Device::getDeviceId, deviceId);
        }
        return deviceMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 根据设备类型id或设备名称查询设备
     *
     * @param deviceTypeId     设备类型id
     * @param deviceName       设备名称
     * @param onlyEnableStatus 是否仅启用状态
     * @return 设备列表
     */
    private List<Device> selectListByDeviceTypeIdAndDeviceName(Integer deviceTypeId, String deviceName, boolean onlyEnableStatus) {
        LambdaQueryWrapper<Device> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderBy(true, true, Device::getSortNumber);
        if (null != deviceTypeId) {
            lambdaQueryWrapper.eq(Device::getDeviceTypeId, deviceTypeId);
        }
        if (StringUtils.hasText(deviceName)) {
            lambdaQueryWrapper.like(Device::getDeviceName, deviceName);
        }
        if (onlyEnableStatus) {
            lambdaQueryWrapper.eq(Device::getEnableStatus, EnableStatusEnum.ENABLE);
        }
        return deviceMapper.selectList(lambdaQueryWrapper);
    }

}




