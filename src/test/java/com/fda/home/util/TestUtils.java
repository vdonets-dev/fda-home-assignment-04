package com.fda.home.util;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import org.apache.commons.io.IOUtils;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import wiremock.net.javacrumbs.jsonunit.core.Configuration;
import wiremock.net.javacrumbs.jsonunit.core.Option;
import wiremock.net.javacrumbs.jsonunit.core.internal.Diff;
import wiremock.net.javacrumbs.jsonunit.core.internal.JsonUtils;
import wiremock.net.javacrumbs.jsonunit.core.internal.Node;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TestUtils {

    /**
     * Loads a JSON file as a string from the resources folder.
     *
     * @param filename The name of the JSON file.
     * @return JSON content as a string.
     * @throws RuntimeException If the file cannot be loaded.
     */
    public static String jsonFileAsString(String filename) throws IOException{
        return IOUtils.resourceToString(filename, StandardCharsets.UTF_8);
    }

    /**
     * Stubs outgoing GET request using provided URL, response payload, query parameters, and HTTP status.
     *
     * @param url             the URL of the request
     * @param responsePayload the returned body
     * @param queryParameters query parameters
     * @param httpStatus      the response HTTP status
     */
    public static void stubGetRequest(String url, String responsePayload, Map<String, StringValuePattern> queryParameters, int httpStatus) {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(url))
                .withQueryParams(queryParameters)
                .willReturn(new ResponseDefinitionBuilder()
                        .withStatus(httpStatus)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responsePayload)));
    }

    /**
     * Custom matcher that matches json.
     * @param expectedJson file to be compared with.
     */
    public static Matcher<Boolean> matchesJson(String expectedJson) {
        return new CustomMatcher<>(expectedJson) {
            @Override
            public boolean matches(Object actualJson) {
                Node expectedNode = JsonUtils.convertToJson(expectedJson,  "expected json", true);
                Node actualNode = JsonUtils.convertToJson(actualJson,  "actual json", true);
                Diff diff = Diff.create(expectedNode, actualNode, "actual", "", Configuration.empty().withOptions(Option.IGNORING_ARRAY_ORDER));
                return diff.similar();
            }
        };
    }
}
