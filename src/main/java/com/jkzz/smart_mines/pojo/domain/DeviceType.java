package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jkzz.smart_mines.enumerate.impl.CommunicationProtocolEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName device_type
 */
@TableName(value = "device_type")
@Data
public class DeviceType implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 设备类型id
     */
    @TableId(type = IdType.AUTO)
    private Integer deviceTypeId;
    /**
     * 设备类型名称
     */
    private String deviceTypeName;
    /**
     * 通讯协议
     */
    private CommunicationProtocolEnum communicationProtocol;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        DeviceType other = (DeviceType) that;
        return (this.getDeviceTypeId() == null ? other.getDeviceTypeId() == null : this.getDeviceTypeId().equals(other.getDeviceTypeId()))
                && (this.getDeviceTypeName() == null ? other.getDeviceTypeName() == null : this.getDeviceTypeName().equals(other.getDeviceTypeName()))
                && (this.getCommunicationProtocol() == null ? other.getCommunicationProtocol() == null : this.getCommunicationProtocol().equals(other.getCommunicationProtocol()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDeviceTypeId() == null) ? 0 : getDeviceTypeId().hashCode());
        result = prime * result + ((getDeviceTypeName() == null) ? 0 : getDeviceTypeName().hashCode());
        result = prime * result + ((getCommunicationProtocol() == null) ? 0 : getCommunicationProtocol().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", deviceTypeId=").append(deviceTypeId);
        sb.append(", deviceTypeName=").append(deviceTypeName);
        sb.append(", communicationProtocol=").append(communicationProtocol);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}