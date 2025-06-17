package com.ecommerce.project.exceptions;

import com.ecommerce.project.payload.APIResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * MyGlobalExceptionHandler is a global exception handler for managing
 * and standardizing exception responses throughout the e-commerce application.
 *
 * This class uses @RestControllerAdvice to centralize the handling of common
 * exceptions such as validation errors, resource not found, and custom API exceptions.
 *
 * Each method corresponds to specific exception types, ensuring appropriate
 * HTTP status codes and user-friendly error messages are returned to the client.
 *
 * @author Adithya R
 */
@RestControllerAdvice
public class MyGlobalExceptionHandler {

    /**
     * Handles validation errors arising from @Valid annotations.
     * Extracts field-specific error messages and returns a map of field names to error messages.
     *
     * @param e the MethodArgumentNotValidException thrown during validation
     * @return a ResponseEntity containing the map of validation errors and a BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            response.put(fieldName, message);
        });
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ResourceNotFoundException when a requested resource is not found.
     * Returns a custom API response with the error message and NOT_FOUND status.
     *
     * @param e the ResourceNotFoundException thrown when a resource is unavailable
     * @return a ResponseEntity containing an APIResponse object and a NOT_FOUND status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles custom API exceptions that indicate specific API-related errors.
     * Returns a custom API response with the error message and BAD_REQUEST status.
     *
     * @param e the APIException thrown due to specific application errors
     * @return a ResponseEntity containing an APIResponse object and a BAD_REQUEST status
     */
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException e) {
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors arising from ConstraintViolationException.
     * Aggregates validation error messages into a single response string.
     *
     * @param ex the ConstraintViolationException thrown during validation
     * @return a ResponseEntity containing the aggregated validation error message and a BAD_REQUEST status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        StringBuilder errors = new StringBuilder("Validation errors: ");
        ex.getConstraintViolations().forEach(violation ->
                errors.append(violation.getPropertyPath())
                        .append(" ")
                        .append(violation.getMessage())
                        .append("; ")
        );
        return new ResponseEntity<>(errors.toString(), HttpStatus.BAD_REQUEST);
    }
}
