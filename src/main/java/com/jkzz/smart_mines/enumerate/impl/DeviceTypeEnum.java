package com.jkzz.smart_mines.enumerate.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum DeviceTypeEnum {

    智能风门(1, "fengMen"),
    智能风门_百叶风窗(2, "fengMenBaiYe"),
    智能风门_推拉风窗(3, "fengMenTuiLa"),
    智能风窗_单(4, "fengChuangDan"),
    智能风窗_双(5, "fengChuangShuang"),
    精准测风(6, "ceFeng"),
    综采喷雾(7, "zongCai"),
    多功能喷雾(8, "penWu"),
    分风器(9, "fenFengQi"),
    防火门(10, "fangHuoMen"),
    副井口风门(11, "fuJingKouFengMen"),
    ;

    private static final Map<Integer, DeviceTypeEnum> map = new HashMap<>(10);
    private final Integer value;
    private final String name;

    public static DeviceTypeEnum getByValue(Integer value) {
        if (map.isEmpty()) {
            for (DeviceTypeEnum deviceTypeEnum : DeviceTypeEnum.values()) {
                map.put(deviceTypeEnum.value, deviceTypeEnum);
            }
        }
        return map.get(value);
    }

}
