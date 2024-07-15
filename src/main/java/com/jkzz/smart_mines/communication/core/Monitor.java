package com.jkzz.smart_mines.communication.core;

import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.manager.MonitorManager;
import com.jkzz.smart_mines.communication.net.CommunicationNet;
import com.jkzz.smart_mines.communication.result.ReadResult;
import com.jkzz.smart_mines.communication.result.WriteResult;
import com.jkzz.smart_mines.communication.websocket.MonitorWebSocket;
import com.jkzz.smart_mines.enumerate.impl.*;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import com.jkzz.smart_mines.pojo.domain.Device;
import com.jkzz.smart_mines.service.LogAlarmService;
import com.jkzz.smart_mines.service.LogOperationService;
import com.jkzz.smart_mines.service.LogSignalService;
import com.jkzz.smart_mines.utils.VUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

@Getter
@Setter
@Slf4j
public abstract class Monitor {

    /**
     * 与前端通信的WebSocket
     */
    private final CopyOnWriteArrayList<MonitorWebSocket> monitorWebSockets = new CopyOnWriteArrayList<>();
    /**
     * 标记需要记录操作日志的参数地址，true表示就地更改了值，false表示远程更改了值
     */
    private final Map<String, Boolean> parameterLogOfOperation = new HashMap<>();
    /**
     * 标记需要记录信号量日志的参数地址
     */
    private final Set<String> parameterLogOfSignal = new HashSet<>();
    /**
     * 实时简略数据结果
     */
    private final JSONObject simpleResultMap = new JSONObject();
    /**
     * 实时数据结果
     */
    private final JSONObject resultMap = new JSONObject();
    /**
     * 实时信号量参数结果
     */
    private final JSONObject parameterValueOfSignalMap = new JSONObject();
    /**
     * 实时参数设置参数结果
     */
    private final JSONObject parameterValueOfParaSettingMap = new JSONObject();
    /**
     * 实时报警参数结果
     */
    private final JSONObject parameterValueOfAlarmMap = new JSONObject();
    private TaskScheduler commThreadPoolExecutor;
    private LogAlarmService logAlarmService;
    private LogOperationService logOperationService;
    private LogSignalService logSignalService;
    /**
     * monitor管理者
     */
    private MonitorManager monitorManager;
    /**
     * 读取PLC时间间隔
     */
    private int timeInterval;
    /**
     * 设备
     */
    private Device device;
    /**
     * 通讯对象
     */
    private CommunicationNet communicationNet;
    /**
     * 在线状态
     */
    private boolean connStatus = false;
    /**
     * 控制方式
     */
    private ControlModeEnum controlMode = ControlModeEnum.LOCAL;
    /**
     * 监视器运行状态
     */
    private boolean state = true;

    /**
     * 关闭监视器
     */
    public void disconnect() {
        communicationNet.disconnect();
        this.state = false;
    }

    /**
     * 启动监视器
     */
    public void run() {
        reset();
        VUtil.isTrueOrFalse(isConn()).trueOrFalseHandler(this::start, this::init);
    }

    protected void init() {
        final ScheduledFuture<?>[] future = {null};
        future[0] = commThreadPoolExecutor.scheduleAtFixedRate(() -> {
            if (isConn()) {
                start();
                while (null == future[0]) Thread.yield();
                future[0].cancel(false);
            }
        }, timeInterval);
    }

    /**
     * 开始通讯
     */
    protected void start() {
        log.info("设备名称：{}，ip：{}-->初始化完成。", device.getDeviceName(), device.getIp());
        simpleResultMap.put("alarm", parameterValueOfAlarmMap);
        resultMap.put("parametersOfSignal", parameterValueOfSignalMap);
        resultMap.put("parametersOfParaSetting", parameterValueOfParaSettingMap);
        resultMap.put("alarm", parameterValueOfAlarmMap);
        final ScheduledFuture<?>[] future = {null};
        future[0] = commThreadPoolExecutor.scheduleAtFixedRate(() -> {
            if (!state) {
                while (null == future[0]) Thread.yield();
                future[0].cancel(true);
                return;
            }
            read();
        }, timeInterval);
    }

