package com.fda.home.functional;

import com.fda.home.BaseApiTest;
import com.fda.home.TestConstants;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class DrugsFdaApiTest extends BaseApiTest {

    @Test
    @DisplayName("POST /api/drugs - Successful creation of a new record (201)")
    void createDrugApplication_ShouldReturn201_WhenRequestIsValid() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "application_number": "ANDA203300",
                      "substance_names": ["Atorvastatin"],
                      "manufacturer_names": ["Pfizer", "Hikma"],
                      "product_numbers": ["001", "002"]
                    }
                """)
                .when()
                .post()
                .then()
                .statusCode(201);
    }

    @ParameterizedTest
    @MethodSource("invalidRequestBodies")
    @DisplayName("POST /api/drugs - Validation error (400) for invalid data")
    void createDrugApplication_ShouldReturn400_WhenRequestIsInvalid(String requestBody, String expectedMessage) {
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("message", Matchers.containsString(expectedMessage));
    }

    @Test
    @DisplayName("GET /api/drugs - Fetch all records (200 OK)")
    void getDrugApplications_ShouldReturn200_WithListOfDrugs() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

    @Test
    @DisplayName("GET /api/drugs/search - Search by manufacturer_name and brand_name (200 OK)")
    void searchDrugApplications_ShouldReturn200_WithFilteredResults() {
        WireMock.stubFor(get(urlPathEqualTo(TestConstants.BASE_PATH))
                .withQueryParam(TestConstants.QUERY_PARAM_SEARCH, equalTo("manufacturer_name:\"Pfizer\""))
                .withQueryParam(TestConstants.QUERY_PARAM_LIMIT, equalTo(TestConstants.LIMIT))
                .withQueryParam(TestConstants.QUERY_PARAM_SKIP, equalTo(TestConstants.SKIP))
                .willReturn(responseDefinition()
                        .withStatus(200)
                        .withHeader("Content-Type", TestConstants.CONTENT_TYPE_JSON)
                        .withBody("""
                                    {
                                      "meta": {
                                        "skip": 0,
                                        "limit": 10,
                                        "total": 122,
                                        "total_pages": 13
                                      },
                                      "drug_applications": [
                                        {
                                          "application_number": "NDA022030",
                                          "sponsor_name": "PFIZER",
                                          "manufacturer_names": ["U.S. Pharmaceuticals", "Pfizer Laboratories Div Pfizer Inc"],
                                          "brand_names": ["TOVIAZ"],
                                          "substance_names": ["FESOTERODINE FUMARATE"],
                                          "product_numbers": ["001", "002"]
                                        }
                                      ]
                                    }
                                """))
        );

        given()
                .contentType(ContentType.JSON)
                .queryParam(TestConstants.QUERY_PARAM_MANUFACTURER_NAME, TestConstants.MANUFACTURER_NAME)
                .queryParam(TestConstants.QUERY_PARAM_PAGE, TestConstants.PAGE)
                .queryParam(TestConstants.QUERY_PARAM_SIZE, TestConstants.SIZE)
                .when()
                .get("/search")
                .then()
                .statusCode(200)
                .body("meta.total", Matchers.equalTo(121))
                .body("meta.total_pages", Matchers.equalTo(13))
                .body("drug_applications[0].application_number", Matchers.isOneOf("NDA022030", "NDA021129"))
                .body("drug_applications[0].sponsor_name", Matchers.equalTo("VIATRIS"));
    }

    @ParameterizedTest
    @MethodSource("invalidSearchParams")
    @DisplayName("GET /api/drugs/search - Validation error (400) for invalid query parameters")
    void searchDrugApplications_ShouldReturn400_WhenInvalidQueryParams(String manufacturerName,
                                                                       String brandName,
                                                                       int page,
                                                                       int size,
                                                                       String expectedMessage) {
        given()
                .contentType(ContentType.JSON)
                .queryParam(TestConstants.QUERY_PARAM_MANUFACTURER_NAME, manufacturerName)
                .queryParam(TestConstants.QUERY_PARAM_PAGE, page)
                .queryParam(TestConstants.QUERY_PARAM_SIZE, size)
                .queryParam(TestConstants.QUERY_PARAM_BRAND_NAME, brandName)
                .when()
                .get("/search")
                .then()
                .statusCode(400)
                .body("message", Matchers.containsString(expectedMessage));
    }

    private static Stream<Arguments> invalidSearchParams() {
        return Stream.of(
                Arguments.of(null, "Lipitor", 0, 10, "searchDrugApplications.manufacturerName: size must be between 2 and 100"),
                Arguments.of("Pfizer", null, 0, 10, "searchDrugApplications.brandName: size must be between 1 and 100"),
                Arguments.of("Pfizer", "Lipitor", -1, 10, "searchDrugApplications.page: must be greater than or equal to 0"),
                Arguments.of("Pfizer", "Lipitor", 0, 100, "searchDrugApplications.size: must be less than or equal to 99")
        );
    }

    private static Stream<Arguments> invalidRequestBodies() {
        return Stream.of(
                Arguments.of("""
                            {
                              "application_number": "ANDA203300",
                              "substance_names": [],
                              "manufacturer_names": ["Pfizer"],
                              "product_numbers": ["001"]
                            }
                        """, "substanceNames: size must be between 1 and 50"),
                Arguments.of("""
                            {
                              "application_number": "ANDA203300",
                              "substance_names": ["Atorvastatin"],
                              "manufacturer_names": [],
                              "product_numbers": ["001"]
                            }
                        """, "manufacturerNames: size must be between 1 and 50"),
                Arguments.of("""
                            {
                              "application_number": "ANDA203300",
                              "substance_names": ["Atorvastatin"],
                              "manufacturer_names": ["Pfizer"],
                              "product_numbers": []
                            }
                        """, "productNumbers: size must be between 1 and 2147483647")
        );
    }
}
