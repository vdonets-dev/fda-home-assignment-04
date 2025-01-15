package com.fda.home.cache;

import com.fda.home.model.dto.OpenFdaSearchResponse;

import java.util.Optional;

public interface OpenFdaCacheService {
    Optional<OpenFdaSearchResponse> getFromCache(String cacheKey);

    void cacheResponse(String cacheKey, OpenFdaSearchResponse response);

    String buildCacheKey(String manufacturerName, String brandName, int skip, int limit);
}
