package com.jkzz.smart_mines.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.mapper.LogSignalMapper;
import com.jkzz.smart_mines.pojo.domain.LogSignal;
import com.jkzz.smart_mines.pojo.qo.LogCurveQO;
import com.jkzz.smart_mines.pojo.vo.LogSignalCurveVO;
import com.jkzz.smart_mines.service.BaseDeviceTypeParameterService;
import com.jkzz.smart_mines.service.DeviceTypeRelationService;
import com.jkzz.smart_mines.service.LogSignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @description 针对表【log_signal】的数据库操作Service实现
 * @createDate 2024-07-01 13:59:38
 */
@Service
@RequiredArgsConstructor
public class LogSignalServiceImpl extends ServiceImpl<LogSignalMapper, LogSignal>
        implements LogSignalService {

    private final LogSignalMapper logSignalMapper;

    private final DeviceTypeRelationService deviceTypeRelationService;

    private final BaseDeviceTypeParameterService baseDeviceTypeParameterService;

    @Override
    public void insert(Integer deviceId, Integer baseDeviceTypeParameterId, String signalValue, long timeMillis) {
        LogSignal signalLog = LogSignal.builder()
                .deviceId(deviceId)
                .baseDeviceTypeParameterId(baseDeviceTypeParameterId)
                .signalValue(signalValue)
                .timeMillis(timeMillis)
                .build();
        logSignalMapper.insert(signalLog);
    }

    @Override
    public void cleanUp() {
        LambdaQueryWrapper<LogSignal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.lt(LogSignal::getTimeMillis, System.currentTimeMillis() - 6L * 30 * 24 * 60 * 60 * 1000);
        logSignalMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public List<LogSignalCurveVO> queryLogSignalCurve(LogCurveQO logCurveQO) {
        List<Integer> baseDeviceTypeIds = deviceTypeRelationService.queryBaseDeviceTypeIdsByDeviceTypeId(logCurveQO.getDeviceTypeId());
        LambdaQueryWrapper<LogSignal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LogSignal::getDeviceId, logCurveQO.getDeviceId())
                .between(LogSignal::getTimeMillis, logCurveQO.getTime()[0], logCurveQO.getTime()[1])
                .orderBy(true, true, LogSignal::getTimeMillis);
        Optional.ofNullable(baseDeviceTypeParameterService.queryByBaseDeviceTypeIdAndCode(baseDeviceTypeIds, logCurveQO.getCode())).ifPresent(parameter -> {
            lambdaQueryWrapper.eq(LogSignal::getBaseDeviceTypeParameterId, parameter.getBaseDeviceTypeParameterId());
        });
        return logSignalMapper.queryLogSignalCurve(lambdaQueryWrapper);
    }

}




