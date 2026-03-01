package com.example.DTO.Response;

import com.example.Enum.Role_Enum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Employee Response DTO for returning employee data
 * Contains all fields that should be exposed to clients
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EmployeeResponse", description = "Employee information response")
public class EmployeeResponseDTO {
    
    @Schema(description = "Unique identifier of the employee", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "Full name of the employee", example = "John Doe")
    private String name;
    
    @Schema(description = "Email address of the employee", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "Role of the employee in the organization")
    private Role_Enum role;
    
    @Schema(description = "ID of the department the employee belongs to", example = "456e7890-e89b-12d3-a456-426614174001")
    private UUID departmentId;
    
    @Schema(description = "Name of the department", example = "Engineering")
    private String departmentName;
    
    @Schema(description = "ID of the employee's manager", example = "789e0123-e89b-12d3-a456-426614174002")
    private UUID managerId;
    
    @Schema(description = "Name of the employee's manager", example = "Jane Smith")
    private String managerName;
    
    @Schema(description = "Timestamp when the employee record was created")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the employee record was last updated")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Total number of leave requests made by the employee", example = "5")
    private int totalLeaveRequests;
    
    @Schema(description = "Number of pending leave requests", example = "2")
    private int pendingLeaveRequests;
    
    @Schema(description = "Number of approved leave requests", example = "3")
    private int approvedLeaveRequests;
    
    @Schema(description = "Whether the employee is active", example = "true")
    private boolean isActive;
    
    @Schema(description = "Version number for optimistic locking", example = "1")
    private Long version;
}
