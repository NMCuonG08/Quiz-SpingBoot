package com.example.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Paginated Response wrapper for list endpoints
 * Provides pagination metadata along with the data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PaginatedResponse", description = "Paginated response wrapper for list endpoints")
public class PaginatedResponse<T> {
    
    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;
    
    @Schema(description = "Human-readable message about the operation", example = "Data retrieved successfully")
    private String message;
    
    @Schema(description = "List of data items")
    private List<T> data;
    
    @Schema(description = "Pagination metadata")
    private PaginationMeta pagination;
    
    @Schema(description = "Timestamp when the response was generated")
    private LocalDateTime timestamp;
    
    @Schema(description = "Error message if the operation failed")
    private String error;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PaginationMeta", description = "Pagination metadata")
    public static class PaginationMeta {
        @Schema(description = "Current page number (0-based)", example = "0")
        private int page;
        
        @Schema(description = "Number of items per page", example = "10")
        private int size;
        
        @Schema(description = "Total number of items", example = "100")
        private long totalElements;
        
        @Schema(description = "Total number of pages", example = "10")
        private int totalPages;
        
        @Schema(description = "Whether this is the first page", example = "true")
        private boolean first;
        
        @Schema(description = "Whether this is the last page", example = "false")
        private boolean last;
        
        @Schema(description = "Whether the result set is empty", example = "false")
        private boolean empty;
    }
    
    // Static factory methods for convenience
    public static <T> PaginatedResponse<T> success(List<T> data, PaginationMeta pagination) {
        return PaginatedResponse.<T>builder()
                .success(true)
                .data(data)
                .pagination(pagination)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> PaginatedResponse<T> success(List<T> data, PaginationMeta pagination, String message) {
        return PaginatedResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .pagination(pagination)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> PaginatedResponse<T> success(List<T> data) {
        PaginationMeta pagination = PaginationMeta.builder()
                .page(0)
                .size(data.size())
                .totalElements(data.size())
                .totalPages(1)
                .first(true)
                .last(true)
                .empty(data.isEmpty())
                .build();
                
        return PaginatedResponse.<T>builder()
                .success(true)
                .data(data)
                .pagination(pagination)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> PaginatedResponse<T> error(String error) {
        return PaginatedResponse.<T>builder()
                .success(false)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
