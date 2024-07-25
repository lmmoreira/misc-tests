package com.leonardo.tests.misctests.infrastructure.config.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;

public class CustomCaffeineCacheManager extends CaffeineCacheManager {

    private final long defaultTTL;

    public CustomCaffeineCacheManager(long defaultTTL) {
        this.defaultTTL = defaultTTL == -1 ? Long.MAX_VALUE : defaultTTL;
    }

    public CustomCaffeineCacheManager() {
        this.defaultTTL = Long.MAX_VALUE;
    }

    @Override
    protected CaffeineCache createCaffeineCache(final String name) {
        return new CustomCaffeineCache(name,
            Caffeine.newBuilder().expireAfter(new Expiry<>() {
                @Override
                public long expireAfterCreate(Object key, Object value, long currentTime) {
                    // Default to MAX_VALUE or default since it is going to be managed manually
                    return Long.MAX_VALUE;
                }

                @Override
                public long expireAfterUpdate(Object key, Object value, long currentTime,
                    long currentDuration) {
                    return currentDuration;
                }

                @Override
                public long expireAfterRead(Object key, Object value, long currentTime,
                    long currentDuration) {
                    return currentDuration;
                }
            }).build(), defaultTTL);
    }

}
