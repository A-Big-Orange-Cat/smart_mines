package com.jkzz.smart_mines.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.mapper.DeviceTypeRelationMapper;
import com.jkzz.smart_mines.pojo.domain.DeviceTypeRelation;
import com.jkzz.smart_mines.service.DeviceTypeRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【device_type_relation】的数据库操作Service实现
 * @createDate 2024-07-01 10:10:55
 */
@Service
public class DeviceTypeRelationServiceImpl extends ServiceImpl<DeviceTypeRelationMapper, DeviceTypeRelation>
        implements DeviceTypeRelationService {

    @Autowired
    DeviceTypeRelationMapper deviceTypeRelationMapper;

    @Override
    public List<Integer> queryBaseDeviceTypeIdsByDeviceTypeId(Integer deviceTypeId) {
        LambdaQueryWrapper<DeviceTypeRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DeviceTypeRelation::getDeviceTypeId, deviceTypeId)
                .select(DeviceTypeRelation::getBaseDeviceTypeId);
        return deviceTypeRelationMapper.selectList(lambdaQueryWrapper).stream()
                .map(DeviceTypeRelation::getBaseDeviceTypeId)
                .collect(Collectors.toList());
    }

}




