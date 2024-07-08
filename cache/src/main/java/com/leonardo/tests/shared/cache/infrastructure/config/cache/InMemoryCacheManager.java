package com.leonardo.tests.shared.cache.infrastructure.config.cache;

import com.leonardo.tests.shared.cache.infrastructure.config.cache.caffeine.CustomCaffeineCacheManager;
import org.springframework.cache.CacheManager;

public interface InMemoryCacheManager {

    default CacheManager inMemoryCacheManager() {
        return new CustomCaffeineCacheManager();
    }
}
