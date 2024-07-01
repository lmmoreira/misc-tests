package com.leonardo.tests.misctests.infrastructure.service.cache;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@Order(2)
public class CacheServiceExceptionAspect {

    private final Map<Class<?>, Function<String, ?>> returnHandlers;

    public CacheServiceExceptionAspect() {
        this.returnHandlers = new HashMap<>();
        this.returnHandlers.put(Optional.class, s -> Optional.empty());
        this.returnHandlers.put(boolean.class, s -> Strings.isNotBlank(s) ? Boolean.parseBoolean(s) : null);
        this.returnHandlers.put(Boolean.class, s -> Strings.isNotBlank(s) ? Boolean.parseBoolean(s) : null);
        this.returnHandlers.put(int.class, s -> Strings.isNotBlank(s) ? Integer.parseInt(s) : null);
        this.returnHandlers.put(Integer.class, s -> Strings.isNotBlank(s) ? Integer.parseInt(s) : null);
        this.returnHandlers.put(Long.class, s -> Strings.isNotBlank(s) ? Long.parseLong(s) : null);
        this.returnHandlers.put(long.class, s -> Strings.isNotBlank(s) ? Long.parseLong(s) : null);
        this.returnHandlers.put(String.class, Function.identity());
    }

    @Around("@annotation(cacheException)")
    public Object handleException(final ProceedingJoinPoint joinPoint,
        final CacheException cacheException) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (final Exception e) {
            final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            final Method method = signature.getMethod();
            final Class<?> returnType = method.getReturnType();

            log.error("[CacheService] Error on cache functions {}", e.toString());

            return Optional.ofNullable(returnHandlers.get(returnType))
                .map(handler -> handler.apply(cacheException.defaultValue()))
                .orElse(null);
        }
    }
}