package com.leonardo.tests.misctests.infrastructure.config.cache;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@ConditionalOnProperty(prefix = "cache.redis", name = "enabled", havingValue = "true")
@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
@RequiredArgsConstructor
@Slf4j
public class RedisCacheConfig implements CachingConfigurer {

    public static final String CACHE_MANAGER = "RedisCacheManager";
    public static final String CACHE_NAME = "redis";
    private final RedisProperties redisProperties;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        final SocketOptions so =
            SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(this.redisProperties.timeout()))
                .build();

        final ClientOptions clientOptions = ClientOptions.builder().socketOptions(so).build();

        final LettuceClientConfiguration clientConfig =
            LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(this.redisProperties.timeout()))
                .clientOptions(clientOptions)
                .build();

        final RedisStandaloneConfiguration redisStandaloneConfiguration =
            new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(this.redisProperties.host());
        redisStandaloneConfiguration.setPort(this.redisProperties.port());
        redisStandaloneConfiguration.setPassword(this.redisProperties.password());
        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(final RedisConnectionFactory cf) {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cf);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .entryTtl(Duration.ofSeconds(this.redisProperties.defaultTTL()));
    }

    @Bean(name = CACHE_MANAGER)
    public CacheManager redisCacheManager(
        final RedisConnectionFactory redisConnectionFactory,
        final RedisCacheConfiguration redisCacheConfiguration) {

        try {
            log.info("[REDIS] connection status {}", redisConnectionFactory.getConnection().ping());
            return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
        } catch (final Exception e) {
            log.error(
                "[RedisCacheConfig][Redis] connection failed. Failing back to ConcurrentMapCacheManager");
        }
        return new ConcurrentMapCacheManager(CACHE_NAME);
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisErrorHandling();
    }

}
