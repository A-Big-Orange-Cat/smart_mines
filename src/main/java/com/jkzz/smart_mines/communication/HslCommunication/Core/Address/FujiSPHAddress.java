package com.jkzz.smart_mines.communication.HslCommunication.Core.Address;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.HslHelper;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.StringResources;

public class FujiSPHAddress extends DeviceAddressDataBase {

    private byte TypeCode = 0;
    private int BitIndex = 0;

    /**
     * 从实际的Fuji的地址里面解析出地址对象<br />
     * Resolve the address object from the actual Fuji address
     *
     * @param address 富士的地址数据信息
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<FujiSPHAddress> ParseFrom(String address) {
        return ParseFrom(address, (short) 0);
    }

    /**
     * 从实际的Fuji的地址里面解析出地址对象<br />
     * Resolve the address object from the actual Fuji address
     *
     * @param address 富士的地址数据信息
     * @param length  读取的数据长度
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<FujiSPHAddress> ParseFrom(String address, short length) {
        FujiSPHAddress addressData = new FujiSPHAddress();
        try {
            switch (address.charAt(0)) {
                case 'M':
                case 'm': {
                    String[] splits = address.split("\\.");
                    int datablock = Integer.parseInt(splits[0].substring(1));
                    if (datablock == 0x01) addressData.setTypeCode((byte) 0x02);
                    else if (datablock == 0x03) addressData.setTypeCode((byte) 0x04);
                    else if (datablock == 0x0A) addressData.setTypeCode((byte) 0x08);
                    else throw new Exception(StringResources.Language.NotSupportedDataType());

                    addressData.setAddressStart(Integer.parseInt(splits[1]));
                    if (splits.length > 2) addressData.BitIndex = HslHelper.CalculateBitStartIndex(splits[2]);
                    break;
                }
                case 'Q':
                case 'q':
                case 'I':
                case 'i': {
                    String[] splits = address.split("\\.");
                    addressData.TypeCode = 0x01;
                    addressData.setAddressStart(Integer.parseInt(splits[0].substring(1)));
                    if (splits.length > 1) addressData.BitIndex = HslHelper.CalculateBitStartIndex(splits[1]);
                    break;
                }
                default:
                    throw new Exception(StringResources.Language.NotSupportedDataType());
            }
        } catch (Exception ex) {
            return new OperateResultExOne<>(ex.getMessage());
        }
        return OperateResultExOne.CreateSuccessResult(addressData);
    }

    /**
     * 获取类型代号
     *
     * @return 类型代号信息
     */
    public byte getTypeCode() {
        return TypeCode;
    }

    /**
     * 设置类型代号信息
     *
     * @param typeCode 类型代号信息
     */
    public void setTypeCode(byte typeCode) {
        TypeCode = typeCode;
    }

    // region Static Method

    /**
     * 获取位索引信息
     *
     * @return 位索引信息
     */
    public int getBitIndex() {
        return BitIndex;
    }

    /**
     * 设置位索引信息
     *
     * @param bitIndex 位索引信息
     */
    public void setBitIndex(int bitIndex) {
        BitIndex = bitIndex;
    }

    // endregion
}
