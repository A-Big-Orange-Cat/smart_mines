package com.jkzz.smart_mines.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jkzz.smart_mines.pojo.qo.LogCurveQO;
import com.jkzz.smart_mines.pojo.qo.LogQO;
import com.jkzz.smart_mines.pojo.vo.LogAlarmVO;
import com.jkzz.smart_mines.pojo.vo.LogOperationVO;
import com.jkzz.smart_mines.pojo.vo.LogSignalCurveVO;
import com.jkzz.smart_mines.response.Resp;
import com.jkzz.smart_mines.service.LogAlarmService;
import com.jkzz.smart_mines.service.LogOperationService;
import com.jkzz.smart_mines.service.LogSignalService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController {

    private final LogOperationService logOperationService;

    private final LogAlarmService logAlarmService;

    private final LogSignalService logSignalService;

    /**
     * 分页查询设备的操作记录(每次10条数据)
     *
     * @param logQO 查询条件
     * @return 结果数据
     */
    @PostMapping("/queryOperationLog")
    public Resp<IPage<LogOperationVO>> queryOperationLog(@Validated @RequestBody LogQO logQO) {
        IPage<LogOperationVO> logOperationVOIPage = logOperationService.queryLogOperationPage(logQO);
        return Resp.success(logOperationVOIPage);
    }

    /**
     * 分页查询设备的报警记录(每次10条数据)
     *
     * @param logQO 查询条件
     * @return 结果数据
     */
    @PostMapping("/queryAlarmLog")
    @ApiOperation("按设备id分页查询报警记录接口(每次100条数据)")
    public Resp<IPage<LogAlarmVO>> queryAlarmLog(@Validated @RequestBody LogQO logQO) {
        IPage<LogAlarmVO> logAlarmIPage = logAlarmService.queryLogAlarmPage(logQO);
        return Resp.success(logAlarmIPage);
    }

    /**
     * 查询设备的信号量记录曲线
     *
     * @param logCurveQO 查询条件
     * @return 结果数据
     */
    @PostMapping("/querySignalLogCurve")
    public Resp<List<LogSignalCurveVO>> querySignalLog(@RequestBody LogCurveQO logCurveQO) {
        List<LogSignalCurveVO> logSignalCurveVOS = logSignalService.queryLogSignalCurve(logCurveQO);
        return Resp.success(logSignalCurveVOS);
    }

}
