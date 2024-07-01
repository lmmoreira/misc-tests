package com.leonardo.tests.misctests.infrastructure.service.cache;

import static com.leonardo.tests.misctests.infrastructure.config.cache.RedisCacheConfig.CACHE_MANAGER;

import com.leonardo.tests.misctests.infrastructure.config.cache.RedisCacheConfig;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCacheService extends AbstractCacheService {

    @Qualifier(CACHE_MANAGER)
    private final CacheManager redisCacheManager;
    private final Optional<RedisTemplate<String, Object>> redisTemplate;

    @Override
    public CacheManager getCacheManager() {
        return this.redisCacheManager;
    }

    @Override
    public String getCacheName() {
        return RedisCacheConfig.CACHE_NAME;
    }

    @Override
    @CacheException
    public void put(String key, Object value, long seconds) {
        final boolean hasFailedBack = redisCacheManager instanceof ConcurrentMapCacheManager;

        if (hasFailedBack) {
            final Cache cache = redisCacheManager.getCache(RedisCacheConfig.CACHE_NAME);
            Objects.requireNonNull(cache).put(key, value);
        } else if (redisTemplate.isPresent()) {
            final String PREFIX_SEPARATOR = "::";
            final String redisKey = RedisCacheConfig.CACHE_NAME.concat(PREFIX_SEPARATOR) + key;
            redisTemplate.get().opsForValue().set(redisKey, value, seconds, TimeUnit.SECONDS);
        }
    }

}
