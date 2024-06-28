package com.leonardo.tests.misctests.infrastructure.config.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty(prefix = "cache.memcached", name = "enabled", havingValue = "true")
@ConfigurationProperties(prefix = "cache.memcached")
public record MemcachedProperties(
    boolean enabled,
    String host,
    int port,
    int defaultTTL,
    int timeout
) {

}
