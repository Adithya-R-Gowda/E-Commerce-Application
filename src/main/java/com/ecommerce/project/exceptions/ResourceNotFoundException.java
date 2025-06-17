package com.ecommerce.project.exceptions;

/**
 * ResourceNotFoundException is a custom exception class used to handle cases
 * where a requested resource is not found in the application.
 *
 * This exception is typically thrown when a specific entity, identified by a field
 * or an ID, is not available in the system. It provides meaningful error messages
 * to be returned to the client, enhancing the user experience and debugging process.
 *
 * The exception includes constructors for both string and numeric identifier fields,
 * making it adaptable to different resource identification needs.
 *
 * @author Adithya R
 */
public class ResourceNotFoundException extends RuntimeException{
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;

    public ResourceNotFoundException(String resourceName, String field, String fieldName) {
        super(String.format("%s not found with %s: %s", resourceName, field, fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }
}
