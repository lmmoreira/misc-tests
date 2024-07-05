package com.leonardo.tests.misctests.infrastructure.config.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EnableConfigurationProperties(CaffeineProperties.class)
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CaffeineCacheConfig {

    public static final String CACHE_MANAGER = "caffeineCacheManager";
    public static final String CACHE_NAME = "caffeine";
    public static final String TTL_SEPARATOR = "#";

    private final CaffeineProperties caffeineProperties;

    @Primary
    @Bean(name = CACHE_MANAGER)
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CustomCaffeineCacheManager();
        return (caffeineProperties.isNoneMode()) ? null
            : cacheManager;
    }

}
