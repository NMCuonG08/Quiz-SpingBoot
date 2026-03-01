package com.example.Repository;

import com.example.Entity.Employee;
import com.example.Enum.Role_Enum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    
    // Find employees by department
    List<Employee> findByDepartmentId(UUID departmentId);
    
    // Find employees by role
    List<Employee> findByRole(Role_Enum role);
    
    // Find employee by name (case insensitive)
    Optional<Employee> findByNameIgnoreCase(String name);
    
    // Find employees by name containing (search)
    List<Employee> findByNameContainingIgnoreCase(String name);
    
    // Find active employees (not soft deleted)
    @EntityGraph(attributePaths = {"department"})
    @Query("SELECT e FROM Employee e WHERE e.isDeleted = false")
    List<Employee> findAllActiveEmployees();
    
    // Find managers
    @Query("SELECT e FROM Employee e WHERE e.role IN ('MANAGER', 'ADMIN') AND e.isDeleted = false")
    List<Employee> findAllManagers();
    
    // Count employees by department
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId AND e.isDeleted = false")
    Long countEmployeesByDepartment(@Param("departmentId") UUID departmentId);
}
