package com.fda.home.util;

public enum ErrorCodes {
    NOT_FOUND,
    BAD_REQUEST,
    SERVICE_UNAVAILABLE,
    INTERNAL_SERVER_ERROR,
    MISSING_PARAMETER,
    PARSE_ERROR,
    VALIDATION_ERROR;

    @Override
    public String toString() {
        return name();
    }
}
