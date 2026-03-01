package com.example.Service.Impl;

import com.example.Entity.Employee;
import com.example.Entity.Department;
import com.example.DTO.Request.EmployeeRequestDTO;
import com.example.DTO.Response.EmployeeResponseDTO;
import com.example.Mapper.EmployeeMapper;
import com.example.Enum.Role_Enum;
import com.example.Exception.ResourceNotFoundException;
import com.example.Exception.BusinessLogicException;
import com.example.Service.EmployeeService;
import com.example.Repository.EmployeeRepository;
import com.example.Repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * High-Performance Employee Service Implementation
 * Following SOLID Principles:
 * - Single Responsibility: Handles only employee-related operations
 * - Open/Closed: Extendable without modification
 * - Liskov Substitution: Can be substituted with any EmployeeService implementation
 * - Interface Segregation: Implements only needed methods
 * - Dependency Inversion: Depends on abstractions (repositories)
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, 
                             DepartmentRepository departmentRepository,
                             EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    @CacheEvict(value = {"employees", "employeesByDepartment", "managersList"}, allEntries = true)
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeRequestDTO) {
        // Validation
        if (!isEmployeeNameUnique(employeeRequestDTO.getName())) {
            throw new BusinessLogicException("Employee with name '" + employeeRequestDTO.getName() + "' already exists");
        }

        Employee employee = employeeMapper.toEntity(employeeRequestDTO);
        
        // Set department if provided
        if (employeeRequestDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(employeeRequestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", employeeRequestDTO.getDepartmentId()));
            employee.setDepartment(department);
        }

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDTO(savedEmployee);
    }

    @Override
    @Cacheable(value = "employees", key = "#id")
    @Transactional(readOnly = true)
    public Optional<EmployeeResponseDTO> getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toResponseDTO);
    }

    @Override
    @Cacheable(value = "employees")
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAllActiveEmployees()
                .stream()
                .map(employeeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CachePut(value = "employees", key = "#id")
    @CacheEvict(value = {"employeesByDepartment", "managersList"}, allEntries = true)
    public EmployeeResponseDTO updateEmployee(UUID id, EmployeeRequestDTO employeeRequestDTO) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        // Update fields using mapper
        employeeMapper.updateEntityFromRequestDTO(existingEmployee, employeeRequestDTO);

        // Handle department change
        if (employeeRequestDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(employeeRequestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", employeeRequestDTO.getDepartmentId()));
            existingEmployee.setDepartment(department);
        } else {
            existingEmployee.setDepartment(null);
        }

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return employeeMapper.toResponseDTO(updatedEmployee);
    }

    @Override
    @CacheEvict(value = {"employees", "employeesByDepartment", "managersList"}, allEntries = true)
    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        // Soft delete
        employee.setIsDeleted(true);
        employeeRepository.save(employee);
    }

    @Override
    @Cacheable(value = "employeesByDepartment", key = "#departmentId")
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getEmployeesByDepartment(UUID departmentId) {
        return employeeRepository.findByDepartmentId(departmentId)
                .stream()
                .map(employeeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getEmployeesByRole(Role_Enum role) {
        return employeeRepository.findByRole(role)
                .stream()
                .map(employeeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(employeeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "managersList")
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllManagers() {
        return employeeRepository.findAllManagers()
                .stream()
                .map(employeeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"employees", "employeesByDepartment"}, allEntries = true)
    public EmployeeResponseDTO assignToDepartment(UUID employeeId, UUID departmentId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));

        employee.setDepartment(department);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDTO(updatedEmployee);
    }



    @Override
    @CacheEvict(value = {"employees", "employeesByDepartment"}, allEntries = true)
    public EmployeeResponseDTO removeFromDepartment(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        employee.setDepartment(null);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDTO(updatedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getEmployeeCountByDepartment(UUID departmentId) {
        return employeeRepository.countEmployeesByDepartment(departmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmployeeNameUnique(String name) {
        return employeeRepository.findByNameIgnoreCase(name).isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canBePromotedToManager(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        // Business logic: Employee can be promoted if they have EMPLOYEE role and are in a department
        return employee.getRole() == Role_Enum.EMPLOYEE && employee.getDepartment() != null;
    }
}
