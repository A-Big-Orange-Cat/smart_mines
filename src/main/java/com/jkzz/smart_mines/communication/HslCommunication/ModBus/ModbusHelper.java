package com.jkzz.smart_mines.communication.HslCommunication.ModBus;

import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Serial.SoftCRC16;
import com.jkzz.smart_mines.communication.HslCommunication.StringResources;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

import java.util.ArrayList;

public class ModbusHelper {

    public static OperateResultExOne<byte[]> ExtraRtuResponseContent(byte[] send, byte[] response) {
        // 长度校验
        if (response.length < 5)
            return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataLengthTooShort() + "5");

        // 检查crc
        if (!SoftCRC16.CheckCRC16(response))
            return new OperateResultExOne<byte[]>(StringResources.Language.ModbusCRCCheckFailed() +
                    SoftBasic.ByteToHexString(response, ' '));

        // 发生了错误
        if ((send[1] + 0x80) == response[1])
            return new OperateResultExOne<byte[]>(response[2], ModbusInfo.GetDescriptionByErrorCode(response[2]));

        if (send[1] != response[1])
            return new OperateResultExOne<byte[]>(response[1], "Receive Command Check Failed: ");

        // 移除CRC校验，返回真实数据
        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeRtuCommandToCore(response));
    }


    public static OperateResultExOne<byte[]> Read(IModbus modbus, String address, short length) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, ModbusInfo.ReadRegister);
        if (!modbusAddress.IsSuccess) return OperateResultExOne.CreateFailedResult(modbusAddress);

        OperateResultExOne<byte[][]> command = ModbusInfo.BuildReadModbusCommand(modbusAddress.Content, length,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.ReadRegister);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> resultArray = new ArrayList<Byte>();
        for (int i = 0; i < command.Content.length; i++) {
            OperateResultExOne<byte[]> read = modbus.ReadFromCoreServer(command.Content[i]);
            if (!read.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(read);

            Utilities.ArrayListAddArray(resultArray, read.Content);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(resultArray));
    }

    public static OperateResult Write(IModbus modbus, String address, byte[] value) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, ModbusInfo.WriteRegister);
        if (!modbusAddress.IsSuccess) return modbusAddress;

        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteWordModbusCommand(modbusAddress.Content, value,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.WriteRegister);
        if (!command.IsSuccess) return command;

        return modbus.ReadFromCoreServer(command.Content);
    }

    public static OperateResult Write(IModbus modbus, String address, short value) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, ModbusInfo.WriteOneRegister);
        if (!modbusAddress.IsSuccess) return modbusAddress;

        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteWordModbusCommand(modbusAddress.Content, value,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.WriteOneRegister);
        if (!command.IsSuccess) return command;

        return modbus.ReadFromCoreServer(command.Content);
    }

    public static OperateResult WriteMask(IModbus modbus, String address, short andMask, short orMask) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, ModbusInfo.WriteMaskRegister);
        if (!modbusAddress.IsSuccess) return modbusAddress;

        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteMaskModbusCommand(modbusAddress.Content, andMask, orMask,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.WriteMaskRegister);
        if (!command.IsSuccess) return command;

        return modbus.ReadFromCoreServer(command.Content);
    }

    public static OperateResultExOne<boolean[]> ReadBoolHelper(IModbus modbus, String address, short length, byte function) {
        if (address.indexOf('.') > 0) {
            String[] addressSplits = Utilities.SplitDot(address);
            int bitIndex = 0;
            try {
                bitIndex = Integer.parseInt(addressSplits[1]);
            } catch (Exception ex) {
                return new OperateResultExOne<boolean[]>("Bit Index format wrong, " + ex.getMessage());
            }
            short len = (short) ((length + bitIndex + 15) / 16);

            OperateResultExOne<byte[]> read = modbus.Read(addressSplits[0], len);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(SoftBasic.BytesReverseByWord(read.Content)), bitIndex, length));
        } else {
            OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, function);
            if (!modbusAddress.IsSuccess) return OperateResultExOne.CreateFailedResult(modbusAddress);

            OperateResultExOne<byte[][]> command = ModbusInfo.BuildReadModbusCommand(modbusAddress.Content, length,
                    modbus.getStation(), modbus.getAddressStartWithZero(), function);
            if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

            ArrayList<Boolean> resultArray = new ArrayList<>();
            for (int i = 0; i < command.Content.length; i++) {
                OperateResultExOne<byte[]> read = modbus.ReadFromCoreServer(command.Content[i]);
                if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

                int bitLength = (command.Content[i][4] & 0xff) * 256 + (command.Content[i][5] & 0xff);
                Utilities.ArrayListAddArray(resultArray, SoftBasic.ByteToBoolArray(read.Content, bitLength));
            }

            return OperateResultExOne.CreateSuccessResult(Utilities.getBools(resultArray));
        }
    }

    public static OperateResult Write(IModbus modbus, String address, boolean[] values) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, ModbusInfo.WriteCoil);
        if (!modbusAddress.IsSuccess) return modbusAddress;

        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteBoolModbusCommand(modbusAddress.Content, values,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.WriteCoil);
        if (!command.IsSuccess) return command;

        return modbus.ReadFromCoreServer(command.Content);
    }

    public static OperateResult Write(IModbus modbus, String address, boolean value) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, ModbusInfo.WriteOneCoil);
        if (!modbusAddress.IsSuccess) return modbusAddress;

        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteBoolModbusCommand(modbusAddress.Content, value,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.WriteOneCoil);
        if (!command.IsSuccess) return command;

        return modbus.ReadFromCoreServer(command.Content);
    }
}
