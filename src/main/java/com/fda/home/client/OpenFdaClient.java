package com.fda.home.client;


import com.fda.home.model.dto.OpenFdaSearchResponse;

public interface OpenFdaClient {
    OpenFdaSearchResponse searchDrugApplications(String manufacturerName,
                                                 String brandName,
                                                 int skip,
                                                 int limit);
}
