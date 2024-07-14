package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.pojo.domain.DeviceDvr;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【device_dvr】的数据库操作Service
 * @createDate 2024-07-01 10:10:55
 */
public interface DeviceDvrService extends IService<DeviceDvr> {

    /**
     * 新增摄像头
     *
     * @param dvr 摄像头
     */
    void insert(DeviceDvr dvr);

    /**
     * 删除摄像头
     *
     * @param dvrId 摄像头id
     */
    void delete(Integer dvrId);

    /**
     * 删除设备的所有摄像头
     *
     * @param deviceId 设备id
     */
    void deleteByDeviceId(Integer deviceId);

    /**
     * 更新摄像头
     *
     * @param dvr 摄像头
     */
    void update(DeviceDvr dvr);

    /**
     * 查询摄像头
     *
     * @param deviceId 设备id
     * @return 摄像头集合
     */
    List<DeviceDvr> queryByDeviceId(Integer deviceId);

    /**
     * 实时预览
     *
     * @param dvrs 摄像头列表
     */
    void start(List<DeviceDvr> dvrs);

    /**
     * 关闭预览
     *
     * @param lUserIdList 用户句柄列表
     */
    void stop(List<Integer> lUserIdList);

}
