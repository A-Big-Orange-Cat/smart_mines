package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jkzz.smart_mines.enumerate.impl.EnableStatusEnum;
import com.jkzz.smart_mines.verification.group.DeleteGroup;
import com.jkzz.smart_mines.verification.group.InsertGroup;
import com.jkzz.smart_mines.verification.group.SelectGroup;
import com.jkzz.smart_mines.verification.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @TableName device
 */
@TableName(value = "device")
@Data
public class Device implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 设备id
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message = "请选择设备", groups = {UpdateGroup.class, DeleteGroup.class})
    @Null(message = "设备id不能自定义", groups = InsertGroup.class)
    private Integer deviceId;
    /**
     * 设备类型id
     */
    @NotNull(message = "请选择设备类型", groups = {InsertGroup.class, UpdateGroup.class})
    private Integer deviceTypeId;
    /**
     * 设备名称
     */
    @Size(max = 20, message = "设备名称最大为20位", groups = {InsertGroup.class, UpdateGroup.class})
    @NotBlank(message = "设备名称不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private String deviceName;
    /**
     * ip地址
     */
    @Size(max = 20, message = "ip地址最大为20位", groups = {InsertGroup.class, UpdateGroup.class})
    @NotBlank(message = "ip地址不能为空", groups = {InsertGroup.class, UpdateGroup.class, SelectGroup.class})
    private String ip;
    /**
     * 端口号
     */
    @NotNull(message = "端口号不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private Integer port;
    /**
     * 设备位置
     */
    @Size(max = 20, message = "设备位置最大为20位", groups = UpdateGroup.class)
    private String deviceLocation;
    /**
     * 禁用状态：禁用/启用
     */
    @NotNull(message = "启用/禁用状态必须为指定值：[1:启用, 2:禁用]", groups = {UpdateGroup.class})
    private EnableStatusEnum enableStatus;
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
        Device other = (Device) that;
        return (this.getDeviceId() == null ? other.getDeviceId() == null : this.getDeviceId().equals(other.getDeviceId()))
                && (this.getDeviceTypeId() == null ? other.getDeviceTypeId() == null : this.getDeviceTypeId().equals(other.getDeviceTypeId()))
                && (this.getDeviceName() == null ? other.getDeviceName() == null : this.getDeviceName().equals(other.getDeviceName()))
                && (this.getIp() == null ? other.getIp() == null : this.getIp().equals(other.getIp()))
                && (this.getPort() == null ? other.getPort() == null : this.getPort().equals(other.getPort()))
                && (this.getDeviceLocation() == null ? other.getDeviceLocation() == null : this.getDeviceLocation().equals(other.getDeviceLocation()))
                && (this.getEnableStatus() == null ? other.getEnableStatus() == null : this.getEnableStatus().equals(other.getEnableStatus()))
                && (this.getSortNumber() == null ? other.getSortNumber() == null : this.getSortNumber().equals(other.getSortNumber()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDeviceId() == null) ? 0 : getDeviceId().hashCode());
        result = prime * result + ((getDeviceTypeId() == null) ? 0 : getDeviceTypeId().hashCode());
        result = prime * result + ((getDeviceName() == null) ? 0 : getDeviceName().hashCode());
        result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
        result = prime * result + ((getPort() == null) ? 0 : getPort().hashCode());
        result = prime * result + ((getDeviceLocation() == null) ? 0 : getDeviceLocation().hashCode());
        result = prime * result + ((getEnableStatus() == null) ? 0 : getEnableStatus().hashCode());
        result = prime * result + ((getSortNumber() == null) ? 0 : getSortNumber().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", deviceId=").append(deviceId);
        sb.append(", deviceTypeId=").append(deviceTypeId);
        sb.append(", deviceName=").append(deviceName);
        sb.append(", ip=").append(ip);
        sb.append(", port=").append(port);
        sb.append(", deviceLocation=").append(deviceLocation);
        sb.append(", enableStatus=").append(enableStatus);
        sb.append(", sortNumber=").append(sortNumber);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}