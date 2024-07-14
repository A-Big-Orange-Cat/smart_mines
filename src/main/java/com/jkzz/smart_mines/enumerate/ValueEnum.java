package com.jkzz.smart_mines.enumerate;

import java.io.Serializable;

/**
 * 只含枚举值的枚举
 *
 * @param <T>
 */
public interface ValueEnum<T> extends Serializable {

    /**
     * 获取枚举值
     *
     * @return 枚举值
     */
    T getValue();

}
