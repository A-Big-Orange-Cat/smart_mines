package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【base_device_type_parameter】的数据库操作Service
 * @createDate 2024-07-01 10:10:55
 */
public interface BaseDeviceTypeParameterService extends IService<BaseDeviceTypeParameter> {

    /**
     * 查询基础设备类型参数
     *
     * @param baseDeviceTypeIds 基础设备类型id列表
     * @return 基础设备类型参数集合
     */
    List<BaseDeviceTypeParameter> queryByBaseDeviceTypeId(List<Integer> baseDeviceTypeIds);

    /**
     * 查询“信号”基础设备类型参数
     *
     * @param baseDeviceTypeIds 基础设备类型id列表
     * @return “信号”基础设备类型参数集合
     */
    List<BaseDeviceTypeParameter> querySignalsByBaseDeviceTypeId(List<Integer> baseDeviceTypeIds);

    /**
     * 查询“操作指令”基础设备类型参数
     *
     * @param baseDeviceTypeIds 基础设备类型id列表
     * @return “操作指令”基础设备类型参数集合
     */
    Map<String, BaseDeviceTypeParameter> queryCommandsByBaseDeviceTypeId(List<Integer> baseDeviceTypeIds);

    /**
     * 查询“参数设置”基础设备类型参数
     *
     * @param baseDeviceTypeIds 基础设备类型id列表
     * @return “参数设置”基础设备类型参数集合
     */
    Map<String, BaseDeviceTypeParameter> queryParaSettingsByBaseDeviceTypeId(List<Integer> baseDeviceTypeIds);

    /**
     * 查询“报警”基础设备类型参数
     *
     * @param baseDeviceTypeIds 基础设备类型id列表
     * @return “报警”基础设备类型参数集合
     */
    List<BaseDeviceTypeParameter> queryAlarmsByBaseDeviceTypeId(List<Integer> baseDeviceTypeIds);

    /**
     * 查询基础设备类型参数
     *
     * @param baseDeviceTypeIds           基础设备类型id列表
     * @param baseDeviceTypeParameterCode 基础设备类型参数代码
     * @return 基础设备类型参数
     */
    BaseDeviceTypeParameter queryByBaseDeviceTypeIdAndCode(List<Integer> baseDeviceTypeIds, String baseDeviceTypeParameterCode);

}
