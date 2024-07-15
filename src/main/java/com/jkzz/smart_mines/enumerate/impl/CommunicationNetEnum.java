package com.jkzz.smart_mines.enumerate.impl;

import com.jkzz.smart_mines.enumerate.NameValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum CommunicationNetEnum implements NameValueEnum<String> {

    OPC("OPC", "allenBradleyNet"),
    MODBUS_TCP("Modbus TCP", "modbusTcpNet"),
    S7("S7", "siemensS7Net"),
    ;

    private final String value;

    private final String name;

    @Override
    public Map<String, Object> serializer() {
        Map<String, Object> serializerMap = new HashMap<>();
        serializerMap.put("name", getName());
        serializerMap.put("value", getValue());
        return serializerMap;
    }
}
