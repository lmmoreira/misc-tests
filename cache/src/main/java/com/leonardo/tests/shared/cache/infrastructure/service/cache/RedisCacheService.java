package com.leonardo.tests.shared.cache.infrastructure.service.cache;

import static com.leonardo.tests.shared.cache.infrastructure.config.cache.redis.RedisCacheConfig.CACHE_MANAGER;

import com.leonardo.tests.shared.cache.infrastructure.config.cache.redis.RedisCacheConfig;
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
        final boolean hasFailedBack = redisTemplate.isEmpty();

        if (hasFailedBack) {
            super.put(key, value, seconds);
        } else {
            final String PREFIX_SEPARATOR = "::";
            final String redisKey = RedisCacheConfig.CACHE_NAME.concat(PREFIX_SEPARATOR) + key;
            redisTemplate.get().opsForValue().set(redisKey, value, seconds, TimeUnit.SECONDS);
        }
    }

}
