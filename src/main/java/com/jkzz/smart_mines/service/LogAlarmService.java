package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.pojo.domain.LogAlarm;
import com.jkzz.smart_mines.pojo.qo.LogQO;
import com.jkzz.smart_mines.pojo.vo.LogAlarmVO;

/**
 * @author Administrator
 * @description 针对表【log_alarm】的数据库操作Service
 * @createDate 2024-07-01 13:59:38
 */
public interface LogAlarmService extends IService<LogAlarm> {

    /**
     * 新增报警记录
     *
     * @param deviceId                  设备id
     * @param baseDeviceTypeParameterId 基础设备类型参数id
     * @param alarmValue                报警的值
     */
    void insert(int deviceId, int baseDeviceTypeParameterId, String alarmValue);

    /**
     * 分页查询报警记录（每页10条记录）
     *
     * @param logQO 查询条件
     * @return 报警记录
     */
    IPage<LogAlarmVO> queryLogAlarmPage(LogQO logQO);

}
