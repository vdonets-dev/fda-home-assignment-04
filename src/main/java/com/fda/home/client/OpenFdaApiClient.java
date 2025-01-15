package com.fda.home.client;

import com.fda.home.model.dto.OpenFdaSearchResponse;

public interface OpenFdaApiClient {
    OpenFdaSearchResponse fetchDrugApplications(String manufacturerName,
                                                String brandName,
                                                int skip,
                                                int limit);
}
