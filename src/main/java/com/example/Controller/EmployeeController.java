package com.example.Controller;

import com.example.DTO.Request.EmployeeRequestDTO;
import com.example.DTO.Response.EmployeeResponseDTO;
import com.example.DTO.Response.ApiResponse;
import com.example.DTO.Response.PaginatedResponse;
import com.example.Service.EmployeeService;
import com.example.Enum.Role_Enum;
import com.example.Exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * High-Performance Employee REST Controller
 * Following RESTful principles and SOLID design
 */
@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Create a new employee")
    @PreAuthorize("hasAuthority('CREATE_EMPLOYEE') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        EmployeeResponseDTO createdEmployee = employeeService.createEmployee(employeeRequestDTO);
        ApiResponse<EmployeeResponseDTO> response = ApiResponse.success(createdEmployee,
                "Employee created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all employees")
    @GetMapping
    public ResponseEntity<PaginatedResponse<EmployeeResponseDTO>> getAllEmployees(
            @PageableDefault(size = 10, page = 0, sort = "name") Pageable pageable) {
        List<EmployeeResponseDTO> employees = employeeService.getAllEmployees(pageable);
        PaginatedResponse<EmployeeResponseDTO> response = PaginatedResponse.success(employees);
        response.setMessage("Employees retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get employee by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable UUID id) {
        EmployeeResponseDTO employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        ApiResponse<EmployeeResponseDTO> response = ApiResponse.success(employee, "Employee retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update employee")
    @PreAuthorize("hasAuthority('UPDATE_EMPLOYEE') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        EmployeeResponseDTO updatedEmployee = employeeService.updateEmployee(id, employeeRequestDTO);
        ApiResponse<EmployeeResponseDTO> response = ApiResponse.success(updatedEmployee,
                "Employee updated successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete employee")
    @PreAuthorize("hasAuthority('DELETE_EMPLOYEE') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        ApiResponse<Void> response = ApiResponse.success(null, "Employee deleted successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get employees by department")
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<PaginatedResponse<EmployeeResponseDTO>> getEmployeesByDepartment(
            @PathVariable UUID departmentId) {
        List<EmployeeResponseDTO> employees = employeeService.getEmployeesByDepartment(departmentId);
        PaginatedResponse<EmployeeResponseDTO> response = PaginatedResponse.success(employees);
        response.setMessage("Employees by department retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get employees by role")
    @GetMapping("/role/{role}")
    public ResponseEntity<PaginatedResponse<EmployeeResponseDTO>> getEmployeesByRole(@PathVariable Role_Enum role) {
        List<EmployeeResponseDTO> employees = employeeService.getEmployeesByRole(role);
        PaginatedResponse<EmployeeResponseDTO> response = PaginatedResponse.success(employees);
        response.setMessage("Employees by role retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search employees by name")
    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<EmployeeResponseDTO>> searchEmployees(@RequestParam String name) {
        List<EmployeeResponseDTO> employees = employeeService.searchEmployeesByName(name);
        PaginatedResponse<EmployeeResponseDTO> response = PaginatedResponse.success(employees);
        response.setMessage("Employee search completed successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all managers")
    @GetMapping("/managers")
    public ResponseEntity<PaginatedResponse<EmployeeResponseDTO>> getAllManagers() {
        List<EmployeeResponseDTO> managers = employeeService.getAllManagers();
        PaginatedResponse<EmployeeResponseDTO> response = PaginatedResponse.success(managers);
        response.setMessage("Managers retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Assign employee to department")
    @PutMapping("/{employeeId}/department/{departmentId}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> assignToDepartment(
            @PathVariable UUID employeeId,
            @PathVariable UUID departmentId) {
        EmployeeResponseDTO employee = employeeService.assignToDepartment(employeeId, departmentId);
        ApiResponse<EmployeeResponseDTO> response = ApiResponse.success(employee,
                "Employee assigned to department successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove employee from department")
    @DeleteMapping("/{employeeId}/department")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> removeFromDepartment(@PathVariable UUID employeeId) {
        EmployeeResponseDTO employee = employeeService.removeFromDepartment(employeeId);
        ApiResponse<EmployeeResponseDTO> response = ApiResponse.success(employee,
                "Employee removed from department successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get employee statistics")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getEmployeeStats() {
        java.util.Map<String, Object> stats = java.util.Map.of(
                "totalEmployees", employeeService.getTotalEmployeeCount(),
                "totalManagers", employeeService.getAllManagers().size());
        ApiResponse<java.util.Map<String, Object>> response = ApiResponse.success(stats,
                "Employee statistics retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check if employee name is unique")
    @GetMapping("/check-name")
    public ResponseEntity<ApiResponse<Boolean>> checkNameUnique(@RequestParam String name) {
        boolean isUnique = employeeService.isEmployeeNameUnique(name);
        ApiResponse<Boolean> response = ApiResponse.success(isUnique, "Name uniqueness check completed");
        return ResponseEntity.ok(response);
    }
}
