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

@Component(MemCachedCacheService.BEAN_ID)
@RequiredArgsConstructor
public class MemCachedCacheService extends AbstractCacheService {

    public static final String BEAN_ID = "memCachedCacheService";

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

        final String MEMCACHED_SEPARATOR = "#";
        var cache = memcachedCacheManager.get().getCache(getCacheName());
        Objects.requireNonNull(cache).put(key.concat(MEMCACHED_SEPARATOR).concat(String.valueOf(seconds)), value);
    }
}
