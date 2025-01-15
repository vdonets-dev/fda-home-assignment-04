package com.fda.home.exception;

import lombok.Getter;

@Getter
public class OpenFdaBadRequestException extends RuntimeException {
    public OpenFdaBadRequestException(String message) {
        super(message);
    }

    public OpenFdaBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}