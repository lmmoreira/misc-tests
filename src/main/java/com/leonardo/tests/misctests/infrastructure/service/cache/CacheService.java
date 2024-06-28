package com.leonardo.tests.misctests.infrastructure.service.cache;

import java.util.Optional;

public interface CacheService {
    <T> Optional<T> get(String key, Class<T> clazz);
    boolean exists(String key);
    boolean notExists(String key);
    void put(String key, Object value, long seconds);
    void put(String key, Object value);
    void evict(String key);
}
