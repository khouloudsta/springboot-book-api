package de.khouloudsta.bookapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.LinkedHashMap;
import org.springframework.http.converter.HttpMessageNotReadableException;

/**
 * Converts application exceptions into structured JSON error responses.
 * Validation and unreadable request bodies return HTTP 400, missing resources
 * return HTTP 404, and unexpected exceptions return HTTP 500.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /** Returns HTTP 404 with the missing-resource message. */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", ex.getMessage(),
                        "status", 404
                ));
    }

    /** Returns HTTP 500 without exposing internal exception details. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherErrors(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "An unexpected error occurred",
                        "status", 500
                ));
    }

    /** Returns HTTP 400 with validation messages grouped by field name. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Validation failed",
                "fields", errors
        ));

    }

    /** Returns HTTP 400 when JSON cannot be converted into the request DTO. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(
            HttpMessageNotReadableException ex
    ) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid JSON or incompatible field type",
                "status", 400
        ));
    }
}
