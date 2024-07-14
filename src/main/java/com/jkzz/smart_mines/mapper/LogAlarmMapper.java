package com.jkzz.smart_mines.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.jkzz.smart_mines.pojo.domain.LogAlarm;
import com.jkzz.smart_mines.pojo.vo.LogAlarmVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 * @description 针对表【log_alarm】的数据库操作Mapper
 * @createDate 2024-07-01 13:59:38
 * @Entity com.jkzz.smart_mines.pojo.domain.LogAlarm
 */
public interface LogAlarmMapper extends BaseMapper<LogAlarm> {

    IPage<LogAlarmVO> queryLogAlarmsPage(IPage<LogAlarmVO> page, @Param(Constants.WRAPPER) Wrapper<LogAlarm> queryWrapper);

}




