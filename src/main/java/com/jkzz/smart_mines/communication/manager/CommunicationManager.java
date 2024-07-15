package com.jkzz.smart_mines.communication.manager;

import com.alibaba.fastjson2.JSONObject;
import com.jkzz.smart_mines.communication.core.Monitor;
import com.jkzz.smart_mines.communication.net.CommunicationNet;
import com.jkzz.smart_mines.communication.websocket.InfoWebSocket;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import com.jkzz.smart_mines.pojo.domain.Device;
import com.jkzz.smart_mines.pojo.domain.DeviceType;
import com.jkzz.smart_mines.pojo.domain.DeviceTypeParameter;
import com.jkzz.smart_mines.service.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Getter
@Component
public class CommunicationManager {

    private final ApplicationContext context;
    private final DeviceTypeRelationService deviceTypeRelationService;
    private final DeviceTypeService deviceTypeService;
    private final BaseDeviceTypeParameterService baseDeviceTypeParameterService;
    private final DeviceTypeParameterService deviceTypeParameterService;
    private final DeviceService deviceService;
    private final LogSignalService logSignalService;
    /**
     * monitor管理者的集合
     */
    private final Map<String, MonitorManager> monitorManagerMap = new ConcurrentHashMap<>();
    /**
     * 与前端通信的WebSocket
     */
    private final CopyOnWriteArrayList<InfoWebSocket> infoWebSockets = new CopyOnWriteArrayList<>();
    /**
     * 设备的实时简略数据结果
     */
    private final JSONObject devicesSimpleResultMap = new JSONObject();

    @Autowired
    public CommunicationManager(ApplicationContext context, DeviceTypeRelationService deviceTypeRelationService, DeviceTypeService deviceTypeService, BaseDeviceTypeParameterService baseDeviceTypeParameterService, DeviceTypeParameterService deviceTypeParameterService, DeviceService deviceService, LogSignalService logSignalService) {
        this.context = context;
        this.deviceTypeRelationService = deviceTypeRelationService;
        this.deviceTypeService = deviceTypeService;
        this.baseDeviceTypeParameterService = baseDeviceTypeParameterService;
        this.deviceTypeParameterService = deviceTypeParameterService;
        this.deviceService = deviceService;
        this.logSignalService = logSignalService;
    }

    //@PostConstruct
    public void init() {
        log.info("开始初始化PLC监视器。");
        List<DeviceType> deviceTypes = deviceTypeService.queryAll();
        // 创建对应类型的monitorManager
        deviceTypes.forEach(this::createMonitorManager);
    }

    @PreDestroy
    public void destroy() {
        monitorManagerMap.forEach((deviceTypeId, monitorManager) ->
                monitorManager.getMonitorMap().forEach((deviceId, monitor) ->
                        disconnectCommunicationNet(monitor.getCommunicationNet())
                )
        );
    }

    /**
     * 大屏发送设备简略信息
     */
    @Async("commThreadPoolExecutor")
    @Scheduled(fixedRateString = "${time.fixedRate.send}")
    public void sendDeviceSimpleInfo() {
        infoWebSockets.forEach(this::sendMessage);
    }

    /**
     * 设备详情发送设备信息
     */
    @Async("commThreadPoolExecutor")
    @Scheduled(fixedRateString = "${time.fixedRate.send}")
    public void sendDeviceInfo() {
        monitorManagerMap.forEach((deviceTypeId, monitorManager) ->
                monitorManager.getMonitorMap().forEach((deviceId, monitor) -> sendMessage(monitor))
        );
    }

    /**
     * 数据库清理，每月清理一次
     */
    @Async("commThreadPoolExecutor")
    @Scheduled(cron = "0 0 0 L * ?")
    public void cleanUpDatabase() {
        logSignalService.cleanUp();
    }

    @Async("commThreadPoolExecutor")
    public void createMonitorManager(DeviceType deviceType) {
        MonitorManager monitorManager = (MonitorManager) context.getBean("monitorManager");
        List<Integer> baseDeviceTypeIds = deviceTypeRelationService.queryBaseDeviceTypeIdsByDeviceTypeId(deviceType.getDeviceTypeId());
        // 获取设备的所有信号参数信息
        List<BaseDeviceTypeParameter> parametersOfSignal = baseDeviceTypeParameterService.querySignalsByBaseDeviceTypeId(baseDeviceTypeIds);
        // 获取设备的所有操作指令参数信息
        Map<String, BaseDeviceTypeParameter> parametersOfCommand = baseDeviceTypeParameterService.queryCommandsByBaseDeviceTypeId(baseDeviceTypeIds);
        // 获取设备的所有参数设置参数信息
        Map<String, BaseDeviceTypeParameter> parametersOfParaSetting = baseDeviceTypeParameterService.queryParaSettingsByBaseDeviceTypeId(baseDeviceTypeIds);
        // 获取设备的所有报警参数信息
        List<BaseDeviceTypeParameter> parametersOfAlarm = baseDeviceTypeParameterService.queryAlarmsByBaseDeviceTypeId(baseDeviceTypeIds);
        //
        Map<String, DeviceTypeParameter> deviceTypeParameters = deviceTypeParameterService.queryMapByDeviceTypeId(deviceType.getDeviceTypeId());

        monitorManager.setCommunicationProtocol(deviceType.getCommunicationProtocol());
        monitorManager.setParametersOfSignal(parametersOfSignal);
        monitorManager.setParametersOfCommand(parametersOfCommand);
        monitorManager.setParametersOfParaSetting(parametersOfParaSetting);
        monitorManager.setParametersOfAlarm(parametersOfAlarm);
        monitorManager.setDeviceTypeParameters(deviceTypeParameters);

        List<Device> devices = deviceService.queryNormalByDeviceTypeIdOrDeviceName(deviceType.getDeviceTypeId(), null);
        devices.forEach(monitorManager::createMonitor);

        monitorManagerMap.put(deviceType.getDeviceTypeId().toString(), monitorManager);
    }

    private void disconnectCommunicationNet(CommunicationNet communicationNet) {
        communicationNet.disconnect();
    }

    private void sendMessage(InfoWebSocket infoWebSocket) {
        if (null != infoWebSocket) {
            infoWebSocket.sendOneMessage(devicesSimpleResultMap.toJSONString());
        }
    }

    private void sendMessage(Monitor monitor) {
        monitor.send();
    }

}
