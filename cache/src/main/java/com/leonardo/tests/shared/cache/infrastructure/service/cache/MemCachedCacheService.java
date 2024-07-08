package com.leonardo.tests.shared.cache.infrastructure.service.cache;

import static com.leonardo.tests.shared.cache.infrastructure.config.cache.memcached.MemcachedCacheConfig.CACHE_MANAGER;

import com.google.code.ssm.spring.ExtendedSSMCacheManager;
import com.leonardo.tests.shared.cache.infrastructure.config.cache.memcached.MemcachedCacheConfig;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
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

        final boolean hasFailedBack = !(memcachedCacheManager.get() instanceof ExtendedSSMCacheManager);

        if (hasFailedBack) {
            super.put(key, value, seconds);
        } else {
            final String MEMCACHED_SEPARATOR = "#";
            var cache = memcachedCacheManager.get().getCache(MemcachedCacheConfig.CACHE_NAME.concat(
                MEMCACHED_SEPARATOR).concat(String.valueOf(seconds)));
            Objects.requireNonNull(cache).put(key, value);
        }
    }

}
