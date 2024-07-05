package com.leonardo.tests.misctests.infrastructure.service.cache;

import static com.leonardo.tests.misctests.infrastructure.config.cache.RedisCacheConfig.CACHE_MANAGER;

import com.leonardo.tests.misctests.infrastructure.config.cache.RedisCacheConfig;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component(RedisCacheService.BEAN_ID)
@RequiredArgsConstructor
public class RedisCacheService extends AbstractCacheService {

    public static final String BEAN_ID = "redisCacheService";

    @Qualifier(CACHE_MANAGER)
    private final Optional<CacheManager> redisCacheManager;
    private final Optional<RedisTemplate<String, Object>> redisTemplate;

    @Override
    public Optional<CacheManager> getCacheManager() {
        return this.redisCacheManager;
    }

    @Override
    public String getCacheName() {
        return RedisCacheConfig.CACHE_NAME;
    }

    @Override
    @CacheException
    public void put(String key, Object value, long seconds) {
        if (redisCacheManager.isEmpty()) {
            return;
        }

        final boolean hasFailedBack = redisTemplate.isEmpty();

        if (hasFailedBack) {
            final String CAFFEINE_SEPARATOR = "#";
            var cache = redisCacheManager.get().getCache(getCacheName());
            Objects.requireNonNull(cache).put(key.concat(CAFFEINE_SEPARATOR).concat(String.valueOf(seconds)), value);
        } else {
            final String PREFIX_SEPARATOR = "::";
            final String redisKey = RedisCacheConfig.CACHE_NAME.concat(PREFIX_SEPARATOR) + key;
            redisTemplate.get().opsForValue().set(redisKey, value, seconds, TimeUnit.SECONDS);
        }
    }

}
