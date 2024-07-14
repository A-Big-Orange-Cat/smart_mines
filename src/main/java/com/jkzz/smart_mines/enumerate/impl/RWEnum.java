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
public enum RWEnum implements NameValueEnum<Integer> {

    RW(1, "读写"),
    ONLY_R(2, "只读"),
    ONLY_W(3, "只写"),
    ;

    private static final Map<Integer, RWEnum> map = new HashMap<>(3);
    private final Integer value;
    @EnumValue
    private final String name;

    @JsonCreator
    public static RWEnum unSerializer(Integer rw) {
        if (map.isEmpty()) {
            for (RWEnum rwEnum : RWEnum.values()) {
                map.put(rwEnum.value, rwEnum);
            }
        }
        return map.get(rw);
    }

    @JsonValue
    public Map<String, Object> serializer() {
        return new HashMap<String, Object>() {{
            put("name", getName());
            put("value", getValue());
        }};
    }

}
