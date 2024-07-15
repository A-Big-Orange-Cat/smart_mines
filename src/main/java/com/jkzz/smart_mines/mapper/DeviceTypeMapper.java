package com.jkzz.smart_mines.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jkzz.smart_mines.pojo.domain.DeviceType;
import com.jkzz.smart_mines.pojo.vo.DeviceTypeVO;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【device_type】的数据库操作Mapper
 * @createDate 2024-07-01 10:10:55
 * @Entity com.jkzz.smart_mines.pojo.domain.DeviceType
 */
public interface DeviceTypeMapper extends BaseMapper<DeviceType> {

    List<DeviceTypeVO> selectDeviceTypeAndEnabledDeviceCount();

}




