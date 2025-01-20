package com.fda.home.bdd.steps;

import com.openfda.generated.models.DrugApplicationDetails;
import com.openfda.generated.models.SearchResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class SearchResponseSteps {

    private final CommonSteps commonSteps;

    @LocalServerPort
    private int port;

    @When("I send a GET request to {string} with the following query parameters:")
    public void iSendAGetRequestWithQueryParameters(String path, Map<String, String> params) {
        Response response = commonSteps.getResponse();
        response = io.restassured.RestAssured.given()
                .baseUri("http://localhost")
                .port(port)
                .queryParams(params)
                .get(path);
        commonSteps.setResponse(response);
    }

    @Then("the response should include only applications with:")
    public void theResponseShouldIncludeOnlyApplicationsWith(DataTable dataTable) {
        List<Map<String, String>> expectedEntries = dataTable.asMaps(String.class, String.class);

        SearchResponse searchResponse = commonSteps.getResponse().as(SearchResponse.class);
        List<DrugApplicationDetails> actualApplications = searchResponse.getDrugApplications();

        for (Map<String, String> entry : expectedEntries) {
            String expectedManufacturerName = entry.get("manufacturer_name");

            assertThat(actualApplications)
                    .withFailMessage("Some applications do not contain the expected manufacturer_name: " + expectedManufacturerName)
                    .allMatch(application -> application.getManufacturerNames()
                            .stream()
                            .anyMatch(name -> name.contains(expectedManufacturerName)));
        }
    }

    @Then("the response body for search should contain:")
    public void theResponseBodyForSearchShouldContain(DataTable dataTable) {
        Map<String, String> expectedMeta = dataTable.asMap(String.class, String.class);

        SearchResponse searchResponse = commonSteps.getResponse().as(SearchResponse.class);

        assertThat(searchResponse).isNotNull();
        assertThat(searchResponse.getMeta().getSkip()).isEqualTo(Integer.parseInt(expectedMeta.get("meta.skip")));
        assertThat(searchResponse.getMeta().getLimit()).isEqualTo(Integer.parseInt(expectedMeta.get("meta.limit")));
        assertThat(searchResponse.getMeta().getTotal()).isEqualTo(Integer.parseInt(expectedMeta.get("meta.total")));
    }

    @Then("the response should include the following applications:")
    public void theResponseShouldIncludeTheFollowingApplications(DataTable dataTable) {
        List<Map<String, String>> expectedEntries = dataTable.asMaps(String.class, String.class);
        String expectedManufacturerName = expectedEntries.get(0).get("manufacturer_name");

        SearchResponse searchResponse = commonSteps.getResponse().as(SearchResponse.class);
        List<DrugApplicationDetails> actualApplications = searchResponse.getDrugApplications();

        assertThat(actualApplications)
                .withFailMessage("Some applications do not contain the expected manufacturer_name: " + expectedManufacturerName)
                .allMatch(application -> application.getManufacturerNames()
                        .stream()
                        .anyMatch(name -> name.contains(expectedManufacturerName)));
    }
}
