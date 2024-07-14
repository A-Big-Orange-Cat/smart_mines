package com.jkzz.smart_mines.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.enumerate.impl.ParameterTypeEnum;
import com.jkzz.smart_mines.mapper.BaseDeviceTypeParameterMapper;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import com.jkzz.smart_mines.service.BaseDeviceTypeParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【base_device_type_parameter】的数据库操作Service实现
 * @createDate 2024-07-01 10:10:55
 */
@Service
public class BaseDeviceTypeParameterServiceImpl extends ServiceImpl<BaseDeviceTypeParameterMapper, BaseDeviceTypeParameter>
        implements BaseDeviceTypeParameterService {

    @Autowired
    private BaseDeviceTypeParameterMapper baseDeviceTypeParameterMapper;

    @Override
    public List<BaseDeviceTypeParameter> queryByBaseDeviceTypeId(List<Integer> baseDeviceTypeIds) {
        return selectListByBaseDeviceTypeIdAndParameterType(baseDeviceTypeIds);
    }

    @Override
    public List<BaseDeviceTypeParameter> querySignalsByBaseDeviceTypeId(List<Integer> baseDeviceTypeIds) {
        return selectListByBaseDeviceTypeIdAndParameterType(baseDeviceTypeIds, ParameterTypeEnum.SIGNAL, ParameterTypeEnum.ALARM_SIGNAL);
    }

    @Override
    public Map<String, BaseDeviceTypeParameter> queryCommandsByBaseDeviceTypeId(List<Integer> baseDeviceTypeIds) {
        return selectListByBaseDeviceTypeIdAndParameterType(baseDeviceTypeIds, ParameterTypeEnum.COMMAND)
                .stream()
                .collect(Collectors.toMap(BaseDeviceTypeParameter::getBaseDeviceTypeParameterCode, parameter -> parameter));
    }

    @Override
    public Map<String, BaseDeviceTypeParameter> queryParaSettingsByBaseDeviceTypeId(List<Integer> baseDeviceTypeIds) {
        return selectListByBaseDeviceTypeIdAndParameterType(baseDeviceTypeIds, ParameterTypeEnum.PARAMETER_SETTING)
                .stream()
                .collect(Collectors.toMap(BaseDeviceTypeParameter::getBaseDeviceTypeParameterCode, parameter -> parameter));
    }

    @Override
    public List<BaseDeviceTypeParameter> queryAlarmsByBaseDeviceTypeId(List<Integer> baseDeviceTypeIds) {
        return selectListByBaseDeviceTypeIdAndParameterType(baseDeviceTypeIds, ParameterTypeEnum.ALARM);
    }

    @Override
    public BaseDeviceTypeParameter queryByBaseDeviceTypeIdAndCode(List<Integer> baseDeviceTypeIds, String baseDeviceTypeParameterCode) {
        LambdaQueryWrapper<BaseDeviceTypeParameter> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BaseDeviceTypeParameter::getBaseDeviceTypeParameterCode, baseDeviceTypeParameterCode)
                .in(BaseDeviceTypeParameter::getBaseDeviceTypeId, baseDeviceTypeIds);
        return baseDeviceTypeParameterMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 根据设备类型id和参数类型查询基础设备类型参数
     *
     * @param baseDeviceTypeIds 基础设备类型id列表
     * @param parameterTypes    多个参数类型
     * @return 基础设备类型参数列表
     */
    private List<BaseDeviceTypeParameter> selectListByBaseDeviceTypeIdAndParameterType(List<Integer> baseDeviceTypeIds, ParameterTypeEnum... parameterTypes) {
        LambdaQueryWrapper<BaseDeviceTypeParameter> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(BaseDeviceTypeParameter::getBaseDeviceTypeId, baseDeviceTypeIds)
                .orderBy(true, true, BaseDeviceTypeParameter::getSortNumber);
        if (parameterTypes.length > 0) {
            lambdaQueryWrapper.in(BaseDeviceTypeParameter::getType, (Object[]) parameterTypes);
        }
        return baseDeviceTypeParameterMapper.selectList(lambdaQueryWrapper);
    }

}




