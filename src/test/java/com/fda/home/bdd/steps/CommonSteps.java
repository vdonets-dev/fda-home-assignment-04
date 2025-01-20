package com.fda.home.bdd.steps;

import com.fda.home.model.DrugApplication;
import com.fda.home.util.TestUtils;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

    @Getter
    @Setter
    private Response response;

    @LocalServerPort
    private int port;

    @SneakyThrows
    @Given("the OpenFDA API is available for manufacturer {string} with response {string}")
    public void mockOpenFdaApiForManufacturer(String manufacturer, String responseFilePath) {
        TestUtils.stubGetRequest(
                "/drug/drugsfda.json",
                TestUtils.jsonFileAsString(responseFilePath),
                Map.of(
                        "search", WireMock.equalTo("openfda.manufacturer_name:\"%s\"".formatted(manufacturer)),
                        "limit", WireMock.equalTo("10"),
                        "skip", WireMock.equalTo("0")
                ),
                200
        );
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        HttpStatus httpStatus = HttpStatus.resolve(statusCode);
        assertThat(httpStatus).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @When("I send a POST request to {string} with the following payload:")
    public void iSendAPostRequestToWithTheFollowingPayload(String endpoint, String payload) {
        response = RestAssured.given()
                .baseUri("http://localhost")
                .port(port)
                .contentType("application/json")
                .body(payload)
                .post(endpoint);
    }

    @Then("the response body should contain the following applications:")
    public void theResponseBodyShouldContainApplications(DataTable dataTable) {
        List<DrugApplication> expectedApplications = dataTable.asList(DrugApplication.class);

        List<DrugApplication> actualApplications = response.jsonPath().getList(".", DrugApplication.class);

        assertThat(actualApplications)
                .withFailMessage("Number of applications does not match. Expected: %d, Actual: %d",
                        expectedApplications.size(), actualApplications.size())
                .hasSameSizeAs(expectedApplications);

        for (DrugApplication expected : expectedApplications) {
            assertThat(actualApplications)
                    .withFailMessage("Expected application not found: " + expected.getApplicationNumber())
                    .anyMatch(actual -> actual.getApplicationNumber().equals(expected.getApplicationNumber())
                            && actual.getManufacturerNames().containsAll(expected.getManufacturerNames())
                            && actual.getSubstanceNames().containsAll(expected.getSubstanceNames())
                            && actual.getProductNumbers().containsAll(expected.getProductNumbers()));
        }
    }

    @Then("the response body should contain the following error messages:")
    public void theResponseBodyShouldContainErrorMessages(List<String> expectedErrorMessages) {
        List<String> actualErrorMessages = response.jsonPath().getList("details", String.class);

        List<String> normalizedExpected = expectedErrorMessages.stream()
                .map(msg -> msg.replace("'", "\""))
                .toList();

        List<String> normalizedActual = actualErrorMessages.stream()
                .map(msg -> msg.replace("'", "\""))
                .toList();

        assertThat(normalizedActual)
                .withFailMessage("Expected error messages were not found. Expected: %s, Actual: %s",
                        normalizedExpected, normalizedActual)
                .containsAll(normalizedExpected);
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        response = RestAssured.given()
                .baseUri("http://localhost")
                .port(port)
                .get(endpoint);
    }
}
