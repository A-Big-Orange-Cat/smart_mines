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
public enum OperationResultEnum implements NameValueEnum<Boolean> {
    SUCCESS(true, "成功"),
    FAILURE(false, "失败"),
    ;
    private static final Map<Boolean, OperationResultEnum> map = new HashMap<>(2);
    private final Boolean value;
    @EnumValue
    private final String name;

    @JsonCreator
    public static OperationResultEnum unSerializer(Boolean operationResult) {
        if (map.isEmpty()) {
            for (OperationResultEnum operationResultEnum : OperationResultEnum.values()) {
                map.put(operationResultEnum.value, operationResultEnum);
            }
        }
        return map.get(operationResult);
    }

    @JsonValue
    public Map<String, Object> serializer() {
        return new HashMap<String, Object>() {{
            put("name", getName());
            put("value", getValue());
        }};
    }

}
