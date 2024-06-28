package com.leonardo.tests.misctests.infrastructure.config.cache;

import com.google.code.ssm.Cache;
import com.google.code.ssm.CacheFactory;
import com.google.code.ssm.config.AbstractSSMConfiguration;
import com.google.code.ssm.config.DefaultAddressProvider;
import com.google.code.ssm.providers.xmemcached.MemcacheClientFactoryImpl;
import com.google.code.ssm.providers.xmemcached.XMemcachedConfiguration;
import com.google.code.ssm.spring.ExtendedSSMCacheManager;
import com.google.code.ssm.spring.SSMCache;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(MemcachedProperties.class)
@Configuration
@EnableCaching
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "cache.memcached", name = "enabled", havingValue = "true")
@Slf4j
public class MemcachedCacheConfig extends AbstractSSMConfiguration {

    public static final String CACHE_MANAGER = "memcachedCacheManager";
    private static final String PORT_SEPARATOR = ":";
    public static final String CACHE_NAME = "memcached";

    private final MemcachedProperties memcachedProperties;

    @Override
    public CacheFactory defaultMemcachedClient() {
        CacheFactory cacheFactory = new CacheFactory();
        cacheFactory.setCacheClientFactory(new MemcacheClientFactoryImpl());
        cacheFactory.setCacheName(CACHE_NAME);
        cacheFactory.setAddressProvider(new DefaultAddressProvider(memcachedProperties
            .host()
            .concat(PORT_SEPARATOR)
            .concat(String.valueOf(memcachedProperties.port()))));
        cacheFactory.setConfiguration(getConfiguration());
        return cacheFactory;
    }

    private XMemcachedConfiguration getConfiguration() {
        XMemcachedConfiguration configuration = new XMemcachedConfiguration();
        configuration.setConsistentHashing(true);
        configuration.setUseBinaryProtocol(true);
        configuration.setConnectionTimeout((long) memcachedProperties.timeout());
        return configuration;
    }

    @Bean(name = CACHE_MANAGER)
    public CacheManager cacheManager() throws Exception {
        ExtendedSSMCacheManager cacheManager = new ExtendedSSMCacheManager();
        Cache cache = this.defaultMemcachedClient().getObject();

        if (!Objects.requireNonNull(cache).getAvailableServers().isEmpty()) {
            cacheManager.setCaches(List.of(new SSMCache(cache, memcachedProperties.defaultTTL())));
            return cacheManager;
        }

        log.error(
            "[MemcachedCacheConfig][Memcached] connection failed. Failing back to ConcurrentMapCacheManager");
        return new ConcurrentMapCacheManager(CACHE_NAME);
    }
}