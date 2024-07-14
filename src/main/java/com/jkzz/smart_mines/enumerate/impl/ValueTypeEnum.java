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
public enum ValueTypeEnum implements NameValueEnum<Integer> {

    BOOL(1, "布尔"),
    //INT(2, "有符号16位整数"),
    UINT(3, "无符号16位整数"),
    ;

    private static final Map<Integer, ValueTypeEnum> map = new HashMap<>(3);
    private final Integer value;
    @EnumValue
    private final String name;

    @JsonCreator
    public static ValueTypeEnum unSerializer(Integer valueType) {
        if (map.isEmpty()) {
            for (ValueTypeEnum valueTypeEnum : ValueTypeEnum.values()) {
                map.put(valueTypeEnum.value, valueTypeEnum);
            }
        }
        return map.get(valueType);
    }

    @JsonValue
    public Map<String, Object> serializer() {
        return new HashMap<String, Object>() {{
            put("name", getName());
            put("value", getValue());
        }};
    }

}
