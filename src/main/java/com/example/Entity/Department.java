package com.example.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Table(name="departments")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Department extends BaseEntity {

    @NotBlank(message = "Department name is required")
    @Size(max = 255, message = "Department name must not exceed 255 characters")
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "employee_count")
    private Integer employeeCount = 0;

    // One-to-Many relationship with Employees
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Employee> employees = new ArrayList<>();

    // Many-to-One relationship with Manager (who is also an Employee)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    @JsonIgnore
    private Employee manager;

    // Utility methods for managing relationships
    public void addEmployee(Employee employee) {
        if (employees == null) {
            employees = new ArrayList<>();
        }
        employees.add(employee);
        employee.setDepartment(this);
        updateEmployeeCount();
    }

    public void removeEmployee(Employee employee) {
        if (employees != null) {
            employees.remove(employee);
            employee.setDepartment(null);
            updateEmployeeCount();
        }
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    private void updateEmployeeCount() {
        this.employeeCount = employees != null ? employees.size() : 0;
    }

    // Business methods
    public boolean hasManager() {
        return manager != null;
    }

    public boolean isEmpty() {
        return employees == null || employees.isEmpty();
    }
}
