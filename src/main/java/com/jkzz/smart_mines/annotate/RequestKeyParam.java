package com.jkzz.smart_mines.annotate;

import java.lang.annotation.*;

/**
 * 自定义分布式锁key的参数注解
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RequestKeyParam {
}
