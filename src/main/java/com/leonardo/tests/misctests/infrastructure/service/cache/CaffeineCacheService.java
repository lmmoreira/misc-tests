package com.leonardo.tests.misctests.infrastructure.service.cache;

import static com.leonardo.tests.misctests.infrastructure.config.cache.CaffeineCacheConfig.CACHE_MANAGER;

import com.leonardo.tests.misctests.infrastructure.config.cache.CaffeineCacheConfig;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CaffeineCacheService extends AbstractCacheService {

    @Qualifier(CACHE_MANAGER)
    private final Optional<CacheManager> caffeineCacheManager;

    @Override
    public Optional<CacheManager> getCacheManager() {
        return this.caffeineCacheManager;
    }

    @Override
    public String getCacheName() {
        return CaffeineCacheConfig.CACHE_NAME;
    }

    @Override
    @CacheException
    public void put(String key, Object value, long seconds) {
        if (caffeineCacheManager.isEmpty()) {
            return;
        }

        final String CAFFEINE_SEPARATOR = "#";
        var cache = caffeineCacheManager.get().getCache(getCacheName());
        Objects.requireNonNull(cache).put(key.concat(CAFFEINE_SEPARATOR).concat(String.valueOf(seconds)), value);
    }
}
