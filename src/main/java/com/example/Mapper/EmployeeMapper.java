package com.example.Mapper;

import com.example.Entity.Employee;
import com.example.Entity.Department;
import com.example.DTO.Request.EmployeeRequestDTO;
import com.example.DTO.Response.EmployeeResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Employee Mapper for converting between DTOs and Entities
 * Following Single Responsibility Principle
 */
@Component
public class EmployeeMapper {

    /**
     * Convert Employee entity to EmployeeResponseDTO
     */
    public EmployeeResponseDTO toResponseDTO(Employee employee) {
        if (employee == null) {
            return null;
        }

        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .role(employee.getRole())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .managerId(employee.getDepartment() != null && employee.getDepartment().getManager() != null 
                    ? employee.getDepartment().getManager().getId() : null)
                .managerName(employee.getDepartment() != null && employee.getDepartment().getManager() != null 
                    ? employee.getDepartment().getManager().getName() : null)
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .totalLeaveRequests(employee.getLeaveRequests() != null ? employee.getLeaveRequests().size() : 0)
                .pendingLeaveRequests(0) // TODO: Calculate based on leave status
                .approvedLeaveRequests(0) // TODO: Calculate based on leave status
                .isActive(!employee.getIsDeleted())
                .version(employee.getVersion())
                .build();
    }

    /**
     * Convert EmployeeRequestDTO to Employee entity
     */
    public Employee toEntity(EmployeeRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setName(requestDTO.getName());
        employee.setEmail(requestDTO.getEmail());
        employee.setRole(requestDTO.getRole());
        // Department will be set separately in the service layer
        
        return employee;
    }

    /**
     * Update existing Employee entity with data from EmployeeRequestDTO
     */
    public void updateEntityFromRequestDTO(Employee employee, EmployeeRequestDTO requestDTO) {
        if (employee == null || requestDTO == null) {
            return;
        }

        employee.setName(requestDTO.getName());
        employee.setEmail(requestDTO.getEmail());
        employee.setRole(requestDTO.getRole());
        // Department will be updated separately in the service layer
    }

    /**
     * Convert list of Employee entities to list of EmployeeResponseDTOs
     */
    public List<EmployeeResponseDTO> toResponseDTOList(List<Employee> employees) {
        if (employees == null) {
            return null;
        }

        return employees.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
