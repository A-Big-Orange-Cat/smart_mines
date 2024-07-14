package com.jkzz.smart_mines.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.enumerate.impl.ControlModeEnum;
import com.jkzz.smart_mines.enumerate.impl.OperationResultEnum;
import com.jkzz.smart_mines.mapper.BaseDeviceTypeParameterMapper;
import com.jkzz.smart_mines.mapper.LogOperationMapper;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import com.jkzz.smart_mines.pojo.domain.LogOperation;
import com.jkzz.smart_mines.pojo.qo.LogQO;
import com.jkzz.smart_mines.pojo.vo.LogOperationVO;
import com.jkzz.smart_mines.service.LogOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【log_operation】的数据库操作Service实现
 * @createDate 2024-07-01 13:59:38
 */
@Service
public class LogOperationServiceImpl extends ServiceImpl<LogOperationMapper, LogOperation>
        implements LogOperationService {

    @Autowired
    LogOperationMapper logOperationMapper;

    @Autowired
    BaseDeviceTypeParameterMapper baseDeviceTypeParameterMapper;

    @Override
    public void insert(Integer deviceId, Integer baseDeviceTypeParameterId, Integer userId, ControlModeEnum controlMode, String operationValue, OperationResultEnum operationResult, long timeMillis) {
        logOperationMapper.insert(createLogOperation(deviceId, baseDeviceTypeParameterId, userId, controlMode, operationValue, operationResult, timeMillis));
    }

    @Override
    public IPage<LogOperationVO> queryLogOperationPage(LogQO logQO) {
        LambdaQueryWrapper<BaseDeviceTypeParameter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(BaseDeviceTypeParameter::getBaseDeviceTypeParameterId)
                .like(BaseDeviceTypeParameter::getBaseDeviceTypeParameterName, logQO.getParameterName());
        List<Integer> baseDeviceTypeParameterIds = baseDeviceTypeParameterMapper.selectList(queryWrapper).stream()
                .map(BaseDeviceTypeParameter::getBaseDeviceTypeParameterId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<LogOperation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LogOperation::getDeviceId, logQO.getDeviceId())
                .in(LogOperation::getBaseDeviceTypeParameterId, baseDeviceTypeParameterIds)
                .orderBy(true, true, LogOperation::getTimeMillis);
        if (null != logQO.getTime()) {
            lambdaQueryWrapper.between(LogOperation::getTimeMillis, logQO.getTime()[0], logQO.getTime()[1]);
        }
        IPage<LogOperationVO> logOperationIPage = new Page<>(logQO.getPageIndex(), 10);
        return logOperationMapper.queryLogOperationsPage(logOperationIPage, lambdaQueryWrapper);
    }

    private LogOperation createLogOperation(Integer deviceId, Integer baseDeviceTypeParameterId, Integer userId, ControlModeEnum controlMode, String operationValue, OperationResultEnum operationResult, long timeMillis) {
        return LogOperation.builder()
                .deviceId(deviceId)
                .baseDeviceTypeParameterId(baseDeviceTypeParameterId)
                .userId(userId)
                .controlMode(controlMode)
                .operationValue(operationValue)
                .operationResult(operationResult)
                .timeMillis(timeMillis)
                .build();
    }

}




