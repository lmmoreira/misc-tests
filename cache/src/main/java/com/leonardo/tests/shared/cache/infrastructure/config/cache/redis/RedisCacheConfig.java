package com.leonardo.tests.shared.cache.infrastructure.config.cache.redis;

import com.leonardo.tests.shared.cache.infrastructure.config.cache.InMemoryCacheManager;
import com.leonardo.tests.shared.cache.infrastructure.config.cache.properties.RedisProperties;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
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

@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
@RequiredArgsConstructor
@Slf4j
@ConditionalOnExpression("'${cache.redis.mode}'.equals('redis') || '${cache.redis.mode}'.equals('inmemory')")
public class RedisCacheConfig implements CachingConfigurer, InMemoryCacheManager {

    public static final String CACHE_MANAGER = "RedisCacheManager";
    public static final String CACHE_NAME = "redis";
    private final RedisProperties redisProperties;

    @Bean
    @ConditionalOnProperty(prefix = "cache.redis", name = "mode", havingValue = "redis")
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
    @ConditionalOnProperty(prefix = "cache.redis", name = "mode", havingValue = "redis")
    public RedisTemplate<String, Object> redisTemplate(
        final RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    @ConditionalOnProperty(prefix = "cache.redis", name = "mode", havingValue = "redis")
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()))
            .entryTtl(Duration.ofSeconds(this.redisProperties.defaultTTL()));
    }

    @Bean(name = CACHE_MANAGER)
    public CacheManager redisCacheManager(
        final Optional<RedisConnectionFactory> redisConnectionFactory,
        final Optional<RedisCacheConfiguration> redisCacheConfiguration) {

        if (redisProperties.isCacheMode() && redisConnectionFactory.isPresent()
            && redisCacheConfiguration.isPresent()) {
            try {
                log.info("[REDIS] connection status {}",
                    redisConnectionFactory.get().getConnection().ping());
                return RedisCacheManager.builder(redisConnectionFactory.get())
                    .cacheDefaults(redisCacheConfiguration.get())
                    .build();
            } catch (final Exception e) {
                log.error(
                    "[RedisCacheConfig] connection failed. Failing back to ConcurrentMapCacheManager");
            }
        }

        return (redisProperties.isNoneMode()) ? null : inMemoryCacheManager();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisErrorHandling();
    }

}