    /**
     * 读取PLC参数
     */
    protected void read() {
        // 读取设备参数信息
        readDeviceParametersOfSignal();
        readDeviceParametersOfParaSetting();
        readDeviceParametersOfAlarm();
    }

    /**
     * 重新设置参数
     */
    protected abstract void reset();

    /**
     * 写入PLC之前执行的方法
     *
     * @param result    写入结果
     * @param parameter 参数
     * @param value     写入值
     * @param userId    用户id
     * @return 是否写入PLC
     */
    protected abstract boolean writePLCBefore(JSONObject result, BaseDeviceTypeParameter parameter, String value, Integer userId);

    /**
     * 写入PLC之后执行的方法
     *
     * @param parameter 参数
     * @param value     写入值
     */
    protected abstract void writePLCAfter(BaseDeviceTypeParameter parameter, String value);

    /**
     * 写入PLC并保存数据库之后执行的方法
     *
     * @param parameter  参数
     * @param writeValue 实际写入值
     * @param timeMillis 写入时间戳
     */
    protected abstract void writePLCAndLogAfter(BaseDeviceTypeParameter parameter, String writeValue, long timeMillis);

    /**
     * 读取PLC之后执行的方法
     *
     * @param result    读取结果
     * @param parameter 参数
     * @param newValue  读取结果
     * @param oldValue  原来结果
     */
    protected abstract void readPLCAfter(JSONObject result, BaseDeviceTypeParameter parameter, String newValue, String oldValue);

    /**
     * 读取PLC并保存数据库之后执行的方法
     *
     * @param parameter  参数
     * @param readValue  读取结果
     * @param timeMillis 读取时间戳
     */
    protected abstract void readPLCAndLogAfter(BaseDeviceTypeParameter parameter, String readValue, long timeMillis);

    /**
     * 判断 远程/就地 控制方式
     *
     * @param parameter 参数
     */
    protected void remoteOrLocalControl(BaseDeviceTypeParameter parameter) {
        VUtil.isTrue(ParameterTypeEnum.COMMAND.equals(parameter.getType()) && ControlModeEnum.LOCAL.equals(getControlMode()))
                .throwAppException(AppExceptionCodeMsg.PLC_CONTROL_NOT_REMOTE);
    }

    /**
     * 更改 远程/就地 控制方式
     *
     * @param result    写入结果
     * @param parameter 参数
     * @param value     值
     * @param userId    用户id
     */
    public void remoteOrLocalControl(JSONObject result, BaseDeviceTypeParameter parameter, String value, Integer userId) {
        WriteResult writeResult = writePLC(parameter, value);
        VUtil.handler(writeResult.isSuccess()).handler(() -> {
            setControlMode(Boolean.parseBoolean(writeResult.getWriteValue()) || "1".equals(writeResult.getWriteValue()) ? ControlModeEnum.REMOTE : ControlModeEnum.LOCAL);
            writeAgain(parameter);
        });
        writeLogDatabase(result, parameter, writeResult, userId);
    }

    /**
     * 将参数值写入PLC
     *
     * @param result    写入结果集合
     * @param parameter 参数
     * @param value     值
     * @param userId    用户id
     */
    public void writeDeviceParameter(JSONObject result, BaseDeviceTypeParameter parameter, String value, Integer userId) {
        remoteOrLocalControl(parameter);
        VUtil.handler(writePLCBefore(result, parameter, value, userId)).handler(() -> {
            WriteResult writeResult = writePLC(parameter, value);
            VUtil.handler(writeResult.isSuccess()).handler(() -> {
                writePLCAfter(parameter, writeResult.getWriteValue());
                writeAgain(parameter);
            });
            long timeMillis = writeLogDatabase(result, parameter, writeResult, userId);
            writePLCAndLogAfter(parameter, writeResult.getWriteValue(), timeMillis);
        });
    }

