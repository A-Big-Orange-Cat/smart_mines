package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName log_alarm
 */
@TableName(value = "log_alarm")
@Data
@Builder
public class LogAlarm implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 报警记录id
     */
    @TableId(type = IdType.AUTO)
    private Long logAlarmId;
    /**
     * 设备id
     */
    private Integer deviceId;
    /**
     * 基础设备参数id
     */
    private Integer baseDeviceTypeParameterId;
    /**
     * 报警的值
     */
    private String alarmValue;
    /**
     * 报警时间戳
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
        LogAlarm other = (LogAlarm) that;
        return (this.getLogAlarmId() == null ? other.getLogAlarmId() == null : this.getLogAlarmId().equals(other.getLogAlarmId()))
                && (this.getDeviceId() == null ? other.getDeviceId() == null : this.getDeviceId().equals(other.getDeviceId()))
                && (this.getBaseDeviceTypeParameterId() == null ? other.getBaseDeviceTypeParameterId() == null : this.getBaseDeviceTypeParameterId().equals(other.getBaseDeviceTypeParameterId()))
                && (this.getAlarmValue() == null ? other.getAlarmValue() == null : this.getAlarmValue().equals(other.getAlarmValue()))
                && (this.getTimeMillis() == null ? other.getTimeMillis() == null : this.getTimeMillis().equals(other.getTimeMillis()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLogAlarmId() == null) ? 0 : getLogAlarmId().hashCode());
        result = prime * result + ((getDeviceId() == null) ? 0 : getDeviceId().hashCode());
        result = prime * result + ((getBaseDeviceTypeParameterId() == null) ? 0 : getBaseDeviceTypeParameterId().hashCode());
        result = prime * result + ((getAlarmValue() == null) ? 0 : getAlarmValue().hashCode());
        result = prime * result + ((getTimeMillis() == null) ? 0 : getTimeMillis().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", logAlarmId=").append(logAlarmId);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", baseDeviceParameterId=").append(baseDeviceTypeParameterId);
        sb.append(", alarmValue=").append(alarmValue);
        sb.append(", timeMillis=").append(timeMillis);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}