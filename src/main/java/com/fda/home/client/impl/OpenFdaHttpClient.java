package com.fda.home.client.impl;

import com.fda.home.client.OpenFdaApiClient;
import com.fda.home.client.OpenFdaErrorHandler;
import com.fda.home.client.util.OpenFdaUrlBuilder;
import com.fda.home.model.dto.OpenFdaSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpenFdaHttpClient implements OpenFdaApiClient {

    private final RestTemplate restTemplate;
    private final OpenFdaUrlBuilder urlBuilder;
    private final OpenFdaErrorHandler errorHandler;

    @Override
    public OpenFdaSearchResponse fetchDrugApplications(String manufacturerName,
                                                       String brandName,
                                                       int skip,
                                                       int limit) {
        String url = urlBuilder.buildSearchUrl(manufacturerName, brandName, skip, limit);
        log.info("Fetching data from OpenFDA API: {}", url);

        try {
            return restTemplate.exchange(url, HttpMethod.GET, null, OpenFdaSearchResponse.class).getBody();
        } catch (HttpClientErrorException ex) {
            errorHandler.handleHttpClientErrorException(ex);
            throw ex;
        }
    }
}
