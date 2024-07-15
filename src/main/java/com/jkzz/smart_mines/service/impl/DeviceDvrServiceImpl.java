package com.jkzz.smart_mines.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.hikvision.PreviewUntil;
import com.jkzz.smart_mines.mapper.DeviceDvrMapper;
import com.jkzz.smart_mines.pojo.domain.DeviceDvr;
import com.jkzz.smart_mines.service.DeviceDvrService;
import com.jkzz.smart_mines.utils.VUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【device_dvr】的数据库操作Service实现
 * @createDate 2024-07-01 10:10:55
 */
@Service
@RequiredArgsConstructor
public class DeviceDvrServiceImpl extends ServiceImpl<DeviceDvrMapper, DeviceDvr>
        implements DeviceDvrService {

    private final DeviceDvrMapper deviceDvrMapper;

    @Override
    public void insert(DeviceDvr dvr) {
        VUtil.isTrue(null != selectOneByDeviceIdAndDvrIp(dvr.getDeviceId(), dvr.getIp(), null))
                .throwAppException(AppExceptionCodeMsg.IP_EXIST);
        VUtil.isTrue(1 != deviceDvrMapper.insert(dvr))
                .throwAppException(AppExceptionCodeMsg.FAILURE_INSERT);
    }

    @Override
    public void delete(Integer dvrId) {
        VUtil.isTrue(1 != deviceDvrMapper.deleteById(dvrId))
                .throwAppException(AppExceptionCodeMsg.FAILURE_DELETE);
    }

    @Override
    public void deleteByDeviceId(Integer deviceId) {
        LambdaQueryWrapper<DeviceDvr> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DeviceDvr::getDeviceId, deviceId);
        deviceDvrMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public void update(DeviceDvr dvr) {
        VUtil.isTrue(null != selectOneByDeviceIdAndDvrIp(dvr.getDeviceId(), dvr.getIp(), dvr.getDvrId()))
                .throwAppException(AppExceptionCodeMsg.IP_EXIST);
        VUtil.isTrue(1 != deviceDvrMapper.updateById(dvr))
                .throwAppException(AppExceptionCodeMsg.FAILURE_UPDATE);
    }

    @Override
    public List<DeviceDvr> queryByDeviceId(Integer deviceId) {
        LambdaQueryWrapper<DeviceDvr> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DeviceDvr::getDeviceId, deviceId);
        return deviceDvrMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public void start(List<DeviceDvr> dvrs) {
        dvrs.forEach(PreviewUntil::loginAndPlayView);
        //threadPoolTaskExecutor.submit(() -> dvrs.forEach(dvr -> PreviewUntil.loginAndPicByPlay(dvr, dvr.getLUserId())));
    }

    @Override
    public void stop(List<Integer> lUserIdList) {
        lUserIdList.forEach(PreviewUntil::shutdown);
    }

    /**
     * 根据设备id和摄像头ip地址查询摄像头
     *
     * @param deviceId 设备id
     * @param ip       摄像头ip地址
     * @param dvrId    排除的摄像头id
     * @return 摄像头
     */
    private DeviceDvr selectOneByDeviceIdAndDvrIp(Integer deviceId, String ip, Integer dvrId) {
        LambdaQueryWrapper<DeviceDvr> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DeviceDvr::getDeviceId, deviceId)
                .eq(DeviceDvr::getIp, ip);
        if (null != dvrId) {
            lambdaQueryWrapper.ne(DeviceDvr::getDvrId, dvrId);
        }
        return deviceDvrMapper.selectOne(lambdaQueryWrapper);
    }

}




