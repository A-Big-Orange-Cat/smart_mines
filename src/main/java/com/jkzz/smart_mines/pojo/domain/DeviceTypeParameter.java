package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jkzz.smart_mines.verification.group.SelectGroup;
import com.jkzz.smart_mines.verification.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @TableName device_type_parameter
 */
@TableName(value = "device_type_parameter")
@Data
public class DeviceTypeParameter implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 设备类型参数id
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message = "请选择参数", groups = UpdateGroup.class)
    private Integer deviceTypeParameterId;
    /**
     * 设备类型id
     */
    @NotNull(message = "请选择设备类型", groups = {UpdateGroup.class, SelectGroup.class})
    private Integer deviceTypeId;
    /**
     * 设备类型参数名
     */
    private String deviceTypeParameterName;
    /**
     * 设备类型参数代码
     */
    private String deviceTypeParameterCode;
    /**
     * 值
     */
    @NotBlank(message = "更改的值不能为空", groups = UpdateGroup.class)
    private String value;
    /**
     * 序号
     */
    private Integer sortNumber;

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
        DeviceTypeParameter other = (DeviceTypeParameter) that;
        return (this.getDeviceTypeParameterId() == null ? other.getDeviceTypeParameterId() == null : this.getDeviceTypeParameterId().equals(other.getDeviceTypeParameterId()))
                && (this.getDeviceTypeId() == null ? other.getDeviceTypeId() == null : this.getDeviceTypeId().equals(other.getDeviceTypeId()))
                && (this.getDeviceTypeParameterName() == null ? other.getDeviceTypeParameterName() == null : this.getDeviceTypeParameterName().equals(other.getDeviceTypeParameterName()))
                && (this.getDeviceTypeParameterCode() == null ? other.getDeviceTypeParameterCode() == null : this.getDeviceTypeParameterCode().equals(other.getDeviceTypeParameterCode()))
                && (this.getValue() == null ? other.getValue() == null : this.getValue().equals(other.getValue()))
                && (this.getSortNumber() == null ? other.getSortNumber() == null : this.getSortNumber().equals(other.getSortNumber()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDeviceTypeParameterId() == null) ? 0 : getDeviceTypeParameterId().hashCode());
        result = prime * result + ((getDeviceTypeId() == null) ? 0 : getDeviceTypeId().hashCode());
        result = prime * result + ((getDeviceTypeParameterName() == null) ? 0 : getDeviceTypeParameterName().hashCode());
        result = prime * result + ((getDeviceTypeParameterCode() == null) ? 0 : getDeviceTypeParameterCode().hashCode());
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
        result = prime * result + ((getSortNumber() == null) ? 0 : getSortNumber().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", deviceTypeParameterId=").append(deviceTypeParameterId);
        sb.append(", deviceTypeId=").append(deviceTypeId);
        sb.append(", deviceTypeParameterName=").append(deviceTypeParameterName);
        sb.append(", deviceTypeParameterCode=").append(deviceTypeParameterCode);
        sb.append(", value=").append(value);
        sb.append(", sortNumber=").append(sortNumber);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}