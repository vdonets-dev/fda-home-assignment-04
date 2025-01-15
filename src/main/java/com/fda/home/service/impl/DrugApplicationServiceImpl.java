package com.fda.home.service.impl;

import com.fda.home.client.OpenFdaClient;
import com.fda.home.converter.Converter;
import com.fda.home.model.DrugApplication;
import com.fda.home.model.dto.OpenFdaSearchResponse;
import com.fda.home.repository.DrugApplicationMapper;
import com.fda.home.service.DrugApplicationService;
import com.openfda.generated.models.DrugApplicationDto;
import com.openfda.generated.models.DrugApplicationRequest;
import com.openfda.generated.models.SearchResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class DrugApplicationServiceImpl implements DrugApplicationService {

    private final OpenFdaClient openFdaClient;
    private final DrugApplicationMapper repository;
    private final Converter<OpenFdaSearchResponse, SearchResponse> searchResponseConverter;
    private final Converter<DrugApplication, DrugApplicationDto> drugApplicationConverter;

    @Override
    public SearchResponse searchDrugApplications(String manufacturerName, String brandName, int page, int size) {
        log.info("Calling openFDA searchDrugs for manufacturer={}, brand={}, page={}, size={}",
                manufacturerName, brandName, page, size);
        int skip = page * size;
        return searchResponseConverter.convert(openFdaClient.searchDrugApplications(manufacturerName, brandName, skip, size));
    }

    @Override
    public List<DrugApplicationDto> getDrugApplications() {
        log.info("Retrieving all stored drug applications");
        return drugApplicationConverter.convertMultiple(repository.getAllDrugApplications(), Collectors.toList());
    }

    @Transactional
    @Override
    public void createDrugApplication(DrugApplicationRequest applicationRequest) {
        log.info("Saving new drug application with applicationNumber={}", applicationRequest.getApplicationNumber());
        repository.insertDrugApplicationWithDetails(applicationRequest);
    }
}
