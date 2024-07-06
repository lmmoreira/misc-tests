package com.leonardo.tests.misctests.infrastructure.config.cache.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty(prefix = "cache.redis", name = "mode", havingValue = "redis")
@ConfigurationProperties(prefix = "cache.redis")
public record RedisProperties(
    String mode,
    String host,
    int port,
    String password,
    int timeout,
    int defaultTTL) implements CacheProperties {

}
