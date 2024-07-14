package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.pojo.domain.DeviceTypeRelation;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【device_type_relation】的数据库操作Service
 * @createDate 2024-07-01 10:10:55
 */
public interface DeviceTypeRelationService extends IService<DeviceTypeRelation> {

    /**
     * 查询基础设备类型id
     *
     * @param deviceTypeId 设备类型id
     * @return 查询基础设备类型id集合
     */
    List<Integer> queryBaseDeviceTypeIdsByDeviceTypeId(Integer deviceTypeId);

}