    /**
     * 写PLC
     *
     * @param parameter 参数
     * @param value     值
     * @return 写入结果
     */
    protected WriteResult writePLC(BaseDeviceTypeParameter parameter, String value) {
        WriteResult writeResult;
        switch (parameter.getValueType()) {
            case BOOL:
                Boolean writeBool = Boolean.parseBoolean(value) || "1".equals(value);
                writeResult = writeBool(parameter.getAddress(), parameter.getBaseDeviceTypeParameterName(), writeBool);
                writeResult.setWriteValue(String.valueOf(writeBool));
                break;
            case UINT:
                int writeUInt = (int) (Double.parseDouble(value) * Optional.ofNullable(parameter.getProportion()).orElse(1));
                writeResult = writeUInt16(parameter.getAddress(), parameter.getBaseDeviceTypeParameterName(), writeUInt);
                writeResult.setWriteValue(new BigDecimal(String.valueOf((double) writeUInt / Optional.ofNullable(parameter.getProportion()).orElse(1)))
                        .stripTrailingZeros().toPlainString());
                break;
            default:
                writeResult = new WriteResult(false, parameter.getBaseDeviceTypeParameterName(), value, "错误的值类型");
                break;
        }
        return writeResult;
    }

    /**
     * 更改需要记录操作日志的参数地址的标记，如果是瞬时量，写完之后恢复0
     *
     * @param parameter 参数
     */
    protected void writeAgain(BaseDeviceTypeParameter parameter) {
        parameterLogOfOperation.replace(parameter.getBaseDeviceTypeParameterCode(), false);
        VUtil.handler(IsInstantaneousEnum.IS_INSTANTANEOUS.equals(parameter.getIsInstantaneous())).handler(() -> {
            switch (parameter.getValueType()) {
                case BOOL:
                    writeBool(parameter.getAddress(), parameter.getBaseDeviceTypeParameterName(), false);
                    break;
                case UINT:
                    writeUInt16(parameter.getAddress(), parameter.getBaseDeviceTypeParameterName(), 0);
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * 读取设备实时参数的信号量
     */
    protected void readDeviceParametersOfSignal() {
        readDeviceParameters(monitorManager.getParametersOfSignal(), parameterValueOfSignalMap);
    }

    /**
     * 读取设备实时参数的参数设置
     */
    protected void readDeviceParametersOfParaSetting() {
        readDeviceParameters(new ArrayList<>(monitorManager.getParametersOfParaSetting().values()), parameterValueOfParaSettingMap);
    }

    /**
     * 读取设备的报警状态
     */
    protected void readDeviceParametersOfAlarm() {
        readDeviceParameters(monitorManager.getParametersOfAlarm(), parameterValueOfAlarmMap);
    }

    /**
     * 读取设备实施参数
     *
     * @param parameters 参数列表
     * @param resultMap  实时参数结果集合
     */
    protected void readDeviceParameters(List<BaseDeviceTypeParameter> parameters, JSONObject resultMap) {
        parameters.forEach(parameter -> {
            ReadResult<?> readResult = readPLC(parameter);
            VUtil.handler(readResult.isSuccess()).handler(() -> {
                String readValue = String.valueOf(readResult.getValue());
                String newValue;
                switch (parameter.getValueType()) {
                    case BOOL:
                        newValue = readValue;
                        break;
                    case UINT:
                        newValue = new BigDecimal(String.valueOf(Double.parseDouble(readValue) / Optional.ofNullable(parameter.getProportion()).orElse(1)))
                                .stripTrailingZeros().toPlainString();
                        break;
                    default:
                        newValue = null;
                        break;
                }
                String oldValue = (String) resultMap.put(parameter.getBaseDeviceTypeParameterCode(), newValue);
                readPLCAfter(resultMap, parameter, newValue, oldValue);
                readLogDatabase(parameter, newValue, oldValue);
            });
        });
    }

    /**
     * 读PLC
     *
     * @param parameter 参数
     * @return 读取结果
     */
    protected ReadResult<? extends Serializable> readPLC(BaseDeviceTypeParameter parameter) {
        switch (parameter.getValueType()) {
            case BOOL:
                return readBool(parameter.getAddress());
            case UINT:
                return readUInt16(parameter.getAddress());
            default:
                return new ReadResult<>(false, null, "错误的值类型");
        }
    }

    /**
     * 写入的参数记录数据库
     *
     * @param result      结果记录集合
     * @param parameter   参数
     * @param writeResult 写入结果
     * @param userId      用户id
     * @return 时间戳
     */
    protected long writeLogDatabase(JSONObject result, BaseDeviceTypeParameter parameter, WriteResult writeResult, Integer userId) {
        long timeMillis = System.currentTimeMillis();
        result.put(parameter.getBaseDeviceTypeParameterCode(), writeResult);
        OperationResultEnum operationResult = writeResult.isSuccess() ? OperationResultEnum.SUCCESS : OperationResultEnum.FAILURE;
        saveLogOperation(device.getDeviceId(), parameter.getBaseDeviceTypeParameterId(), userId, ControlModeEnum.REMOTE, writeResult.getWriteValue(), operationResult, timeMillis);
        return timeMillis;
    }

    /**
     * 读取的参数记录数据库
     *
     * @param parameter 参数
     * @param newValue  读取值
     * @param oldValue  原来值
     */
    protected void readLogDatabase(BaseDeviceTypeParameter parameter, String newValue, String oldValue) {
        if (null == oldValue) return;
        // 读取值和原来值不一样才保存数据库
        VUtil.handler(!newValue.equals(oldValue)).handler(() -> {
            long timeMillis = System.currentTimeMillis();
            switch (parameter.getType()) {
                case SIGNAL:
                    // 只有标记的信号量参数才保存数据库
                    VUtil.handler(parameterLogOfSignal.contains(parameter.getBaseDeviceTypeParameterCode())).handler(() -> {
                        saveLogSignal(device.getDeviceId(), parameter.getBaseDeviceTypeParameterId(), newValue, timeMillis);
                        readPLCAndLogAfter(parameter, newValue, timeMillis);
                    });
                    break;
                case ALARM_SIGNAL:
                    // 只有标记的报警信号量参数才对比保存数据库
                    Optional.ofNullable(monitorManager.getDeviceTypeParameters().get(parameter.getBaseDeviceTypeParameterCode()))
                            .ifPresent(deviceTypeParameter -> {
                                if (Double.parseDouble(newValue) >= Double.parseDouble(deviceTypeParameter.getValue())) {
                                    parameterValueOfAlarmMap.put(parameter.getBaseDeviceTypeParameterCode(), newValue);
                                    saveLogAlarm(device.getDeviceId(), parameter.getBaseDeviceTypeParameterId(), newValue);
                                } else {
                                    parameterValueOfAlarmMap.put(parameter.getBaseDeviceTypeParameterCode(), "0");
                                }
                            });
                    // 只有标记的报警信号量参数才保存数据库
                    VUtil.handler(parameterLogOfSignal.contains(parameter.getBaseDeviceTypeParameterCode())).handler(() -> {
                        saveLogSignal(device.getDeviceId(), parameter.getBaseDeviceTypeParameterId(), newValue, timeMillis);
                        readPLCAndLogAfter(parameter, newValue, timeMillis);
                    });
                    break;
                case PARAMETER_SETTING:
                    // 只有标记的操作参数才保存数据库
                    VUtil.handler(parameterLogOfOperation.containsKey(parameter.getBaseDeviceTypeParameterCode())).handler(() ->
                            // 如果是远程造成的修改就不保存数据库
                            VUtil.isTrueOrFalse(parameterLogOfOperation.get(parameter.getBaseDeviceTypeParameterCode())).trueOrFalseHandler(
                                    () -> {
                                        saveLogOperation(device.getDeviceId(), parameter.getBaseDeviceTypeParameterId(), 0, ControlModeEnum.LOCAL, newValue, OperationResultEnum.SUCCESS, timeMillis);
                                        readPLCAndLogAfter(parameter, newValue, timeMillis);
                                    },
                                    () -> parameterLogOfOperation.replace(parameter.getBaseDeviceTypeParameterCode(), true)
                            )
                    );
                    break;
                case ALARM:
                    VUtil.handler(!"0".equals(newValue)).handler(() -> {
                        saveLogAlarm(device.getDeviceId(), parameter.getBaseDeviceTypeParameterId(), newValue);
                        readPLCAndLogAfter(parameter, newValue, timeMillis);
                    });
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * 保存信号记录
     *
     * @param deviceId                  设备id
     * @param baseDeviceTypeParameterId 基础设备参数id
     * @param signalValue               信号量的值
     * @param timeMillis                时间戳
     */
    protected void saveLogSignal(Integer deviceId, Integer baseDeviceTypeParameterId, String signalValue, long timeMillis) {
        logSignalService.insert(deviceId, baseDeviceTypeParameterId, signalValue, timeMillis);
    }

    /**
     * 保存操作记录
     *
     * @param deviceId                  设备id
     * @param baseDeviceTypeParameterId 基础设备参数id
     * @param userId                    操作用户id
     * @param controlMode               就地/远程
     * @param operationValue            操作后的值
     * @param operationResult           操作结果
     * @param timeMillis                时间戳
     */
    protected void saveLogOperation(Integer deviceId, Integer baseDeviceTypeParameterId, Integer userId, ControlModeEnum controlMode, String operationValue, OperationResultEnum operationResult, long timeMillis) {
        logOperationService.insert(deviceId, baseDeviceTypeParameterId, userId, controlMode, operationValue, operationResult, timeMillis);
    }

    /**
     * 保存报警记录
     *
     * @param deviceId                  设备id
     * @param baseDeviceTypeParameterId 基础设备参数id
     * @param alarmValue                报警的值
     */
    protected void saveLogAlarm(Integer deviceId, Integer baseDeviceTypeParameterId, String alarmValue) {
        logAlarmService.insert(deviceId, baseDeviceTypeParameterId, alarmValue);
    }

    /**
     * 判断连接状态
     */
    protected boolean isConn() {
        boolean conn = communicationNet.getConn();
        if (connStatus != conn) {
            connStatus = conn;
        }
        simpleResultMap.put("connStatus", connStatus);
        resultMap.put("connStatus", connStatus);
        return conn;
    }

    public ReadResult<Integer> readUInt16(String address) {
        return isConn() ? communicationNet.readUInt16(address) : new ReadResult<>(false, null, "设备离线");
    }

    public ReadResult<Boolean> readBool(String address) {
        return isConn() ? communicationNet.readBool(address) : new ReadResult<>(false, null, "设备离线");
    }

    public WriteResult writeUInt16(String address, String parameterName, Integer value) {
        WriteResult writeResult = communicationNet.write(address, value);
        writeResult.setParameterName(parameterName);
        return writeResult;
    }

    public WriteResult writeBool(String address, String parameterName, Boolean value) {
        WriteResult writeResult = communicationNet.write(address, value);
        writeResult.setParameterName(parameterName);
        return writeResult;
    }

    /**
     * 向前端发送结果数据
     */
    public void send() {
        //通过WebSocket发送给前端
        monitorWebSockets.forEach(monitorWebSocket -> {
            if (null != monitorWebSocket) {
                monitorWebSocket.sendOneMessage(resultMap);
            }
        });
    }

}
