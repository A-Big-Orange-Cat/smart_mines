package com.jkzz.smart_mines.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.mapper.LogAlarmMapper;
import com.jkzz.smart_mines.pojo.domain.LogAlarm;
import com.jkzz.smart_mines.pojo.qo.LogQO;
import com.jkzz.smart_mines.pojo.vo.LogAlarmVO;
import com.jkzz.smart_mines.service.LogAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Administrator
 * @description 针对表【log_alarm】的数据库操作Service实现
 * @createDate 2024-07-01 13:59:38
 */
@Service
@RequiredArgsConstructor
public class LogAlarmServiceImpl extends ServiceImpl<LogAlarmMapper, LogAlarm>
        implements LogAlarmService {

    private final LogAlarmMapper logAlarmMapper;

    @Override
    public void insert(int deviceId, int baseDeviceTypeParameterId, String alarmValue) {
        logAlarmMapper.insert(createLogAlarm(deviceId, baseDeviceTypeParameterId, alarmValue));
    }

    @Override
    public IPage<LogAlarmVO> queryLogAlarmPage(LogQO logQO) {
        LambdaQueryWrapper<LogAlarm> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LogAlarm::getDeviceId, logQO.getDeviceId())
                .orderBy(true, true, LogAlarm::getTimeMillis);
        Optional.ofNullable(logQO.getTime()).ifPresent(time ->
                lambdaQueryWrapper.between(LogAlarm::getTimeMillis, time[0], time[1])
        );
        IPage<LogAlarmVO> logAlarmIPage = new Page<>(logQO.getPageIndex(), 10);
        return logAlarmMapper.queryLogAlarmsPage(logAlarmIPage, lambdaQueryWrapper);
    }

    private LogAlarm createLogAlarm(int deviceId, int baseDeviceTypeParameterId, String alarmValue) {
        long timeMillis = System.currentTimeMillis();
        return LogAlarm.builder()
                .deviceId(deviceId)
                .baseDeviceTypeParameterId(baseDeviceTypeParameterId)
                .alarmValue(alarmValue)
                .timeMillis(timeMillis)
                .build();
    }

}




