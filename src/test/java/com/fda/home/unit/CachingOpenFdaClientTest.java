package com.fda.home.unit;

import com.fda.home.cache.OpenFdaCacheService;
import com.fda.home.client.OpenFdaApiClient;
import com.fda.home.client.impl.CachingOpenFdaClient;
import com.fda.home.model.dto.OpenFdaSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CachingOpenFdaClientTest {

    @InjectMocks
    private CachingOpenFdaClient cachingOpenFdaClient;

    @Mock
    private OpenFdaApiClient apiClient;

    @Mock
    private OpenFdaCacheService cacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnSearchResponseWhenCacheMissAndApiCallSucceeds() {
        // Given
        String manufacturerName = "TestManufacturer";
        String brandName = "TestBrand";
        int skip = 10;
        int limit = 20;

        String cacheKey = "cache-key";
        OpenFdaSearchResponse mockApiResponse = new OpenFdaSearchResponse();

        when(cacheService.buildCacheKey(manufacturerName, brandName, skip, limit)).thenReturn(cacheKey);
        when(cacheService.getFromCache(cacheKey)).thenReturn(Optional.empty());
        when(apiClient.fetchDrugApplications(manufacturerName, brandName, skip, limit)).thenReturn(mockApiResponse);

        // When
        OpenFdaSearchResponse actualResponse = cachingOpenFdaClient.searchDrugApplications(manufacturerName, brandName, skip, limit);

        // Then
        verify(cacheService).buildCacheKey(manufacturerName, brandName, skip, limit);
        verify(cacheService).getFromCache(cacheKey);
        verify(apiClient).fetchDrugApplications(manufacturerName, brandName, skip, limit);
        verify(cacheService).cacheResponse(cacheKey, mockApiResponse);
        assertThat(actualResponse).isEqualTo(mockApiResponse);
    }

    @Test
    void shouldReturnResponseFromCacheWhenCacheHit() {
        // Given
        String manufacturerName = "TestManufacturer";
        String brandName = "TestBrand";
        int skip = 10;
        int limit = 20;

        String cacheKey = "cache-key";
        OpenFdaSearchResponse cachedResponse = new OpenFdaSearchResponse();

        when(cacheService.buildCacheKey(manufacturerName, brandName, skip, limit)).thenReturn(cacheKey);
        when(cacheService.getFromCache(cacheKey)).thenReturn(Optional.of(cachedResponse));

        // When
        OpenFdaSearchResponse actualResponse = cachingOpenFdaClient.searchDrugApplications(manufacturerName, brandName, skip, limit);

        // Then
        verify(cacheService).buildCacheKey(manufacturerName, brandName, skip, limit);
        verify(cacheService).getFromCache(cacheKey);
        verifyNoInteractions(apiClient);
        assertThat(actualResponse).isEqualTo(cachedResponse);
    }

    @Test
    void shouldHandleApiFailureGracefully() {
        // Given
        String manufacturerName = "TestManufacturer";
        String brandName = "TestBrand";
        int skip = 10;
        int limit = 20;

        String cacheKey = "cache-key";

        when(cacheService.buildCacheKey(manufacturerName, brandName, skip, limit)).thenReturn(cacheKey);
        when(cacheService.getFromCache(cacheKey)).thenReturn(Optional.empty());
        when(apiClient.fetchDrugApplications(manufacturerName, brandName, skip, limit))
                .thenThrow(new RuntimeException("API call failed"));

        // When & Then
        assertThatThrownBy(() -> cachingOpenFdaClient.searchDrugApplications(manufacturerName, brandName, skip, limit))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("API call failed");

        verify(cacheService).buildCacheKey(manufacturerName, brandName, skip, limit);
        verify(cacheService).getFromCache(cacheKey);
        verify(apiClient).fetchDrugApplications(manufacturerName, brandName, skip, limit);
        verifyNoMoreInteractions(cacheService);
    }
}
