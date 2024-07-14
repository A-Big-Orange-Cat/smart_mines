package com.jkzz.smart_mines.communication.HslCommunication.Profinet.Delta;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.HslHelper;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExTwo;
import com.jkzz.smart_mines.communication.HslCommunication.ModBus.ModbusInfo;
import com.jkzz.smart_mines.communication.HslCommunication.StringResources;

/**
 * 台达PLC的相关的帮助类，公共的地址解析的方法。<br />
 * Delta PLC related help classes, public address resolution methods.
 */
public class DeltaHelper {

    /**
     * 根据台达PLC的地址，解析出转换后的modbus协议信息，适用DVP系列，当前的地址仍然支持站号指定，例如s=2;D100<br />
     * According to the address of Delta PLC, the converted modbus protocol information is parsed out, applicable to DVP series,
     * the current address still supports station number designation, such as s=2;D100
     *
     * @param address    台达plc的地址信息
     * @param modbusCode 原始的对应的modbus信息
     * @return 还原后的modbus地址
     */
    public static OperateResultExOne<String> PraseDeltaDvpAddress(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = HslHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = stationPara.Content2;
            }

            if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil) {
                if (address.startsWith("S") || address.startsWith("s"))
                    return OperateResultExOne.CreateSuccessResult(station + Integer.parseInt(address.substring(1)));
                else if (address.startsWith("X") || address.startsWith("x"))
                    return OperateResultExOne.CreateSuccessResult(station + "x=2;" + (Integer.parseInt(address.substring(1), 8) + 0x400));
                else if (address.startsWith("Y") || address.startsWith("y"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1), 8) + 0x500));
                else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x600));
                else if (address.startsWith("C") || address.startsWith("c"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xE00));
                else if (address.startsWith("M") || address.startsWith("m")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 1536)
                        return OperateResultExOne.CreateSuccessResult(station + (add - 1536 + 0xB000));
                    else
                        return OperateResultExOne.CreateSuccessResult(station + (add + 0x800));
                }
            } else {
                if (address.startsWith("D") || address.startsWith("d")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 4096)
                        return OperateResultExOne.CreateSuccessResult(station + (add - 4096 + 0x9000));
                    else
                        return OperateResultExOne.CreateSuccessResult(station + (add + 0x1000));
                } else if (address.startsWith("C") || address.startsWith("c")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 200)
                        return OperateResultExOne.CreateSuccessResult(station + (add - 200 + 0x0EC8));
                    else
                        return OperateResultExOne.CreateSuccessResult(station + (add + 0x0E00));
                } else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x600));
            }

            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        } catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }
}
