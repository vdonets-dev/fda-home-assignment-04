package com.fda.home.bdd.steps;

import com.fda.home.model.DrugApplication;
import com.fda.home.repository.DrugApplicationMapper;
import com.fda.home.util.ParameterResolver;
import com.openfda.generated.models.DrugApplicationRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class DrugApplicationSteps {

    private final DrugApplicationMapper drugApplicationMapper;
    private final ParameterResolver parameterResolver;

    @DataTableType
    public DrugApplication transform(Map<String, String> row) {
        return new DrugApplication(
                row.get("application_number"),
                parameterResolver.csvString(row.get("manufacturer_names")),
                parameterResolver.csvString(row.get("substance_names")),
                parameterResolver.csvString(row.get("product_numbers"))
        );
    }

    @Then("the database should contain the following drug applications:")
    public void theDatabaseShouldContain(DataTable dataTable) {
        List<DrugApplication> expectedApplications = dataTable.asList(DrugApplication.class);

        for (DrugApplication expected : expectedApplications) {
            DrugApplication actual = drugApplicationMapper.getDrugApplicationById(expected.getApplicationNumber());

            assertThat(actual)
                    .withFailMessage("Drug application not found: " + expected.getApplicationNumber())
                    .isNotNull();
            assertThat(actual.getManufacturerNames())
                    .containsExactlyInAnyOrderElementsOf(expected.getManufacturerNames());
            assertThat(actual.getSubstanceNames())
                    .containsExactlyInAnyOrderElementsOf(expected.getSubstanceNames());
            assertThat(actual.getProductNumbers())
                    .containsExactlyInAnyOrderElementsOf(expected.getProductNumbers());
        }
    }

    @Given("the database contains the following applications:")
    public void theDatabaseContainsApplications(DataTable dataTable) {
        List<DrugApplication> applications = dataTable.asList(DrugApplication.class);
        for (DrugApplication application : applications) {
            DrugApplicationRequest request = mapToRequest(application);
            drugApplicationMapper.insertDrugApplicationWithDetails(request);
        }
    }

    private DrugApplicationRequest mapToRequest(DrugApplication application) {
        return new DrugApplicationRequest(
                application.getApplicationNumber(),
                application.getManufacturerNames(),
                application.getSubstanceNames(),
                application.getProductNumbers()
        );
    }
}
