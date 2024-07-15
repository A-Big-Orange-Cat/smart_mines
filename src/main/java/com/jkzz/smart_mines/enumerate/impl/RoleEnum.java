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
public enum RoleEnum implements NameValueEnum<Integer> {

    USER(0, "用户"),
    ADMIN(1, "管理员"),
    SUPER_ADMIN(-1, "超级管理员"),
    ;

    private static final Map<Integer, RoleEnum> map = new HashMap<>(3);
    private final Integer value;
    @EnumValue
    private final String name;

    @JsonCreator
    public static RoleEnum unSerializer(Integer permission) {
        if (map.isEmpty()) {
            for (RoleEnum roleEnum : RoleEnum.values()) {
                map.put(roleEnum.value, roleEnum);
            }
        }
        return map.get(permission);
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
