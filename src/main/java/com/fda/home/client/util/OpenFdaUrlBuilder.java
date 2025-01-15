package com.fda.home.client.util;

import com.fda.home.util.OpenFdaApiConstants;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OpenFdaUrlBuilder {

    @Value("${openfda.api.base-url}")
    private String baseUrl;

    public String buildSearchUrl(String manufacturerName, String brandName, int skip, int limit) {
        String searchQuery = String.format("%s:\"%s\"", OpenFdaApiConstants.MANUFACTURER_NAME_FIELD, manufacturerName);
        if (!StringUtils.isBlank(brandName)) {
            searchQuery += String.format(" %s %s:\"%s\"", OpenFdaApiConstants.AND_OPERATOR, OpenFdaApiConstants.BRAND_NAME_FIELD, brandName);
        }

        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam(OpenFdaApiConstants.SEARCH_PARAM, searchQuery)
                .queryParam(OpenFdaApiConstants.LIMIT_PARAM, limit)
                .queryParam(OpenFdaApiConstants.SKIP_PARAM, skip)
                .build()
                .toUriString();
    }
}
