package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jkzz.smart_mines.enumerate.impl.ControlModeEnum;
import com.jkzz.smart_mines.enumerate.impl.OperationResultEnum;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName log_operation
 */
@TableName(value = "log_operation")
@Data
@Builder
public class LogOperation implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 操作记录id
     */
    @TableId(type = IdType.AUTO)
    private Long logOperationId;
    /**
     * 设备id
     */
    private Integer deviceId;
    /**
     * 基础设备参数id
     */
    private Integer baseDeviceTypeParameterId;
    /**
     * 操作用户id
     */
    private Integer userId;
    /**
     * 远程/就地
     */
    private ControlModeEnum controlMode;
    /**
     * 操作后的值
     */
    private String operationValue;
    /**
     * 操作结果
     */
    private OperationResultEnum operationResult;
    /**
     * 操作时间戳
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
        LogOperation other = (LogOperation) that;
        return (this.getLogOperationId() == null ? other.getLogOperationId() == null : this.getLogOperationId().equals(other.getLogOperationId()))
                && (this.getDeviceId() == null ? other.getDeviceId() == null : this.getDeviceId().equals(other.getDeviceId()))
                && (this.getBaseDeviceTypeParameterId() == null ? other.getBaseDeviceTypeParameterId() == null : this.getBaseDeviceTypeParameterId().equals(other.getBaseDeviceTypeParameterId()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getControlMode() == null ? other.getControlMode() == null : this.getControlMode().equals(other.getControlMode()))
                && (this.getOperationValue() == null ? other.getOperationValue() == null : this.getOperationValue().equals(other.getOperationValue()))
                && (this.getTimeMillis() == null ? other.getTimeMillis() == null : this.getTimeMillis().equals(other.getTimeMillis()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLogOperationId() == null) ? 0 : getLogOperationId().hashCode());
        result = prime * result + ((getDeviceId() == null) ? 0 : getDeviceId().hashCode());
        result = prime * result + ((getBaseDeviceTypeParameterId() == null) ? 0 : getBaseDeviceTypeParameterId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getControlMode() == null) ? 0 : getControlMode().hashCode());
        result = prime * result + ((getOperationValue() == null) ? 0 : getOperationValue().hashCode());
        result = prime * result + ((getTimeMillis() == null) ? 0 : getTimeMillis().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", logOperationId=").append(logOperationId);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", baseDeviceParameterId=").append(baseDeviceTypeParameterId);
        sb.append(", userId=").append(userId);
        sb.append(", controlMode=").append(controlMode);
        sb.append(", operationValue=").append(operationValue);
        sb.append(", timeMillis=").append(timeMillis);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}