package com.jkzz.smart_mines.enumerate;

import java.util.Map;

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
     * 序列化格式方法
     *
     * @return 序列化结果数据
     */
    Map<String, Object> serializer();

    /**
     * 获取枚举描述：value:name
     */
    default String description() {
        return getValue().toString() + ":" + getName();
    }

}
