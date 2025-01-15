package com.fda.home.service;

import com.openfda.generated.models.DrugApplicationDto;
import com.openfda.generated.models.DrugApplicationRequest;
import com.openfda.generated.models.SearchResponse;

import java.util.List;

public interface DrugApplicationService {
    List<DrugApplicationDto> getDrugApplications();

    SearchResponse searchDrugApplications(String manufacturerName, String brandName, int page, int size);

    void createDrugApplication(DrugApplicationRequest drugApplicationRequest);
}
