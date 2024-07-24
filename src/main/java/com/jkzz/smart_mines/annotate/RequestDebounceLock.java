package com.jkzz.smart_mines.annotate;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义防重复提交注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RequestDebounceLock {

    /**
     * redis锁前缀（接口方法名）
     */
    String prefix() default "";

    /**
     * redis锁过期时间，默认1秒
     */
    int expire() default 1;

    /**
     * redis锁过期时间单位，默认单位为秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * redis key分隔符
     */
    String delimiter() default "@";

}
