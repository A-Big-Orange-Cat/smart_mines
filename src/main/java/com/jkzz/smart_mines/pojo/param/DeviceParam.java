package com.jkzz.smart_mines.pojo.param;

import com.jkzz.smart_mines.verification.group.DeleteGroup;
import com.jkzz.smart_mines.verification.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class DeviceParam implements Serializable {

    /**
     * 设备id
     */
    @NotNull(message = "请选择设备", groups = {UpdateGroup.class, DeleteGroup.class})
    private Integer deviceId;

    /**
     * 设备类型id
     */
    private Integer deviceTypeId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备位置
     */
    @Size(max = 20, message = "设备位置最大为20位", groups = UpdateGroup.class)
    @NotBlank(message = "请选择设备位置", groups = UpdateGroup.class)
    private String deviceLocation;

}
