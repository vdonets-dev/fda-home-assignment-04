package com.fda.home.unit;

import com.fda.home.client.OpenFdaClient;
import com.fda.home.converter.Converter;
import com.fda.home.model.DrugApplication;
import com.fda.home.model.dto.OpenFdaSearchResponse;
import com.fda.home.repository.DrugApplicationMapper;
import com.fda.home.service.impl.DrugApplicationServiceImpl;
import com.openfda.generated.models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DrugApplicationServiceTest {

    @Mock
    private OpenFdaClient openFdaClient;

    @Mock
    private DrugApplicationMapper repository;

    @Mock
    private Converter<OpenFdaSearchResponse, SearchResponse> searchResponseConverter;

    @Mock
    private Converter<DrugApplication, DrugApplicationDto> drugApplicationConverter;

    @InjectMocks
    private DrugApplicationServiceImpl drugApplicationService;

    @Captor
    private ArgumentCaptor<OpenFdaSearchResponse> openFdaResponseCaptor;

    @Captor
    private ArgumentCaptor<List<DrugApplication>> drugApplicationListCaptor;

    @Captor
    private ArgumentCaptor<DrugApplicationRequest> drugApplicationRequestCaptor;

    @AfterEach
    void tearDown() {
        Mockito.reset(openFdaClient, searchResponseConverter, repository, drugApplicationConverter);
    }

    @Test
    @DisplayName("Should search drug applications successfully")
    void searchDrugApplications_Success() {
        // Given
        String manufacturerName = "Pfizer";
        String brandName = "Lipitor";
        int page = 0;
        int size = 10;
        int skip = page * size;

        OpenFdaSearchResponse.Meta.Results mockMetaResults = new OpenFdaSearchResponse.Meta.Results();
        mockMetaResults.setSkip(skip);
        mockMetaResults.setLimit(size);
        mockMetaResults.setTotal(1);

        OpenFdaSearchResponse.Meta mockMeta = new OpenFdaSearchResponse.Meta();
        mockMeta.setResults(mockMetaResults);

        OpenFdaSearchResponse.Result mockResult = new OpenFdaSearchResponse.Result();
        mockResult.setApplicationNumber("NDA123456");
        mockResult.setSponsorName("Pfizer");

        OpenFdaSearchResponse mockResponse = new OpenFdaSearchResponse();
        mockResponse.setMeta(mockMeta);
        mockResponse.setResults(List.of(mockResult));

        SearchResponse expectedSearchResponse = new SearchResponse();

        when(openFdaClient.searchDrugApplications(manufacturerName, brandName, skip, size))
                .thenReturn(mockResponse);
        when(searchResponseConverter.convert(eq(mockResponse)))
                .thenReturn(expectedSearchResponse);

        // When
        SearchResponse result = drugApplicationService.searchDrugApplications(manufacturerName, brandName, page, size);

        // Then
        verify(openFdaClient).searchDrugApplications(manufacturerName, brandName, skip, size);
        Assertions.assertEquals(expectedSearchResponse, result);
    }

    @Test
    @DisplayName("Should retrieve all stored drug applications")
    void getDrugApplications_Success() {
        // Given
        DrugApplication mockApplication = new DrugApplication();
        mockApplication.setApplicationNumber("NDA123456");

        List<DrugApplication> mockApplications = List.of(mockApplication);

        DrugApplicationDto expectedDto = new DrugApplicationDto();
        expectedDto.setApplicationNumber("NDA123456");

        List<DrugApplicationDto> expectedDtos = List.of(expectedDto);

        when(repository.getAllDrugApplications())
                .thenReturn(mockApplications);
        when(drugApplicationConverter.convertMultiple(eq(mockApplications), any()))
                .thenReturn(expectedDtos);

        // When
        List<DrugApplicationDto> result = drugApplicationService.getDrugApplications();

        // Then
        Assertions.assertEquals(mockApplications, drugApplicationListCaptor.getValue());
        Assertions.assertEquals(expectedDtos, result);
    }

    @Test
    @DisplayName("Should save new drug application")
    void createDrugApplication_Success() {
        // Given
        DrugApplicationRequest applicationRequest = new DrugApplicationRequest();
        applicationRequest.setApplicationNumber("ANDA203300");

        // When
        drugApplicationService.createDrugApplication(applicationRequest);

        // Then
        verify(repository).insertDrugApplicationWithDetails(drugApplicationRequestCaptor.capture());
        Assertions.assertEquals(applicationRequest, drugApplicationRequestCaptor.getValue());
    }

    @Test
    @DisplayName("Should handle empty drug application results")
    void searchDrugApplications_EmptyResults() {
        // Given
        String manufacturerName = "Unknown";
        String brandName = "UnknownBrand";
        int page = 0;
        int size = 10;
        int skip = page * size;

        OpenFdaSearchResponse.Meta.Results mockMetaResults = new OpenFdaSearchResponse.Meta.Results();
        mockMetaResults.setSkip(skip);
        mockMetaResults.setLimit(size);
        mockMetaResults.setTotal(0);

        OpenFdaSearchResponse.Meta mockMeta = new OpenFdaSearchResponse.Meta();
        mockMeta.setResults(mockMetaResults);

        OpenFdaSearchResponse mockResponse = new OpenFdaSearchResponse();
        mockResponse.setMeta(mockMeta);
        mockResponse.setResults(List.of());

        SearchResponse expectedSearchResponse = new SearchResponse();

        when(openFdaClient.searchDrugApplications(manufacturerName, brandName, skip, size))
                .thenReturn(mockResponse);
        when(searchResponseConverter.convert(eq(mockResponse)))
                .thenReturn(expectedSearchResponse);

        // When
        SearchResponse result = drugApplicationService.searchDrugApplications(manufacturerName, brandName, page, size);

        // Then
        verify(openFdaClient).searchDrugApplications(manufacturerName, brandName, skip, size);
        Assertions.assertEquals(expectedSearchResponse, result);
    }

    @Test
    @DisplayName("Should search drug applications with multiple results")
    void searchDrugApplications_MultipleResults() {
        // Given
        String manufacturerName = "Moderna";
        String brandName = "Spikevax";
        int page = 1;
        int size = 5;
        int skip = page * size;

        OpenFdaSearchResponse.Meta.Results mockMetaResults = new OpenFdaSearchResponse.Meta.Results();
        mockMetaResults.setSkip(skip);
        mockMetaResults.setLimit(size);
        mockMetaResults.setTotal(2);

        OpenFdaSearchResponse.Meta mockMeta = new OpenFdaSearchResponse.Meta();
        mockMeta.setResults(mockMetaResults);

        OpenFdaSearchResponse.Result result1 = new OpenFdaSearchResponse.Result();
        result1.setApplicationNumber("NDA654321");
        result1.setSponsorName("Moderna");

        OpenFdaSearchResponse.Result result2 = new OpenFdaSearchResponse.Result();
        result2.setApplicationNumber("NDA789012");
        result2.setSponsorName("Moderna");

        OpenFdaSearchResponse mockResponse = new OpenFdaSearchResponse();
        mockResponse.setMeta(mockMeta);
        mockResponse.setResults(List.of(result1, result2));

        SearchResponse expectedSearchResponse = new SearchResponse();

        when(openFdaClient.searchDrugApplications(manufacturerName, brandName, skip, size))
                .thenReturn(mockResponse);
        when(searchResponseConverter.convert(eq(mockResponse)))
                .thenReturn(expectedSearchResponse);

        // When
        SearchResponse result = drugApplicationService.searchDrugApplications(manufacturerName, brandName, page, size);

        // Then
        verify(searchResponseConverter).convert(openFdaResponseCaptor.capture());
        Assertions.assertEquals(mockResponse, openFdaResponseCaptor.getValue());
        verify(openFdaClient).searchDrugApplications(manufacturerName, brandName, skip, size);
        Assertions.assertEquals(expectedSearchResponse, result);
    }
}
