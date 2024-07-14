package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName log_signal
 */
@TableName(value = "log_signal")
@Data
@Builder
public class LogSignal implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 信号量id
     */
    @TableId(type = IdType.AUTO)
    private Long logSignalId;
    /**
     * 设备id
     */
    private Integer deviceId;
    /**
     * 基础设备参数id
     */
    private Integer baseDeviceTypeParameterId;
    /**
     * 信号量的值
     */
    private String signalValue;
    /**
     * 时间戳
     */
    private Long timeMillis;

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
        LogSignal other = (LogSignal) that;
        return (this.getLogSignalId() == null ? other.getLogSignalId() == null : this.getLogSignalId().equals(other.getLogSignalId()))
                && (this.getDeviceId() == null ? other.getDeviceId() == null : this.getDeviceId().equals(other.getDeviceId()))
                && (this.getBaseDeviceTypeParameterId() == null ? other.getBaseDeviceTypeParameterId() == null : this.getBaseDeviceTypeParameterId().equals(other.getBaseDeviceTypeParameterId()))
                && (this.getSignalValue() == null ? other.getSignalValue() == null : this.getSignalValue().equals(other.getSignalValue()))
                && (this.getTimeMillis() == null ? other.getTimeMillis() == null : this.getTimeMillis().equals(other.getTimeMillis()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLogSignalId() == null) ? 0 : getLogSignalId().hashCode());
        result = prime * result + ((getDeviceId() == null) ? 0 : getDeviceId().hashCode());
        result = prime * result + ((getBaseDeviceTypeParameterId() == null) ? 0 : getBaseDeviceTypeParameterId().hashCode());
        result = prime * result + ((getSignalValue() == null) ? 0 : getSignalValue().hashCode());
        result = prime * result + ((getTimeMillis() == null) ? 0 : getTimeMillis().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", logSignalId=").append(logSignalId);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", baseDeviceParameterId=").append(baseDeviceTypeParameterId);
        sb.append(", signalValue=").append(signalValue);
        sb.append(", timeMillis=").append(timeMillis);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}