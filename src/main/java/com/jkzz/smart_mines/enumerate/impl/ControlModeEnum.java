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
public enum ControlModeEnum implements NameValueEnum<Boolean> {

    LOCAL(false, "就地"),
    REMOTE(true, "远程"),
    ;

    private static final Map<Boolean, ControlModeEnum> map = new HashMap<>(2);
    private final Boolean value;
    @EnumValue
    private final String name;

    @JsonCreator
    public static ControlModeEnum unSerializer(Boolean controlMode) {
        if (map.isEmpty()) {
            for (ControlModeEnum controlModeEnum : ControlModeEnum.values()) {
                map.put(controlModeEnum.value, controlModeEnum);
            }
        }
        return map.get(controlMode);
    }

    @JsonValue
    public Map<String, Object> serializer() {
        return new HashMap<String, Object>() {{
            put("name", getName());
            put("value", getValue());
        }};
    }

}
