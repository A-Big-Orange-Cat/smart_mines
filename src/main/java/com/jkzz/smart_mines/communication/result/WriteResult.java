package com.jkzz.smart_mines.communication.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class WriteResult implements Serializable {

    private boolean isSuccess;

    private String parameterName;

    private String writeValue;

    private String message;

}
