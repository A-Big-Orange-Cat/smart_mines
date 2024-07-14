package com.jkzz.smart_mines.pojo.param;

import com.jkzz.smart_mines.verification.group.PlcWriteGroup;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class PlcParam implements Serializable {

    /**
     * 设备类型id
     */
    @NotNull(message = "请选择设备类型")
    private Integer deviceTypeId;

    /**
     * 设备id
     */
    @NotNull(message = "请选择设备")
    private Integer deviceId;

    /**
     * 基础设备类型参数代码数组
     */
    @NotEmpty(message = "请选择指令", groups = PlcWriteGroup.class)
    private Map<String, String> codesAndValues;

    @Tolerate
    public PlcParam() {
    }

}
