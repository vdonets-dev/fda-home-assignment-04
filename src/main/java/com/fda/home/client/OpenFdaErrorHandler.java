package com.fda.home.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fda.home.exception.OpenFdaBadRequestException;
import com.fda.home.exception.OpenFdaNotFoundException;
import com.fda.home.model.dto.OpenFdaErrorResponse;
import com.fda.home.util.ErrorCodes;
import com.openfda.generated.models.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpenFdaErrorHandler {

    private final ObjectMapper objectMapper;

    /**
     * Handles HttpClientErrorException and throws corresponding custom exceptions.
     *
     * @param ex the exception to handle
     */
    public void handleHttpClientErrorException(HttpClientErrorException ex) {
        log.error("Handling HttpClientErrorException: {}", ex.getMessage());

        ErrorResponse errorResponse = parseErrorResponse(ex);
        int statusCode = ex.getStatusCode().value();

        switch (statusCode) {
            case 404 -> {
                log.warn("Not Found (404) error handled.");
                throw new OpenFdaNotFoundException(errorResponse.getMessage());
            }
            case 400 -> {
                log.warn("Bad Request (400) error handled.");
                throw new OpenFdaBadRequestException(errorResponse.getMessage(), ex);
            }
            default -> {
                log.error("Unexpected error: {}", statusCode);
                throw ex;
            }
        }
    }

    /**
     * Parses the error response from the API.
     *
     * @param ex the exception containing the response
     * @return parsed ErrorResponse
     */
    private ErrorResponse parseErrorResponse(HttpClientErrorException ex) {
        try {
            OpenFdaErrorResponse openFdaError = objectMapper.readValue(ex.getResponseBodyAsString(), OpenFdaErrorResponse.class);
            return new ErrorResponse()
                    .code(openFdaError.getErrorData().getCode())
                    .message(openFdaError.getErrorData().getMessage());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse error response", e);
            return new ErrorResponse()
                    .code(ErrorCodes.PARSE_ERROR.name())
                    .message("Failed to parse error response from OpenFDA");
        }
    }
}

