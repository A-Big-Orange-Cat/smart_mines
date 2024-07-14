package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.enumerate.impl.ControlModeEnum;
import com.jkzz.smart_mines.enumerate.impl.OperationResultEnum;
import com.jkzz.smart_mines.pojo.domain.LogOperation;
import com.jkzz.smart_mines.pojo.qo.LogQO;
import com.jkzz.smart_mines.pojo.vo.LogOperationVO;

/**
 * @author Administrator
 * @description 针对表【log_operation】的数据库操作Service
 * @createDate 2024-07-01 13:59:38
 */
public interface LogOperationService extends IService<LogOperation> {

    /**
     * 新增操作记录
     *
     * @param deviceId                  设备id
     * @param baseDeviceTypeParameterId 基础设备类型参数id
     * @param userId                    用户id
     * @param controlMode               远程/就地
     * @param operationValue            操作的值
     * @param operationResult           操作结果
     * @param timeMillis                时间戳
     */
    void insert(Integer deviceId, Integer baseDeviceTypeParameterId, Integer userId, ControlModeEnum controlMode, String operationValue, OperationResultEnum operationResult, long timeMillis);

    /**
     * 分页查询操作记录（每页10条记录）
     *
     * @param logQO 查询条件
     * @return 操作记录
     */
    IPage<LogOperationVO> queryLogOperationPage(LogQO logQO);
}
