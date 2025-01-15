package com.fda.home.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OpenFdaErrorResponse {
    @JsonProperty("error")
    private ErrorData errorData;

    @Data
    public static class ErrorData {
        private String code;
        private String message;
    }
}

