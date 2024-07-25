package com.leonardo.tests.misctests.infrastructure.config.cache.properties;

public interface CacheProperties {
    String mode();

    default boolean isNoneMode() {
        return "none".equals(mode());
    }

    default boolean isInMemoryMode() {
        return "inmemory".equals(mode());
    }

    default boolean isCacheMode() {
        return (!this.isNoneMode() && !this.isInMemoryMode());
    }

}