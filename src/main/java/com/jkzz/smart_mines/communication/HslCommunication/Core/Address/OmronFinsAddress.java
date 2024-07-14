package com.jkzz.smart_mines.communication.HslCommunication.Core.Address;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.HslHelper;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Profinet.Omron.OmronFinsDataType;
import com.jkzz.smart_mines.communication.HslCommunication.StringResources;

/**
 * 欧姆龙的Fins协议的地址类对象
 */
public class OmronFinsAddress extends DeviceAddressDataBase {

    private byte BitCode = 0;
    private byte WordCode = 0;

    /**
     * 从实际的欧姆龙的地址里面解析出地址对象<br />
     * Resolve the address object from the actual Profinet.Omron address
     *
     * @param address 欧姆龙的地址数据信息
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<OmronFinsAddress> ParseFrom(String address) {
        return ParseFrom(address, (short) 0);
    }

    /**
     * 从实际的欧姆龙的地址里面解析出地址对象<br />
     * Resolve the address object from the actual Profinet.Omron address
     *
     * @param address 欧姆龙的地址数据信息
     * @param length  读取的数据长度
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<OmronFinsAddress> ParseFrom(String address, short length) {
        OmronFinsAddress addressData = new OmronFinsAddress();
        try {
            addressData.setLength(length);
            switch (address.charAt(0)) {
                case 'D':
                case 'd': {
                    // DM区数据
                    addressData.BitCode = OmronFinsDataType.DM.getBitCode();
                    addressData.WordCode = OmronFinsDataType.DM.getWordCode();
                    break;
                }
                case 'C':
                case 'c': {
                    // CIO区数据
                    addressData.BitCode = OmronFinsDataType.CIO.getBitCode();
                    addressData.WordCode = OmronFinsDataType.CIO.getWordCode();
                    break;
                }
                case 'W':
                case 'w': {
                    // WR区
                    addressData.BitCode = OmronFinsDataType.WR.getBitCode();
                    addressData.WordCode = OmronFinsDataType.WR.getWordCode();
                    break;
                }
                case 'H':
                case 'h': {
                    // HR区
                    addressData.BitCode = OmronFinsDataType.HR.getBitCode();
                    addressData.WordCode = OmronFinsDataType.HR.getWordCode();
                    break;
                }
                case 'A':
                case 'a': {
                    // AR区
                    addressData.BitCode = OmronFinsDataType.AR.getBitCode();
                    addressData.WordCode = OmronFinsDataType.AR.getWordCode();
                    break;
                }
                case 'E':
                case 'e': {
                    // E区，比较复杂，需要专门的计算
                    String[] splits = address.split("\\.");
                    int block = Integer.parseInt(splits[0].substring(1), 16);
                    if (block < 16) {
                        addressData.BitCode = (byte) (0x20 + block);
                        addressData.WordCode = (byte) (0xA0 + block);
                    } else {
                        addressData.BitCode = (byte) (0xE0 + block - 16);
                        addressData.WordCode = (byte) (0x60 + block - 16);
                    }
                    break;
                }
                default:
                    throw new Exception(StringResources.Language.NotSupportedDataType());
            }

            if (address.charAt(0) == 'E' || address.charAt(0) == 'e') {
                String[] splits = address.split("\\.");
                int addr = Integer.parseInt(splits[1]) * 16;
                // 包含位的情况，例如 E1.100.F
                if (splits.length > 2) addr += HslHelper.CalculateBitStartIndex(splits[2]);
                addressData.setAddressStart(addr);
            } else {
                String[] splits = address.substring(1).split("\\.");
                int addr = Integer.parseInt(splits[0]) * 16;
                // 包含位的情况，例如 D100.F
                if (splits.length > 1) addr += HslHelper.CalculateBitStartIndex(splits[1]);
                addressData.setAddressStart(addr);
            }
        } catch (Exception ex) {
            return new OperateResultExOne<>(ex.getMessage());
        }

        return OperateResultExOne.CreateSuccessResult(addressData);
    }

    /**
     * 获取位操作指令
     *
     * @return 位操作指令
     */
    public byte getBitCode() {
        return BitCode;
    }

    /**
     * 设置位操作的指令
     *
     * @param bitCode 位操作指令
     */
    public void setBitCode(byte bitCode) {
        BitCode = bitCode;
    }

    /**
     * 获取字操作的指令
     *
     * @return 字操作指令
     */
    public byte getWordCode() {
        return WordCode;
    }


    // region Static Method

    /**
     * 设置字操作指令
     *
     * @param wordCode 字操作指令
     */
    public void setWordCode(byte wordCode) {
        WordCode = wordCode;
    }

    public void Parse(String address, int length) {
        OperateResultExOne<OmronFinsAddress> addressData = ParseFrom(address, (short) length);
        if (addressData.IsSuccess) {
            setAddressStart(addressData.Content.getAddressStart());
            setLength(addressData.Content.getLength());
            setBitCode(addressData.Content.getBitCode());
            setWordCode(addressData.Content.getWordCode());
        }
    }

    // endregion
}
