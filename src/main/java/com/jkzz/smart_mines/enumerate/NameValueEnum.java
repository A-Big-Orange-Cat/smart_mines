package com.jkzz.smart_mines.enumerate;

/**
 * 含有枚举值以及枚举名称的枚举
 *
 * @param <T>
 */
public interface NameValueEnum<T> extends ValueEnum<T> {

    /**
     * 获取枚举名称
     *
     * @return 枚举名
     */
    String getName();

    /**
     * 获取枚举描述：value:name
     */
    default String description() {
        return getValue().toString() + ":" + getName();
    }

}
