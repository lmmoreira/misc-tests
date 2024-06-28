package com.leonardo.tests.misctests.controller;

import com.leonardo.tests.misctests.infrastructure.service.cache.CacheService;
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

    private final CacheService memCachedCacheService;
    private final CacheService redisCacheService;

    @GetMapping(value = "/{marketplaceId}", produces = "application/json")
    public MarketplaceEntity getUser(@PathVariable Long marketplaceId) {
        return redisCacheService.get("default" + marketplaceId, MarketplaceEntity.class)
            .orElseGet(() -> {
                var e = marketplaceEntityRepository.findById(marketplaceId)
                    .orElseThrow(RuntimeException::new);
                redisCacheService.put("default" + marketplaceId, e, 10);
                return e;
            });
    }

    @GetMapping(value = "/memcached/{marketplaceId}", produces = "application/json")
    public MarketplaceEntity getUserMemcached(@PathVariable Long marketplaceId) {
        return memCachedCacheService.get("default" + marketplaceId, MarketplaceEntity.class)
            .orElseGet(() -> {
                var e = marketplaceEntityRepository.findById(marketplaceId)
                    .orElseThrow(RuntimeException::new);
                memCachedCacheService.put("default" + marketplaceId, e, 10);
                return e;
            });
    }

    @GetMapping(value = "/caffeine/{marketplaceId}", produces = "application/json")
    @Cacheable(cacheManager = "caffeineCacheManager", value = "marketplaceController", key = "{'cafeine', #marketplaceId}")
    public MarketplaceEntity getUserCaffeine(@PathVariable Long marketplaceId) {
        return marketplaceEntityRepository.findById(marketplaceId)
            .orElseThrow(RuntimeException::new);
    }

}
