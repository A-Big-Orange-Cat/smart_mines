package com.jkzz.smart_mines.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jkzz.smart_mines.pojo.domain.Device;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 * @description 针对表【device】的数据库操作Mapper
 * @createDate 2024-07-01 10:10:55
 * @Entity com.jkzz.smart_mines.pojo.domain.Device
 */
public interface DeviceMapper extends BaseMapper<Device> {

    int countByDeviceTypeIdAndEnableStatus(@Param("deviceTypeId") Integer deviceTypeId);

}




