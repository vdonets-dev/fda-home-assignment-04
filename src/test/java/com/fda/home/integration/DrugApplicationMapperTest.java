package com.fda.home.integration;

import com.fda.home.BaseRepositoryTest;
import com.fda.home.model.DrugApplication;
import com.fda.home.repository.DrugApplicationMapper;
import com.openfda.generated.models.DrugApplicationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DrugApplicationMapperTest extends BaseRepositoryTest {

    @Autowired
    private DrugApplicationMapper drugApplicationMapper;

    @Test
    void shouldInsertAndRetrieveDrugApplication() {
        // Given
        DrugApplicationRequest request = createTestRequest("APP001", List.of("Pfizer", "Moderna"), List.of("SubstanceA", "SubstanceB"), List.of("P001", "P002"));

        // When
        drugApplicationMapper.insertDrugApplicationWithDetails(request);

        // Then
        DrugApplication insertedApplication = drugApplicationMapper.getDrugApplicationById("APP001");

        assertThat(insertedApplication)
                .isNotNull()
                .extracting(
                        DrugApplication::getApplicationNumber,
                        DrugApplication::getManufacturerNames,
                        DrugApplication::getSubstanceNames,
                        DrugApplication::getProductNumbers
                )
                .containsExactlyInAnyOrder(
                        "APP001",
                        List.of("Pfizer", "Moderna"),
                        List.of("SubstanceA", "SubstanceB"),
                        List.of("P001", "P002")
                );
    }

    @Test
    void shouldRetrieveAllDrugApplications() {
        // Given
        DrugApplicationRequest request1 = createTestRequest("APP001", List.of("Pfizer", "Moderna"), List.of("SubstanceA", "SubstanceB"), List.of("P001", "P002"));
        DrugApplicationRequest request2 = createTestRequest("APP002", List.of("Company2"), List.of("SubstanceC"), List.of("P003"));

        drugApplicationMapper.insertDrugApplicationWithDetails(request1);
        drugApplicationMapper.insertDrugApplicationWithDetails(request2);

        // When
        List<DrugApplication> applications = drugApplicationMapper.getAllDrugApplications();

        // Then
        assertThat(applications).hasSize(2);

        assertThat(applications)
                .anySatisfy(app -> {
                    assertThat(app.getApplicationNumber()).isEqualTo("APP001");
                    assertThat(app.getManufacturerNames()).containsExactlyInAnyOrder("Pfizer", "Moderna");
                    assertThat(app.getSubstanceNames()).containsExactlyInAnyOrder("SubstanceA", "SubstanceB");
                    assertThat(app.getProductNumbers()).containsExactlyInAnyOrder("P001", "P002");
                });

        assertThat(applications)
                .anySatisfy(app -> {
                    assertThat(app.getApplicationNumber()).isEqualTo("APP002");
                    assertThat(app.getManufacturerNames()).containsExactlyInAnyOrder("Company2");
                    assertThat(app.getSubstanceNames()).containsExactlyInAnyOrder("SubstanceC");
                    assertThat(app.getProductNumbers()).containsExactlyInAnyOrder("P003");
                });
    }

    @Test
    void shouldHandleEmptyResult() {
        // When
        DrugApplication result = drugApplicationMapper.getDrugApplicationById("NON_EXISTENT");

        // Then
        assertThat(result).isNull();
    }

    @Test
    void shouldHandleEmptyListsInRequest() {
        // Given
        DrugApplicationRequest request = new DrugApplicationRequest("APP003",
                List.of(), List.of(), List.of());

        // When
        drugApplicationMapper.insertDrugApplicationWithDetails(request);

        // Then
        DrugApplication insertedApplication = drugApplicationMapper.getDrugApplicationById("APP003");

        assertThat(insertedApplication)
                .isNotNull()
                .extracting(
                        DrugApplication::getApplicationNumber,
                        DrugApplication::getManufacturerNames,
                        DrugApplication::getSubstanceNames,
                        DrugApplication::getProductNumbers
                )
                .containsExactly("APP003", List.of(), List.of(), List.of());
    }

    // Создание тестовых данных с использованием параметров
    private DrugApplicationRequest createTestRequest(String applicationNumber,
                                                     List<String> manufacturers,
                                                     List<String> substances,
                                                     List<String> products) {
        return new DrugApplicationRequest(
                applicationNumber,
                manufacturers,
                substances,
                products
        );
    }
}

