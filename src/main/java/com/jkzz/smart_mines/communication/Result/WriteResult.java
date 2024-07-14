package com.jkzz.smart_mines.communication.Result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor
public class WriteResult implements Serializable {

    private boolean isSuccess;

    private String parameterName;

    private String writeValue;

    private String message;

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setWriteValue(String writeValue) {
        this.writeValue = writeValue;
    }
}
