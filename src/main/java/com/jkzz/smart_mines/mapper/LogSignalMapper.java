package com.jkzz.smart_mines.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.jkzz.smart_mines.pojo.domain.LogSignal;
import com.jkzz.smart_mines.pojo.vo.LogSignalCurveVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【log_signal】的数据库操作Mapper
 * @createDate 2024-07-01 13:59:38
 * @Entity com.jkzz.smart_mines.pojo.domain.LogSignal
 */
public interface LogSignalMapper extends BaseMapper<LogSignal> {

    List<LogSignalCurveVO> queryLogSignalCurve(@Param(Constants.WRAPPER) Wrapper<LogSignal> lambdaQueryWrapper);

}




