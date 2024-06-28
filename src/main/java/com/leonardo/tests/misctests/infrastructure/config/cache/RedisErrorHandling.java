package com.leonardo.tests.misctests.infrastructure.config.cache;

import static java.lang.String.format;

import io.lettuce.core.RedisCommandTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class RedisErrorHandling implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(
        final RuntimeException exception,
        final Cache cache,
        final Object key) {
        this.processExceptions(exception);
    }

    @Override
    public void handleCachePutError(
        final RuntimeException exception,
        final Cache cache,
        final Object key,
        final Object value) {
        this.processExceptions(exception);
    }

    @Override
    public void handleCacheEvictError(
        final RuntimeException exception,
        final Cache cache,
        final Object key) {
        this.processExceptions(exception);
    }

    @Override
    public void handleCacheClearError(
        final RuntimeException exception, final Cache cache) {
        this.processExceptions(exception);
    }

    private void processExceptions(final RuntimeException exception) {
        if (exception.getCause() instanceof RedisCommandTimeoutException) {
            final String errorMessage =
                format(
                    "[REDIS] Error connecting to redis with errorMessage '%s'",
                    exception.getMessage());
            log.error(errorMessage, exception);
        } else {
            final String errorMessage =
                format("[REDIS] exception caught with errorMessage '%s'", exception.getMessage());
            log.warn(errorMessage, exception);
        }
    }
}
