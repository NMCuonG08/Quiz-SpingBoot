package com.example.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global Exception Handler for the Application
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @org.springframework.web.bind.annotation.InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        binder.registerCustomEditor(org.springframework.web.multipart.MultipartFile.class,
                new java.beans.PropertyEditorSupport() {
                    @Override
                    public void setAsText(String text) {
                        setValue(null);
                    }
                });
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException ex, HttpServletRequest request) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("code", ex.getCode());
        errorDetails.put("message", ex.getMessage());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("error", errorDetails);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getRequestURI());
        response.put("method", request.getMethod());

        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(NoResourceFoundException ex,
            HttpServletRequest request) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("code", "NOT_FOUND");
        errorDetails.put("message", "The requested endpoint does not exist: " + ex.getMessage());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("error", errorDetails);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getRequestURI());
        response.put("method", request.getMethod());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("code", "VALIDATION_FAILED");

        // Get the first error message
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Validation failed");

        errorDetails.put("message", message);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("error", errorDetails);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getRequestURI());
        response.put("method", request.getMethod());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, HttpServletRequest request) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("code", "INTERNAL_SERVER_ERROR");
        errorDetails.put("message", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("error", errorDetails);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", request.getRequestURI());
        response.put("method", request.getMethod());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
