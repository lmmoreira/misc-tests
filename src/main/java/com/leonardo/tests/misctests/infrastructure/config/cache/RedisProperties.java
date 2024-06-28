package com.leonardo.tests.misctests.infrastructure.config.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty(prefix = "cache.redis", name = "enabled", havingValue = "true")
@ConfigurationProperties(prefix = "cache.redis")
public record RedisProperties(
    boolean enabled,
    String host,
    int port,
    String password,
    int timeout,
    int defaultTTL) {}
