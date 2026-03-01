package com.example.Exception;

import lombok.Getter;

/**
 * Custom Runtime Exception for Resource Not Found scenarios
 * Following Single Responsibility Principle
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {
    
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = "";
        this.fieldName = "";
        this.fieldValue = "";
    }
}
