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
        return createErrorResponse("BAD_REQUEST", "Invalid request format or missing fields");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream().map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage()).findFirst().orElse("Validation failed");

        return createErrorResponse(ErrorCodes.BAD_REQUEST.name(), errorMessage);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return createErrorResponse(ErrorCodes.MISSING_PARAMETER.name(), ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream().map(violation -> violation.getPropertyPath() + ": " + violation.getMessage()).findFirst().orElse("Validation error occurred");

        return createErrorResponse(ErrorCodes.VALIDATION_ERROR.name(), errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error("An unexpected error occurred", ex);
        return createErrorResponse(ErrorCodes.INTERNAL_SERVER_ERROR.name(), "An unexpected error occurred");
    }

    @ExceptionHandler(OpenFdaBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleOpenFdaBadRequest(OpenFdaBadRequestException ex) {
        return createErrorResponse(ErrorCodes.BAD_REQUEST.name(), ex.getMessage());
    }

    private ErrorResponse createErrorResponse(String code, String message) {
        return new ErrorResponse().code(code).message(message);
    }
}