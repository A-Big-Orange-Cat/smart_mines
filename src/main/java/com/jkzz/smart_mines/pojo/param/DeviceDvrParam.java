package com.jkzz.smart_mines.pojo.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class DeviceDvrParam implements Serializable {

    /**
     * 用户句柄列表
     */
    @JsonProperty("lUserIdList")
    @NotNull(message = "请选择用户句柄列表")
    private List<Integer> lUserIdList;

}
