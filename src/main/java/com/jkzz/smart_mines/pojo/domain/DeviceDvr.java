package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @TableName device_dvr
 */
@TableName(value = "device_dvr")
@Data
public class DeviceDvr implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 摄像头id
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message = "请选择摄像头", groups = {UpdateGroup.class, DeleteGroup.class})
    @Null(message = "摄像头id不能自定义", groups = InsertGroup.class)
    private Integer dvrId;
    /**
     * 设备id
     */
    @NotNull(message = "请选择设备", groups = {InsertGroup.class, UpdateGroup.class, SelectGroup.class})
    private Integer deviceId;
    /**
     * ip地址
     */
    @Size(max = 20, message = "ip地址最大为20位", groups = {InsertGroup.class, UpdateGroup.class})
    @NotBlank(message = "ip地址不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private String ip;
    /**
     * 端口号
     */
    @NotNull(message = "端口号不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private Integer port;
    /**
     * 用户名
     */
    @Size(max = 20, message = "用户名最大为20位", groups = {InsertGroup.class, UpdateGroup.class})
    @NotBlank(message = "用户名不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private String userName;
    /**
     * 密码
     */
    @Size(max = 20, message = "密码最大为20位", groups = {InsertGroup.class, UpdateGroup.class})
    @NotBlank(message = "密码不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private String password;
    /**
     * 登录状态
     */
    @TableField(exist = false)
    private String loginStatus;
    /**
     * 登录信息
     */
    @TableField(exist = false)
    private String loginMessage;
    /**
     * 预览状态
     */
    @TableField(exist = false)
    private String playStatus;
    /**
     * 预览信息
     */
    @TableField(exist = false)
    private String playMessage;
    /**
     * 用户句柄
     */
    @TableField(exist = false)
    private Integer lUserId;
    /**
     * 预览句柄
     */
    @TableField(exist = false)
    private Integer lPlayId;
    /**
     * 通道号
     */
    @TableField(exist = false)
    private Integer lDChannel;

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
        DeviceDvr other = (DeviceDvr) that;
        return (this.getDvrId() == null ? other.getDvrId() == null : this.getDvrId().equals(other.getDvrId()))
                && (this.getDeviceId() == null ? other.getDeviceId() == null : this.getDeviceId().equals(other.getDeviceId()))
                && (this.getIp() == null ? other.getIp() == null : this.getIp().equals(other.getIp()))
                && (this.getPort() == null ? other.getPort() == null : this.getPort().equals(other.getPort()))
                && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
                && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDvrId() == null) ? 0 : getDvrId().hashCode());
        result = prime * result + ((getDeviceId() == null) ? 0 : getDeviceId().hashCode());
        result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
        result = prime * result + ((getPort() == null) ? 0 : getPort().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", dvrId=").append(dvrId);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", ip=").append(ip);
        sb.append(", port=").append(port);
        sb.append(", userName=").append(userName);
        sb.append(", password=").append(password);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}