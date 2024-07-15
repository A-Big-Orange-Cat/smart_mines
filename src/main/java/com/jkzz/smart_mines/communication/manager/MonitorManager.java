package com.jkzz.smart_mines.communication.manager;

import com.jkzz.smart_mines.communication.core.Monitor;
import com.jkzz.smart_mines.communication.net.CommunicationNet;
import com.jkzz.smart_mines.enumerate.impl.CommunicationNetEnum;
import com.jkzz.smart_mines.enumerate.impl.CommunicationProtocolEnum;
import com.jkzz.smart_mines.pojo.domain.BaseDeviceTypeParameter;
import com.jkzz.smart_mines.pojo.domain.Device;
import com.jkzz.smart_mines.pojo.domain.DeviceTypeParameter;
import com.jkzz.smart_mines.service.LogAlarmService;
import com.jkzz.smart_mines.service.LogOperationService;
import com.jkzz.smart_mines.service.LogSignalService;
import com.jkzz.smart_mines.utils.EnumUtil;
import com.jkzz.smart_mines.utils.HAUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Component
@Scope("prototype")
public class MonitorManager {

    /**
     * 监视器的集合
     */
    private final Map<String, Monitor> monitorMap = new ConcurrentHashMap<>();
    private final ApplicationContext context;
    private final CommunicationManager communicationManager;
    private final LogAlarmService logAlarmService;
    private final LogOperationService logOperationService;
    private final LogSignalService logSignalService;
    @Resource(name = "commThreadPoolExecutor")
    private TaskScheduler commThreadPoolExecutor;
    @Value("${time.fixedRate.read}")
    private int timeInterval;
    /**
     * 通讯协议
     */
    private CommunicationProtocolEnum communicationProtocol;
    /**
     * “信号”参数列表
     */
    private List<BaseDeviceTypeParameter> parametersOfSignal;
    /**
     * “操作指令”参数map
     */
    private Map<String, BaseDeviceTypeParameter> parametersOfCommand;
    /**
     * “参数设置“参数列表
     */
    private Map<String, BaseDeviceTypeParameter> parametersOfParaSetting;
    /**
     * “报警“参数列表
     */
    private List<BaseDeviceTypeParameter> parametersOfAlarm;
    /**
     * 设备类型参数map
     */
    private Map<String, DeviceTypeParameter> deviceTypeParameters;

    @Autowired
    public MonitorManager(ApplicationContext context, CommunicationManager communicationManager, LogAlarmService logAlarmService, LogOperationService logOperationService, LogSignalService logSignalService) {
        this.context = context;
        this.communicationManager = communicationManager;
        this.logAlarmService = logAlarmService;
        this.logOperationService = logOperationService;
        this.logSignalService = logSignalService;
    }

    @Async("commThreadPoolExecutor")
    public void createMonitor(Device device) {
        // 创建监视器对象
        Optional.ofNullable(HAUtil.getMonitor(device.getDeviceTypeId())).ifPresent(monitor -> {
            // 创建通信对象
            String communicationName = EnumUtil.getNameByValue(CommunicationNetEnum.values(), communicationProtocol.getName());
            CommunicationNet communicationNet = (CommunicationNet) context.getBean(communicationName);
            communicationNet.createCommunicationNet(device.getIp(), device.getPort());

            // 初始化监视器参数
            initMonitor(monitor, device, communicationNet);

            monitor.run();
            monitorMap.put(device.getDeviceId().toString(), monitor);
        });
    }

    public void deleteMonitor(Integer deviceId) {
        if (monitorMap.containsKey(deviceId.toString())) {
            monitorMap.remove(deviceId.toString()).disconnect();
            communicationManager.getDevicesSimpleResultMap().remove(deviceId.toString());
        }
    }

    public void updateMonitor(Device device) {
        Monitor monitor = monitorMap.get(device.getDeviceId().toString());
        if (null == monitor) {
            createMonitor(device);
        } else {
            String ip = monitor.getDevice().getIp();
            int port = monitor.getDevice().getPort();
            if (!ip.equals(device.getIp()) || port != device.getPort()) {
                monitor.getCommunicationNet().setCommunicationNet(device.getIp(), device.getPort());
            }
            monitor.setDevice(device);
        }
    }

    private void initMonitor(Monitor monitor, Device device, CommunicationNet communicationNet) {
        monitor.setCommThreadPoolExecutor(commThreadPoolExecutor);
        monitor.setLogAlarmService(logAlarmService);
        monitor.setLogOperationService(logOperationService);
        monitor.setLogSignalService(logSignalService);
        monitor.setTimeInterval(timeInterval);
        monitor.setMonitorManager(this);
        monitor.setDevice(device);
        monitor.setCommunicationNet(communicationNet);
        // 参数设置的更改保存到操作记录数据库
        parametersOfParaSetting.forEach((code, parameter) ->
                monitor.getParameterLogOfOperation().put(parameter.getBaseDeviceTypeParameterCode(), true)
        );
        // 设置简略信息
        communicationManager.getDevicesSimpleResultMap().put(device.getDeviceId().toString(), monitor.getSimpleResultMap());
    }

}
