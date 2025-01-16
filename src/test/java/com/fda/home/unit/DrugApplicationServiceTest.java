package com.fda.home.unit;

import com.fda.home.client.OpenFdaClient;
import com.fda.home.converter.Converter;
import com.fda.home.model.DrugApplication;
import com.fda.home.model.dto.OpenFdaSearchResponse;
import com.fda.home.repository.DrugApplicationMapper;
import com.fda.home.service.impl.DrugApplicationServiceImpl;
import com.openfda.generated.models.DrugApplicationDetails;
import com.openfda.generated.models.DrugApplicationDto;
import com.openfda.generated.models.DrugApplicationRequest;
import com.openfda.generated.models.SearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DrugApplicationServiceTest {

    @InjectMocks
    private DrugApplicationServiceImpl drugApplicationService;

    @Mock
    private OpenFdaClient openFdaClient;

    @Mock
    private DrugApplicationMapper repository;

    @Mock
    private Converter<OpenFdaSearchResponse, SearchResponse> searchResponseConverter;

    @Mock
    private Converter<DrugApplication, DrugApplicationDto> drugApplicationConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnSearchResponseWhenSearchDrugApplicationsCalled() {
        // Given
        String manufacturerName = "TestManufacturer";
        String brandName = "TestBrand";
        int page = 1;
        int size = 10;
        int skip = page * size;

        OpenFdaSearchResponse mockApiResponse = new OpenFdaSearchResponse();
        OpenFdaSearchResponse.Meta meta = new OpenFdaSearchResponse.Meta();
        OpenFdaSearchResponse.Meta.Results results = new OpenFdaSearchResponse.Meta.Results();
        results.setLimit(3);
        results.setSkip(0);
        results.setTotal(0);
        meta.setResults(results);
        mockApiResponse.setMeta(meta);

        SearchResponse expectedResponse = new SearchResponse();

        when(openFdaClient.searchDrugApplications(manufacturerName, brandName, skip, size)).thenReturn(mockApiResponse);
        when(searchResponseConverter.convert(mockApiResponse)).thenReturn(expectedResponse);

        // When
        SearchResponse actualResponse = drugApplicationService.searchDrugApplications(manufacturerName, brandName, page, size);

        // Then
        verify(openFdaClient).searchDrugApplications(manufacturerName, brandName, skip, size);
        verify(searchResponseConverter, times(1)).convert(mockApiResponse);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnAllDrugApplicationsWhenGetDrugApplicationsCalled() {
        // Given
        List<DrugApplication> mockApplications = List.of(
                new DrugApplication("123", List.of("Manufacturer1"), List.of("Substance1"), List.of("Product1"))
        );
        DrugApplicationDto expectedDto = new DrugApplicationDto();
        expectedDto.setApplicationNumber("123");
        expectedDto.setManufacturerNames(List.of("Manufacturer1"));
        expectedDto.setSubstanceNames(List.of("Substance1"));
        expectedDto.setProductNumbers(List.of("Product1"));

        List<DrugApplicationDto> expectedDtos = List.of(expectedDto);

        when(repository.getAllDrugApplications()).thenReturn(mockApplications);
        when(drugApplicationConverter.convertMultiple(eq(mockApplications), any())).thenReturn(expectedDtos);

        // When
        List<DrugApplicationDto> actualDtos = drugApplicationService.getDrugApplications();

        // Then
        verify(repository).getAllDrugApplications();
        verify(drugApplicationConverter, times(1)).convertMultiple(eq(mockApplications), any());
        assertThat(actualDtos).isEqualTo(expectedDtos);
    }

    @Test
    void shouldSaveDrugApplicationWhenCreateDrugApplicationCalled() {
        // Given
        DrugApplicationRequest validRequest = new DrugApplicationRequest();
        validRequest.setApplicationNumber("123");
        validRequest.setManufacturerNames(List.of("Manufacturer1"));
        validRequest.setSubstanceNames(List.of("Substance1"));
        validRequest.setProductNumbers(List.of("Product1"));

        doNothing().when(repository).insertDrugApplicationWithDetails(validRequest);

        // When
        drugApplicationService.createDrugApplication(validRequest);

        // Then
        verify(repository).insertDrugApplicationWithDetails(validRequest);
    }

    @Test
    void shouldHandleEmptyDrugApplications() {
        // Given
        when(repository.getAllDrugApplications()).thenReturn(List.of());
        when(drugApplicationConverter.convertMultiple(eq(List.of()), any())).thenReturn(List.of());

        // When
        List<DrugApplicationDto> actualDtos = drugApplicationService.getDrugApplications();

        // Then
        verify(repository).getAllDrugApplications();
        verify(drugApplicationConverter, never()).convertMultiple(eq(List.of()), any());
        assertThat(actualDtos).isEmpty();
    }

    @Test
    void shouldHandleNullMetaInOpenFdaSearchResponse() {
        // Given
        OpenFdaSearchResponse mockResponse = new OpenFdaSearchResponse();
        mockResponse.setMeta(null);
        mockResponse.setResults(List.of());

        when(searchResponseConverter.convert(mockResponse)).thenAnswer(invocation -> {
            OpenFdaSearchResponse response = invocation.getArgument(0);
            SearchResponse result = new SearchResponse();
            result.setMeta(null);
            result.setDrugApplications(response.getResults().stream()
                    .map(resultEntry -> {
                        DrugApplicationDetails dto = new DrugApplicationDetails();
                        dto.setApplicationNumber(resultEntry.getApplicationNumber());
                        return dto;
                    }).collect(Collectors.toList()));
            return result;
        });

        // When
        SearchResponse actualResponse = searchResponseConverter.convert(mockResponse);

        // Then
        verify(searchResponseConverter, times(1)).convert(mockResponse);
        assertThat(actualResponse.getDrugApplications()).isEmpty();
        assertThat(actualResponse.getMeta()).isNull();
    }
}
