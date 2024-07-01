package com.leonardo.tests.misctests.infrastructure.service.cache;

import static com.leonardo.tests.misctests.infrastructure.config.cache.MemcachedCacheConfig.CACHE_MANAGER;

import com.leonardo.tests.misctests.infrastructure.config.cache.MemcachedCacheConfig;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemCachedCacheService extends AbstractCacheService {

    @Qualifier(CACHE_MANAGER)
    private final CacheManager memcachedCacheManager;

    @Override
    public CacheManager getCacheManager() {
        return this.memcachedCacheManager;
    }

    @Override
    public String getCacheName() {
        return MemcachedCacheConfig.CACHE_NAME;
    }

    @Override
    @CacheException
    public void put(String key, Object value, long seconds) {
        final boolean hasFailedBack = memcachedCacheManager instanceof ConcurrentMapCacheManager;
        final String MEMCACHED_SEPARATOR = "#";
        final String cacheName = hasFailedBack ? MemcachedCacheConfig.CACHE_NAME
            : MemcachedCacheConfig.CACHE_NAME.concat(
                MEMCACHED_SEPARATOR).concat(String.valueOf(seconds));
        var cache = memcachedCacheManager.getCache(cacheName);
        Objects.requireNonNull(cache).put(key, value);
    }
}
