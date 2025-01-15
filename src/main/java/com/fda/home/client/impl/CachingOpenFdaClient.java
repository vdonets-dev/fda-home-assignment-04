package com.fda.home.client.impl;

import com.fda.home.cache.OpenFdaCacheService;
import com.fda.home.client.OpenFdaApiClient;
import com.fda.home.client.OpenFdaClient;
import com.fda.home.model.dto.OpenFdaSearchResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CachingOpenFdaClient implements OpenFdaClient {

    private final OpenFdaApiClient apiClient;
    private final OpenFdaCacheService cacheService;

    @CircuitBreaker(name = "openfdaCircuitBreaker")
    @Retryable(retryFor = {ConnectTimeoutException.class, SocketTimeoutException.class},
            backoff = @Backoff(delayExpression = "#{${openfda.api.retry.backoff-interval}}"))
    public OpenFdaSearchResponse searchDrugApplications(String manufacturerName,
                                                        String brandName,
                                                        int skip,
                                                        int limit) {
        String cacheKey = cacheService.buildCacheKey(manufacturerName, brandName, skip, limit);

        return cacheService.getFromCache(cacheKey)
                .orElseGet(() -> {
                    log.info("Cache miss. Fetching data from API...");
                    OpenFdaSearchResponse response = apiClient.fetchDrugApplications(manufacturerName, brandName, skip, limit);
                    cacheService.cacheResponse(cacheKey, response);
                    return response;
                });
    }
}

