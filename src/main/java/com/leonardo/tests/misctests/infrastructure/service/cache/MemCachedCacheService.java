package com.leonardo.tests.misctests.infrastructure.service.cache;

import static com.leonardo.tests.misctests.infrastructure.config.cache.MemcachedCacheConfig.CACHE_MANAGER;

import com.leonardo.tests.misctests.infrastructure.config.cache.MemcachedCacheConfig;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemCachedCacheService extends AbstractCacheService {

    @Qualifier(CACHE_MANAGER)
    private final Optional<CacheManager> memcachedCacheManager;

    @Override
    public Optional<CacheManager> getCacheManager() {
        return this.memcachedCacheManager;
    }

    @Override
    public String getCacheName() {
        return MemcachedCacheConfig.CACHE_NAME;
    }

    @Override
    @CacheException
    public void put(String key, Object value, long seconds) {
        if (memcachedCacheManager.isEmpty()) {
            return;
        }

        final boolean hasFailedBack = memcachedCacheManager.get() instanceof ConcurrentMapCacheManager;
        final String MEMCACHED_SEPARATOR = "#";
        final String cacheName = hasFailedBack ? MemcachedCacheConfig.CACHE_NAME
            : MemcachedCacheConfig.CACHE_NAME.concat(
                MEMCACHED_SEPARATOR).concat(String.valueOf(seconds));
        var cache = memcachedCacheManager.get().getCache(cacheName);
        Objects.requireNonNull(cache).put(key, value);
    }
}
