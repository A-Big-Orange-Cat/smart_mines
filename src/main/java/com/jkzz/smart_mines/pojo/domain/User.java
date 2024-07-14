package com.jkzz.smart_mines.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jkzz.smart_mines.enumerate.impl.RoleEnum;
import com.jkzz.smart_mines.verification.group.DeleteGroup;
import com.jkzz.smart_mines.verification.group.InsertGroup;
import com.jkzz.smart_mines.verification.group.LoginGroup;
import com.jkzz.smart_mines.verification.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message = "请选择用户", groups = {UpdateGroup.class, DeleteGroup.class})
    @Null(message = "用户id不能自定义", groups = InsertGroup.class)
    private Integer userId;
    /**
     * 用户名
     */
    @Size(max = 20, message = "用户名最大为20位", groups = {InsertGroup.class, UpdateGroup.class, LoginGroup.class})
    @NotBlank(message = "用户名不能为空", groups = {InsertGroup.class, UpdateGroup.class, LoginGroup.class})
    private String userName;
    /**
     * 用户真实姓名
     */
    @Size(max = 10, message = "真实姓名最大为10位", groups = {InsertGroup.class, UpdateGroup.class})
    @NotBlank(message = "用户真实姓名不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private String userTrueName;
    /**
     * 用户密码
     */
    @Size(max = 20, message = "密码最大为20位", groups = {InsertGroup.class, UpdateGroup.class, LoginGroup.class})
    @NotBlank(message = "用户密码不能为空", groups = {InsertGroup.class, UpdateGroup.class, LoginGroup.class})
    private String password;
    /**
     * 角色：用户/管理员/超级管理员
     */
    @NotNull(message = "用户权限必须为指定值：[0:用户, 1:管理员]", groups = {InsertGroup.class, UpdateGroup.class})
    private RoleEnum role;

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
        User other = (User) that;
        return (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
                && (this.getUserTrueName() == null ? other.getUserTrueName() == null : this.getUserTrueName().equals(other.getUserTrueName()))
                && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
                && (this.getRole() == null ? other.getRole() == null : this.getRole().equals(other.getRole()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getUserTrueName() == null) ? 0 : getUserTrueName().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getRole() == null) ? 0 : getRole().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", userName=").append(userName);
        sb.append(", userTrueName=").append(userTrueName);
        sb.append(", password=").append(password);
        sb.append(", role=").append(role);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}