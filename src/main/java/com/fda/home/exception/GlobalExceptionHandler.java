package com.fda.home.exception;

import com.fda.home.util.ErrorCodes;
import com.openfda.generated.models.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({OpenFdaNotFoundException.class, OpenFdaApiException.class})
    public ResponseEntity<ErrorResponse> handleOpenFdaErrors(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse();

        if (ex instanceof OpenFdaNotFoundException) {
            error.setCode(ErrorCodes.SERVICE_UNAVAILABLE.name());
            error.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } else {
            error.setCode(ErrorCodes.SERVICE_UNAVAILABLE.name());
            error.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequestBody(HttpMessageNotReadableException ex) {
        return createErrorResponse(
                "BAD_REQUEST",
                "Invalid request format or missing fields",
                List.of("Request body is malformed or contains invalid data.")
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        return createErrorResponse(ErrorCodes.VALIDATION_ERROR.name(), "Validation failed.", details);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        List<String> details = List.of("Parameter '" + ex.getParameterName() + "' is missing.");

        return createErrorResponse(
                ErrorCodes.MISSING_PARAMETER.name(),
                "Required parameters are missing.",
                details
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return createErrorResponse(ErrorCodes.VALIDATION_ERROR.name(), "Validation error occurred.", details);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error("An unexpected error occurred", ex);
        return createErrorResponse(
                ErrorCodes.INTERNAL_SERVER_ERROR.name(),
                "An unexpected error occurred.",
                List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(OpenFdaBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleOpenFdaBadRequest(OpenFdaBadRequestException ex) {
        return createErrorResponse(ErrorCodes.BAD_REQUEST.name(), ex.getMessage(), List.of(ex.getMessage()));
    }

    private ErrorResponse createErrorResponse(String code, String message, List<String> details) {
        return new ErrorResponse().code(code).message(message).details(details);
    }
}
