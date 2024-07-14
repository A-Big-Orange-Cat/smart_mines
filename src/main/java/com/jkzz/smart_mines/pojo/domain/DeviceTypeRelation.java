package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName device_type_relation
 */
@TableName(value = "device_type_relation")
@Data
public class DeviceTypeRelation implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 设备类型id
     */
    @TableId(type = IdType.NONE)
    private Integer deviceTypeId;
    /**
     * 基础设备类型id
     */
    //@TableId(type = IdType.NONE)
    private Integer baseDeviceTypeId;

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
        DeviceTypeRelation other = (DeviceTypeRelation) that;
        return (this.getDeviceTypeId() == null ? other.getDeviceTypeId() == null : this.getDeviceTypeId().equals(other.getDeviceTypeId()))
                && (this.getBaseDeviceTypeId() == null ? other.getBaseDeviceTypeId() == null : this.getBaseDeviceTypeId().equals(other.getBaseDeviceTypeId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDeviceTypeId() == null) ? 0 : getDeviceTypeId().hashCode());
        result = prime * result + ((getBaseDeviceTypeId() == null) ? 0 : getBaseDeviceTypeId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", deviceTypeId=").append(deviceTypeId);
        sb.append(", baseDeviceTypeId=").append(baseDeviceTypeId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}