package com.leonardo.tests.misctests.infrastructure.config.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty(prefix = "cache.memcached", name = "mode", havingValue = "memcached")
@ConfigurationProperties(prefix = "cache.memcached")
public record MemcachedProperties(
    String mode,
    String host,
    int port,
    int defaultTTL,
    int timeout
) {

    public boolean isMemcachedMode() {
        return "memcached".equals(mode);
    }

    public boolean isInMemoryMode() {
        return "inmemory".equals(mode);
    }

    public boolean isNoneMode() {
        return "none".equals(mode);
    }

}
