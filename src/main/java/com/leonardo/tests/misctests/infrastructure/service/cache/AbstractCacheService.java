package com.leonardo.tests.misctests.infrastructure.service.cache;

import java.util.Objects;
import java.util.Optional;
import org.springframework.cache.CacheManager;

public abstract class AbstractCacheService implements CacheService {

    public abstract CacheManager getCacheManager();

    public abstract String getCacheName();

    public abstract void put(String key, Object value, long seconds);

    @Override
    @CacheRetrieveException
    public <T> Optional<T> get(String key, Class<T> clazz) {
        var cache = this.getCacheManager().getCache(this.getCacheName());
        return Optional.ofNullable(Objects.requireNonNull(cache).get(key, clazz));
    }

    @Override
    @CacheRetrieveException(defaultValue = "false")
    public boolean exists(String key) {
        var cache = this.getCacheManager().getCache(this.getCacheName());
        return Objects.requireNonNull(cache).get(key) != null;
    }

    @Override
    @CacheRetrieveException(defaultValue = "true")
    public boolean notExists(String key) {
        var cache = this.getCacheManager().getCache(this.getCacheName());
        return Objects.requireNonNull(cache).get(key) == null;
    }

    @Override
    @CacheRetrieveException
    public void put(String key, Object value) {
        var cache = this.getCacheManager().getCache(this.getCacheName());
        Objects.requireNonNull(cache).put(key, value);
    }

    @Override
    @CacheRetrieveException
    public void evict(String key) {
        var cache = this.getCacheManager().getCache(this.getCacheName());
        Objects.requireNonNull(cache).evict(key);
    }

}
