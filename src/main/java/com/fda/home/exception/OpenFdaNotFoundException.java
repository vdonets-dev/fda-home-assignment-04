package com.fda.home.exception;

public class OpenFdaNotFoundException extends RuntimeException {
    public OpenFdaNotFoundException(String message) {
        super(message);
    }

    public OpenFdaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
