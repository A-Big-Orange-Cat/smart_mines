package com.jkzz.smart_mines.communication.HslCommunication.Profinet.Inovance;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.ModBus.ModbusTcpNet;

public class InovanceTcpNet extends ModbusTcpNet {

    // region Constructor

    private InovanceSeries Series = InovanceSeries.AM;   // 系列信息

    /**
     * 实例化一个默认的对象
     */
    public InovanceTcpNet() {
        super();
        Series = InovanceSeries.AM;
    }

    /**
     * 通过指定站号，ip地址，端口号来实例化一个新的对象
     *
     * @param ipAddress Ip地址
     * @param port      端口号
     * @param station   站号信息
     */
    public InovanceTcpNet(String ipAddress, int port, byte station) {
        super(ipAddress, port, station);
        Series = InovanceSeries.AM;
    }

    // endregion

    // region Public Properties

    /**
     * 通过指定站号，IP地址，端口以及PLC的系列来实例化一个新的对象<br />
     *
     * @param series    PLC的系列
     * @param ipAddress Ip地址
     * @param port      端口号
     * @param station   站号信息
     */
    public InovanceTcpNet(InovanceSeries series, String ipAddress, int port, byte station) {
        super(ipAddress, port, station);
        Series = series;
    }

    /**
     * 获取当前PLC的系列信息
     *
     * @return 系列
     */
    public InovanceSeries getSeries() {
        return Series;
    }

    /**
     * 设置当前PLC的系列信息，需要在连接之前设置完成
     *
     * @param series 系列信息
     */
    public void setSeries(InovanceSeries series) {
        Series = series;
    }

    // endregion

    // region Override

    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode) {
        return InovanceHelper.PraseInovanceAddress(this.Series, address, modbusCode);
    }

    // endregion

    // region Object Override

    public String toString() {
        return "InovanceTcpNet<" + Series + ">[" + getIpAddress() + ":" + getPort() + "]";
    }

    // endregion
}
