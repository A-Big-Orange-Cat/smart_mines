package com.jkzz.smart_mines.enumerate.impl;

import com.jkzz.smart_mines.enumerate.NameValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommunicationNetEnum implements NameValueEnum<String> {

    OPC("OPC", "allenBradleyNet"),
    MODBUS_TCP("Modbus TCP", "modbusTcpNet"),
    S7("S7", "siemensS7Net"),
    ;

    private final String value;

    private final String name;

}
