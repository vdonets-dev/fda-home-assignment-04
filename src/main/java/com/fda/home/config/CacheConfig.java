package com.fda.home.config;

import com.fda.home.cache.CacheName;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Value("${caching.caches.openFdaCache.expire-after-write-seconds}")
    private long expireAfterWriteSeconds;

    @Value("${caching.caches.openFdaCache.maximum-size}")
    private long maximumSize;

    @Value("${caching.caches.openFdaCache.initial-capacity}")
    private int initialCapacity;

    @Bean
    public CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CacheName.OPEN_FDA_CACHE.getName());
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWriteSeconds, TimeUnit.SECONDS)
                .maximumSize(maximumSize)
                .initialCapacity(initialCapacity));
        return cacheManager;
    }
}
