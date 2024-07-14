package com.jkzz.smart_mines.enumerate.impl;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jkzz.smart_mines.enumerate.NameValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ParameterTypeEnum implements NameValueEnum<Integer> {

    PARAMETER_SETTING(1, "参数设置"),
    COMMAND(2, "操作指令"),
    SIGNAL(3, "信号"),
    ALARM_SIGNAL(4, "报警值信号"),
    ALARM(5, "报警"),
    ;

    private static final Map<Integer, ParameterTypeEnum> map = new HashMap<>(4);
    private final Integer value;
    @EnumValue
    private final String name;

    @JsonCreator
    public static ParameterTypeEnum unSerializer(Integer parameterType) {
        if (map.isEmpty()) {
            for (ParameterTypeEnum parameterTypeEnum : ParameterTypeEnum.values()) {
                map.put(parameterTypeEnum.value, parameterTypeEnum);
            }
        }
        return map.get(parameterType);
    }

    @JsonValue
    public Map<String, Object> serializer() {
        return new HashMap<String, Object>() {{
            put("name", getName());
            put("value", getValue());
        }};
    }

}
