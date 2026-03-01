package com.example.Repository;

import com.example.Entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    
    // Find department by name (case insensitive)
    Optional<Department> findByNameIgnoreCase(String name);
    
    // Find departments by name containing (search)
    List<Department> findByNameContainingIgnoreCase(String name);
    
    // Find active departments (not soft deleted)
    @Query("SELECT d FROM Department d WHERE d.isDeleted = false")
    List<Department> findAllActiveDepartments();
    
    // Find departments with no manager
    @Query("SELECT d FROM Department d WHERE d.manager IS NULL AND d.isDeleted = false")
    List<Department> findDepartmentsWithoutManager();
    
    // Find departments by manager
    List<Department> findByManagerId(UUID managerId);
}
