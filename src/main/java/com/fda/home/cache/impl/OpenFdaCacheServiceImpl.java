package com.fda.home.cache.impl;

import com.fda.home.cache.CacheNames;
import com.fda.home.cache.OpenFdaCacheService;
import com.fda.home.model.dto.OpenFdaSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpenFdaCacheServiceImpl implements OpenFdaCacheService {
    private final CacheManager cacheManager;
    public static final String KEY_FORMAT = "%s:%s:%d:%d";

    @Override
    public Optional<OpenFdaSearchResponse> getFromCache(String cacheKey) {
        return Optional.ofNullable(cacheManager.getCache(CacheNames.OPEN_FDA_CACHE.getName()))
                .map(cache -> cache.get(cacheKey, OpenFdaSearchResponse.class));
    }

    @Override
    public void cacheResponse(String cacheKey, OpenFdaSearchResponse response) {
        Optional.ofNullable(cacheManager.getCache(CacheNames.OPEN_FDA_CACHE.getName()))
                .ifPresent(cache -> cache.put(cacheKey, response));
    }

    @Override
    public String buildCacheKey(String manufacturerName, String brandName, int skip, int limit) {
        return String.format(KEY_FORMAT, manufacturerName, brandName, skip, limit);
    }
}
