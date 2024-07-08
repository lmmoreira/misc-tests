package com.leonardo.tests.shared.cache.infrastructure.config;

import com.leonardo.tests.shared.cache.infrastructure.config.cache.caffeine.CaffeineCacheConfig;
import com.leonardo.tests.shared.cache.infrastructure.config.cache.memcached.MemcachedCacheConfig;
import com.leonardo.tests.shared.cache.infrastructure.config.cache.redis.RedisCacheConfig;
import com.leonardo.tests.shared.cache.infrastructure.service.cache.CacheServiceFactory;
import com.leonardo.tests.shared.cache.infrastructure.service.cache.CaffeineCacheService;
import com.leonardo.tests.shared.cache.infrastructure.service.cache.MemCachedCacheService;
import com.leonardo.tests.shared.cache.infrastructure.service.cache.RedisCacheService;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
    CaffeineCacheConfig.class,
    MemcachedCacheConfig.class,
    RedisCacheConfig.class,
    CacheServiceFactory.class,
    CaffeineCacheService.class,
    MemCachedCacheService.class,
    RedisCacheService.class

})
public class CacheSharedConfig {

}
