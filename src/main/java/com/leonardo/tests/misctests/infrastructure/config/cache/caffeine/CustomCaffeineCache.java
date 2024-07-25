package com.leonardo.tests.misctests.infrastructure.config.cache.caffeine;

import static com.leonardo.tests.misctests.infrastructure.config.cache.caffeine.CaffeineCacheConfig.TTL_SEPARATOR;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Duration;
import org.springframework.cache.caffeine.CaffeineCache;

public class CustomCaffeineCache extends CaffeineCache {

    private final long defaultTTL;

    public CustomCaffeineCache(String name, Cache<Object, Object> cache, long defaultTTL) {
        super(name, cache);
        this.defaultTTL = defaultTTL;
    }

    @Override
    public void put(Object key, Object value) {
        final String keyStr = key.toString();
        final String[] keyParts = keyStr.split(TTL_SEPARATOR);
        final String baseKey = keyParts[0];
        final long ttl = (keyParts.length > 1) ? Long.parseLong(keyParts[1]) : defaultTTL;

        getNativeCache().put(baseKey, value);
        getNativeCache().policy().expireVariably().ifPresent(expiry -> expiry.put(baseKey, value, Duration.ofSeconds(ttl)));
    }
}
