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
public enum EnableStatusEnum implements NameValueEnum<Integer> {

    ENABLE(1, "启用"),
    DISABLE(0, "禁用"),
    ;

    private static final Map<Integer, EnableStatusEnum> map = new HashMap<>(2);
    private final Integer value;
    @EnumValue
    private final String name;

    @JsonCreator
    public static EnableStatusEnum unSerializer(Integer enableStatus) {
        if (map.isEmpty()) {
            for (EnableStatusEnum enableStatusEnum : EnableStatusEnum.values()) {
                map.put(enableStatusEnum.value, enableStatusEnum);
            }
        }
        return map.get(enableStatus);
    }

    @JsonValue
    public Map<String, Object> serializer() {
        return new HashMap<String, Object>() {{
            put("name", getName());
            put("value", getValue());
        }};
    }

}
