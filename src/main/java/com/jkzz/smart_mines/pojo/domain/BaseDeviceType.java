package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName base_device_type
 */
@TableName(value = "base_device_type")
@Data
public class BaseDeviceType implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 基础设备类型id
     */
    @TableId(type = IdType.AUTO)
    private Integer baseDeviceTypeId;
    /**
     * 基础设备类型名称
     */
    private String baseDeviceTypeName;

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
        BaseDeviceType other = (BaseDeviceType) that;
        return (this.getBaseDeviceTypeId() == null ? other.getBaseDeviceTypeId() == null : this.getBaseDeviceTypeId().equals(other.getBaseDeviceTypeId()))
                && (this.getBaseDeviceTypeName() == null ? other.getBaseDeviceTypeName() == null : this.getBaseDeviceTypeName().equals(other.getBaseDeviceTypeName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getBaseDeviceTypeId() == null) ? 0 : getBaseDeviceTypeId().hashCode());
        result = prime * result + ((getBaseDeviceTypeName() == null) ? 0 : getBaseDeviceTypeName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", baseDeviceTypeId=").append(baseDeviceTypeId);
        sb.append(", baseDeviceTypeName=").append(baseDeviceTypeName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}