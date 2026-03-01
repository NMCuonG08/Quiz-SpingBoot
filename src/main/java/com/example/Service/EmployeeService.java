package com.example.Service;

import com.example.DTO.Request.EmployeeRequestDTO;
import com.example.DTO.Response.EmployeeResponseDTO;
import com.example.Enum.Role_Enum;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Employee Service Interface - Following Interface Segregation Principle
 * Defines contract for employee operations
 */
public interface EmployeeService {
    
    // CRUD Operations
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeRequestDTO);
    Optional<EmployeeResponseDTO> getEmployeeById(UUID id);
    List<EmployeeResponseDTO> getAllEmployees(Pageable pageable);
    EmployeeResponseDTO updateEmployee(UUID id, EmployeeRequestDTO employeeRequestDTO);
    void deleteEmployee(UUID id);
    
    // Business Operations
    List<EmployeeResponseDTO> getEmployeesByDepartment(UUID departmentId);
    List<EmployeeResponseDTO> getEmployeesByRole(Role_Enum role);
    List<EmployeeResponseDTO> searchEmployeesByName(String name);
    List<EmployeeResponseDTO> getAllManagers();
    
    // Department Assignment
    EmployeeResponseDTO assignToDepartment(UUID employeeId, UUID departmentId);
    EmployeeResponseDTO removeFromDepartment(UUID employeeId);
    
    // Statistics
    long getTotalEmployeeCount();
    long getEmployeeCountByDepartment(UUID departmentId);
    
    // Validation
    boolean isEmployeeNameUnique(String name);
    boolean canBePromotedToManager(UUID employeeId);
}
