package com.example.DTO.Request;

import com.example.Enum.Role_Enum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Employee Request DTO for creating and updating employees
 * Contains only fields that can be set by clients
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EmployeeRequest", description = "Employee creation/update request")
public class EmployeeRequestDTO {
    
    @NotBlank(message = "Employee name is required")
    @Size(max = 255, message = "Employee name must not exceed 255 characters")
    @Schema(description = "Full name of the employee", example = "John Doe", required = true)
    private String name;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Schema(description = "Email address of the employee", example = "john.doe@example.com", required = true)
    private String email;
    
    @NotNull(message = "Employee role is required")
    @Schema(description = "Role of the employee in the organization", required = true)
    private Role_Enum role;
    
    @Schema(description = "ID of the department to assign the employee to", example = "456e7890-e89b-12d3-a456-426614174001")
    private UUID departmentId;
}
