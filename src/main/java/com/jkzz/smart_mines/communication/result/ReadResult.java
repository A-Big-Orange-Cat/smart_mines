package com.jkzz.smart_mines.communication.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor
public class ReadResult<T extends Serializable> implements Serializable {

    private boolean isSuccess;

    private T value;

    private String message;

}
