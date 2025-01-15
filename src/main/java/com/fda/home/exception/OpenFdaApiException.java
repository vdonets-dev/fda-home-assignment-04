package com.fda.home.exception;

import lombok.Getter;

@Getter
public class OpenFdaApiException extends RuntimeException {
    public OpenFdaApiException(String message) {
        super(message);
    }

    public OpenFdaApiException(String message, Throwable cause) {
        super(message, cause);
    }
}