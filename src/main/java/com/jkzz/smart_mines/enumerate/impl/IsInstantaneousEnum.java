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
public enum IsInstantaneousEnum implements NameValueEnum<Boolean> {

    IS_INSTANTANEOUS(true, "是"),
    NON_INSTANTANEOUS(false, "否"),
    ;
    private static final Map<Boolean, IsInstantaneousEnum> map = new HashMap<>(2);
    private final Boolean value;
    @EnumValue
    private final String name;

    @JsonCreator
    public static IsInstantaneousEnum unSerializer(Boolean isInstantaneous) {
        if (map.isEmpty()) {
            for (IsInstantaneousEnum instantaneousEnum : IsInstantaneousEnum.values()) {
                map.put(instantaneousEnum.value, instantaneousEnum);
            }
        }
        return map.get(isInstantaneous);
    }

    @JsonValue
    @Override
    public Map<String, Object> serializer() {
        Map<String, Object> serializerMap = new HashMap<>();
        serializerMap.put("name", getName());
        serializerMap.put("value", getValue());
        return serializerMap;
    }
}
