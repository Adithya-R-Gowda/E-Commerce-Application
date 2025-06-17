package com.ecommerce.project.exceptions;

/**
 * APIException is a custom exception class used to handle application-specific
 * errors that are not covered by standard exceptions.
 *
 * This exception typically represents business logic errors or invalid operations
 * within the application and is intended to provide meaningful error messages
 * for the client.
 *
 * It extends {@link RuntimeException} to enable unchecked exception behavior,
 * ensuring flexibility in its usage without mandatory exception handling.
 *
 * @author Adithya R
 */
public class APIException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public APIException() {
    }

    public APIException(String message) {
        super(message);
    }
}
