package com.leonardo.tests.misctests.controller;


import com.leonardo.tests.misctests.infrastructure.config.cache.caffeine.CaffeineCacheConfig;
import com.leonardo.tests.misctests.infrastructure.config.cache.memcached.MemcachedCacheConfig;
import com.leonardo.tests.misctests.infrastructure.config.cache.redis.RedisCacheConfig;
import com.leonardo.tests.misctests.infrastructure.service.cache.CacheServiceFactory;
import com.leonardo.tests.misctests.infrastructure.service.cache.CaffeineCacheService;
import com.leonardo.tests.misctests.infrastructure.service.cache.MemCachedCacheService;
import com.leonardo.tests.misctests.infrastructure.service.cache.RedisCacheService;
import com.leonardo.tests.misctests.orm.entity.MarketplaceEntity;
import com.leonardo.tests.misctests.orm.repository.MarketplaceEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/marketplace")
@Slf4j
public class MarketplaceController {

    private final MarketplaceEntityRepository marketplaceEntityRepository;

    private final CacheServiceFactory cacheServiceFactory;

    @GetMapping(value = "/{marketplaceId}", produces = "application/json")
    public MarketplaceEntity getUser(@PathVariable Long marketplaceId) {
        return cacheServiceFactory.getCacheService(RedisCacheService.BEAN_ID)
            .get("default" + marketplaceId, MarketplaceEntity.class)
            .orElseGet(() -> {
                var e = marketplaceEntityRepository.findById(marketplaceId)
                    .orElseThrow(RuntimeException::new);
                cacheServiceFactory.getCacheService(RedisCacheService.BEAN_ID)
                    .put("default" + marketplaceId, e, 10);
                return e;
            });
    }

    @GetMapping(value = "/memcached/{marketplaceId}", produces = "application/json")
    public MarketplaceEntity getUserMemcached(@PathVariable Long marketplaceId) {
        return cacheServiceFactory.getCacheService(MemCachedCacheService.BEAN_ID)
            .get("default" + marketplaceId, MarketplaceEntity.class)
            .orElseGet(() -> {
                var e = marketplaceEntityRepository.findById(marketplaceId)
                    .orElseThrow(RuntimeException::new);
                cacheServiceFactory.getCacheService(MemCachedCacheService.BEAN_ID)
                    .put("default" + marketplaceId, e, 10);
                return e;
            });
    }

    @GetMapping(value = "/caffeine/{marketplaceId}", produces = "application/json")
    public MarketplaceEntity getUserCaffeine(@PathVariable Long marketplaceId) {
        return cacheServiceFactory.getCacheService(CaffeineCacheService.BEAN_ID)
            .get("default" + marketplaceId, MarketplaceEntity.class)
            .orElseGet(() -> {
                var e = marketplaceEntityRepository.findById(marketplaceId)
                    .orElseThrow(RuntimeException::new);
                cacheServiceFactory.getCacheService(CaffeineCacheService.BEAN_ID)
                    .put("default" + marketplaceId, e, 10);
                return e;
            });
    }

    @GetMapping(value = "/caffeine2/{marketplaceId}", produces = "application/json")
    public MarketplaceEntity getUserCaffeine2(@PathVariable Long marketplaceId) {
        return cacheServiceFactory.getCacheService(CaffeineCacheService.BEAN_ID)
            .get("default2" + marketplaceId, MarketplaceEntity.class)
            .orElseGet(() -> {
                var e = marketplaceEntityRepository.findById(marketplaceId)
                    .orElseThrow(RuntimeException::new);
                cacheServiceFactory.getCacheService(CaffeineCacheService.BEAN_ID)
                    .put("default2" + marketplaceId, e, 30);
                return e;
            });
    }

    @GetMapping(value = "/redis/annot/{marketplaceId}", produces = "application/json")
    @Cacheable(cacheManager = RedisCacheConfig.CACHE_MANAGER, value = RedisCacheConfig.CACHE_NAME, key = "'default' + #marketplaceId")
    public MarketplaceEntity getUserRedisAnnot(@PathVariable Long marketplaceId) {
        return marketplaceEntityRepository.findById(marketplaceId)
            .orElseThrow(RuntimeException::new);
    }

    @GetMapping(value = "/memcached/annot/{marketplaceId}", produces = "application/json")
    @Cacheable(cacheManager = MemcachedCacheConfig.CACHE_MANAGER, value = MemcachedCacheConfig.CACHE_NAME,key = "'default' + #marketplaceId")
    public MarketplaceEntity getUserMemCachedAnnot(@PathVariable Long marketplaceId) {
        return marketplaceEntityRepository.findById(marketplaceId)
            .orElseThrow(RuntimeException::new);
    }

    @GetMapping(value = "/caffeine/annot/{marketplaceId}", produces = "application/json")
    @Cacheable(cacheManager = CaffeineCacheConfig.CACHE_MANAGER, value = CaffeineCacheConfig.CACHE_NAME, key = "'default' + #marketplaceId")
    public MarketplaceEntity getUserCaffeineAnnot(@PathVariable Long marketplaceId) {
        return marketplaceEntityRepository.findById(marketplaceId)
            .orElseThrow(RuntimeException::new);
    }

}
