package com.jkzz.smart_mines.aop;

import com.jkzz.smart_mines.annotate.RequestDebounceLock;
import com.jkzz.smart_mines.annotate.RequestKeyParam;
import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.exception.AppException;
import com.jkzz.smart_mines.service.LockTableService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Configuration
@RequiredArgsConstructor
@Order(2)
public class RequestDebounceLockAspect {

    private final LockTableService lockTableService;

    @Around("execution(public * * (..)) && @annotation(com.jkzz.smart_mines.annotate.RequestDebounceLock)")
    public Object interceptor(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RequestDebounceLock requestLock = method.getAnnotation(RequestDebounceLock.class);
        if (StringUtils.isEmpty(requestLock.prefix())) {
            throw new AppException(AppExceptionCodeMsg.QUIVER_REDIS_PREFIX_ERROR);
        }
        // 获取自定义key
        final String lockKey = getLockKey(joinPoint);
        boolean isLocked;
        // 获取锁
        synchronized (this) {
            isLocked = lockTableService.lock(lockKey);
        }
        // 没有拿到锁说明已经有了请求了
        if (!isLocked) {
            throw new AppException(AppExceptionCodeMsg.QUIVER_FREQUENT);
        }
        lockTableService.lockTime(lockKey, requestLock.expire(), requestLock.timeUnit());
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new AppException(AppExceptionCodeMsg.ERR_SYSTEM);
        }
    }

    private String getLockKey(ProceedingJoinPoint joinPoint) {
        // 获取连接点的方法签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // Method对象
        Method method = signature.getMethod();
        // 获取Method对象上的注解对象
        RequestDebounceLock requestLock = method.getAnnotation(RequestDebounceLock.class);
        // 获取方法参数
        final Object[] args = joinPoint.getArgs();
        // 获取Method对象上所有的注解
        final Parameter[] parameters = method.getParameters();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            final RequestKeyParam keyParam = parameters[i].getAnnotation(RequestKeyParam.class);
            // 如果属性不是RequestRedisKeyParam注解，则不处理
            if (null == keyParam) {
                continue;
            }
            sb.append(requestLock.delimiter()).append(args[i]);
        }
        // 如果方法上没有RequestRedisKeyParam注解
        if (StringUtils.isEmpty(sb.toString())) {
            // 获取方法上面多个注解（为什么是两层数组，因为第一层数组是只有一个元素的数组）
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            // 循环注解
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                // 获取注解类中所有的属性字段
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    // 判断字段上是否有RequestRedisKeyParam注解
                    final RequestKeyParam annotation = field.getAnnotation(RequestKeyParam.class);
                    // 如果没有，跳过
                    if (null == annotation) {
                        continue;
                    }
                    // 如果有，设置Accessible为true（为true时可以使用反射访问私有变量，否则不能访问私有变量）
                    field.setAccessible(true);
                    sb.append(requestLock.delimiter()).append(ReflectionUtils.getField(field, object));
                }
            }
        }
        // 返回指定前缀的key
        return requestLock.prefix() + sb;
    }

}
