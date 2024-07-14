package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @TableName system
 */
@TableName(value = "system")
@Data
public class System implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId
    private Integer id;
    /**
     * 系统主界面标题
     */
    @Size(max = 20, message = "系统主界面标题最大为20位")
    @NotBlank(message = "系统主界面标题不能为空")
    private String title;
    /**
     * cpu序列号
     */
    private String cpuId;
    /**
     * 地图路径
     */
    private String mapPath;
    /**
     * 本机ip+端口号
     */
    @TableField(exist = false)
    private String websocketURL;

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
        System other = (System) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
                && (this.getCpuId() == null ? other.getCpuId() == null : this.getCpuId().equals(other.getCpuId()))
                && (this.getMapPath() == null ? other.getMapPath() == null : this.getMapPath().equals(other.getMapPath()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getCpuId() == null) ? 0 : getCpuId().hashCode());
        result = prime * result + ((getMapPath() == null) ? 0 : getMapPath().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", title=").append(title);
        sb.append(", cpuId=").append(cpuId);
        sb.append(", mapPath=").append(mapPath);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}