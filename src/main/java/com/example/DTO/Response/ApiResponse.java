package com.example.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API Response wrapper for all endpoints
 * Provides consistent response structure across the application
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ApiResponse", description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;

    @Schema(description = "Human-readable message about the operation", example = "Operation completed successfully")
    private String message;

    @Schema(description = "The actual data returned by the operation")
    private T data;

    @Schema(description = "Error message if the operation failed", example = "Validation failed")
    private String error;

    @Schema(description = "Timestamp when the response was generated")
    private LocalDateTime timestamp;

    @Schema(description = "Request path that generated this response", example = "/api/v1/employees")
    private String path;

    @Schema(description = "HTTP status code", example = "200")
    private int statusCode;

    // Static factory methods for convenience
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(200)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String error, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
