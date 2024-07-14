package com.jkzz.smart_mines.communication.HslCommunication.Profinet.Delta;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.DataFormat;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.ModBus.ModbusTcpNet;

/**
 * 台达PLC的网口通讯类，基于Modbus-Rtu协议开发，按照台达的地址进行实现。<br />
 * The tcp communication class of Delta PLC is developed based on the Modbus-Tcp protocol and implemented according to Delta's address.
 * <p>
 * 适用于DVP-ES/EX/EC/SS型号，DVP-SA/SC/SX/EH型号，地址参考API文档，同时地址可以携带站号信息，举例：[s=2;D100],[s=3;M100]，可以动态修改当前报文的站号信息。<br />
 */
public class DeltaDvpTcpNet extends ModbusTcpNet {
    public DeltaDvpTcpNet() {
        super();
        getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    public DeltaDvpTcpNet(String ipAddress, int port, byte station) {
        super(ipAddress, port, station);
        getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    @Override
    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode) {
        return DeltaHelper.PraseDeltaDvpAddress(address, modbusCode);
    }

    @Override
    public String toString() {
        return "DeltaDvpTcpNet[" + getIpAddress() + ":" + getPort() + "]";
    }
}
