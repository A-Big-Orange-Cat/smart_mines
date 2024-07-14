package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jkzz.smart_mines.enumerate.impl.IsInstantaneousEnum;
import com.jkzz.smart_mines.enumerate.impl.ParameterTypeEnum;
import com.jkzz.smart_mines.enumerate.impl.RWEnum;
import com.jkzz.smart_mines.enumerate.impl.ValueTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName base_device_type_parameter
 */
@TableName(value = "base_device_type_parameter")
@Data
public class BaseDeviceTypeParameter implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 基础设备类型参数id
     */
    @TableId(type = IdType.AUTO)
    private Integer baseDeviceTypeParameterId;
    /**
     * 基础设备类型id
     */
    private Integer baseDeviceTypeId;
    /**
     * 基础设备类型参数名
     */
    private String baseDeviceTypeParameterName;
    /**
     * 基础设备类型参数代码
     */
    private String baseDeviceTypeParameterCode;
    /**
     * 数值类型
     */
    private ValueTypeEnum valueType;
    /**
     * 参数地址
     */
    private String address;
    /**
     * 比例值
     */
    private Integer proportion;
    /**
     * 读写
     */
    private RWEnum rw;
    /**
     * 是否瞬时量
     */
    private IsInstantaneousEnum isInstantaneous;
    /**
     * 参数类型
     */
    private ParameterTypeEnum type;
    /**
     * 参数单位
     */
    private String unit;
    /**
     * 参数说明
     */
    private String info;
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
        BaseDeviceTypeParameter other = (BaseDeviceTypeParameter) that;
        return (this.getBaseDeviceTypeParameterId() == null ? other.getBaseDeviceTypeParameterId() == null : this.getBaseDeviceTypeParameterId().equals(other.getBaseDeviceTypeParameterId()))
                && (this.getBaseDeviceTypeId() == null ? other.getBaseDeviceTypeId() == null : this.getBaseDeviceTypeId().equals(other.getBaseDeviceTypeId()))
                && (this.getBaseDeviceTypeParameterName() == null ? other.getBaseDeviceTypeParameterName() == null : this.getBaseDeviceTypeParameterName().equals(other.getBaseDeviceTypeParameterName()))
                && (this.getBaseDeviceTypeParameterCode() == null ? other.getBaseDeviceTypeParameterCode() == null : this.getBaseDeviceTypeParameterCode().equals(other.getBaseDeviceTypeParameterCode()))
                && (this.getValueType() == null ? other.getValueType() == null : this.getValueType().equals(other.getValueType()))
                && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
                && (this.getProportion() == null ? other.getProportion() == null : this.getProportion().equals(other.getProportion()))
                && (this.getRw() == null ? other.getRw() == null : this.getRw().equals(other.getRw()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
                && (this.getUnit() == null ? other.getUnit() == null : this.getUnit().equals(other.getUnit()))
                && (this.getInfo() == null ? other.getInfo() == null : this.getInfo().equals(other.getInfo()))
                && (this.getSortNumber() == null ? other.getSortNumber() == null : this.getSortNumber().equals(other.getSortNumber()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getBaseDeviceTypeParameterId() == null) ? 0 : getBaseDeviceTypeParameterId().hashCode());
        result = prime * result + ((getBaseDeviceTypeId() == null) ? 0 : getBaseDeviceTypeId().hashCode());
        result = prime * result + ((getBaseDeviceTypeParameterName() == null) ? 0 : getBaseDeviceTypeParameterName().hashCode());
        result = prime * result + ((getBaseDeviceTypeParameterCode() == null) ? 0 : getBaseDeviceTypeParameterCode().hashCode());
        result = prime * result + ((getValueType() == null) ? 0 : getValueType().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getProportion() == null) ? 0 : getProportion().hashCode());
        result = prime * result + ((getRw() == null) ? 0 : getRw().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getUnit() == null) ? 0 : getUnit().hashCode());
        result = prime * result + ((getInfo() == null) ? 0 : getInfo().hashCode());
        result = prime * result + ((getSortNumber() == null) ? 0 : getSortNumber().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", baseDeviceTypeParameterId=").append(baseDeviceTypeParameterId);
        sb.append(", baseDeviceTypeId=").append(baseDeviceTypeId);
        sb.append(", baseDeviceTypeParameterName=").append(baseDeviceTypeParameterName);
        sb.append(", baseDeviceTypeParameterCode=").append(baseDeviceTypeParameterCode);
        sb.append(", valueType=").append(valueType);
        sb.append(", address=").append(address);
        sb.append(", proportion=").append(proportion);
        sb.append(", rw=").append(rw);
        sb.append(", type=").append(type);
        sb.append(", unit=").append(unit);
        sb.append(", info=").append(info);
        sb.append(", sortNumber=").append(sortNumber);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}