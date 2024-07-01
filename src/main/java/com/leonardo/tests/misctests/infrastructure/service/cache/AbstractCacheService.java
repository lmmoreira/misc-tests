package com.leonardo.tests.misctests.infrastructure.service.cache;

import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@Slf4j
public abstract class AbstractCacheService implements CacheService {

    public abstract CacheManager getCacheManager();

    public abstract String getCacheName();

    public abstract void put(String key, Object value, long seconds);

    @Override
    @CacheException
    public <T> Optional<T> get(String key, Class<T> clazz) {
        final Cache cache = this.getCacheManager().getCache(this.getCacheName());
        final Optional<T> optional = Optional.ofNullable(Objects.requireNonNull(cache).get(key, clazz));
        log.info("[CacheService] Get key {} from cache: {}", key, optional.orElse(null));
        return optional;
    }

    @Override
    @CacheException(defaultValue = "false")
    public boolean exists(String key) {
        return this.get(key, Object.class).isPresent();
    }

    @Override
    @CacheException(defaultValue = "true")
    public boolean notExists(String key) {
        return !this.exists(key);
    }

    @Override
    @CacheException
    public void put(String key, Object value) {
        var cache = this.getCacheManager().getCache(this.getCacheName());
        Objects.requireNonNull(cache).put(key, value);
    }

    @Override
    @CacheException
    public void evict(String key) {
        var cache = this.getCacheManager().getCache(this.getCacheName());
        Objects.requireNonNull(cache).evict(key);
    }

}
