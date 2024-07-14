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
public enum CommunicationProtocolEnum implements NameValueEnum<Integer> {

    OPC(1, "OPC"),
    MODBUS_TCP(2, "Modbus TCP"),
    S7(3, "S7"),
    ;

    private static final Map<Integer, CommunicationProtocolEnum> map = new HashMap<>(3);
    private final Integer value;
    @EnumValue
    private final String name;

    @JsonCreator
    public static CommunicationProtocolEnum unSerializer(Integer communicationProtocol) {
        if (map.isEmpty()) {
            for (CommunicationProtocolEnum communicationProtocolEnum : CommunicationProtocolEnum.values()) {
                map.put(communicationProtocolEnum.value, communicationProtocolEnum);
            }
        }
        return map.get(communicationProtocol);
    }

    @JsonValue
    public Map<String, Object> serializer() {
        return new HashMap<String, Object>() {{
            put("name", getName());
            put("value", getValue());
        }};
    }

}
