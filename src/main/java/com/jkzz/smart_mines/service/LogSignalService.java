package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.pojo.domain.LogSignal;
import com.jkzz.smart_mines.pojo.qo.LogCurveQO;
import com.jkzz.smart_mines.pojo.vo.LogSignalCurveVO;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【log_signal】的数据库操作Service
 * @createDate 2024-07-01 13:59:38
 */
public interface LogSignalService extends IService<LogSignal> {

    /**
     * 新增信号量记录
     *
     * @param deviceId                  设备id
     * @param baseDeviceTypeParameterId 基础设备类型参数id
     * @param signalValue               信号量的值
     * @param timeMillis                时间戳
     */
    void insert(Integer deviceId, Integer baseDeviceTypeParameterId, String signalValue, long timeMillis);

    /**
     * 清理数据（保存近半年）
     */
    void cleanUp();

    /**
     * 查询信号量变化曲线
     *
     * @param logCurveQO 查询条件
     * @return “信号量-时间戳”集合
     */
    List<LogSignalCurveVO> queryLogSignalCurve(LogCurveQO logCurveQO);

}
