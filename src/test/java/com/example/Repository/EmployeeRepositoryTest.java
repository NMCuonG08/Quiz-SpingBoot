package com.example.Repository;

import com.example.Entity.Employee;
import com.example.Entity.Department;
import com.example.Enum.Role_Enum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive Repository Tests
 * Testing data access layer with H2 in-memory database
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Employee Repository Tests")
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee testEmployee;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        // Create test department
        testDepartment = new Department();
        testDepartment.setName("IT Department");
        testDepartment.setDescription("Information Technology");
        entityManager.persistAndFlush(testDepartment);

        // Create test employee
        testEmployee = new Employee();
        testEmployee.setName("John Doe");
        testEmployee.setEmail("john.doe@example.com");
        testEmployee.setRole(Role_Enum.EMPLOYEE);
        testEmployee.setDepartment(testDepartment);
        entityManager.persistAndFlush(testEmployee);
    }

    @Test
    @DisplayName("Should find employee by name ignoring case")
    void shouldFindEmployeeByNameIgnoreCase() {
        // When
        Optional<Employee> found = employeeRepository.findByNameIgnoreCase("JOHN DOE");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should find employees by department ID")
    void shouldFindEmployeesByDepartmentId() {
        // When
        List<Employee> employees = employeeRepository.findByDepartmentId(testDepartment.getId());

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should find employees by role")
    void shouldFindEmployeesByRole() {
        // Given
        Employee manager = new Employee();
        manager.setName("Jane Manager");
        manager.setEmail("jane.manager@example.com");
        manager.setRole(Role_Enum.MANAGER);
        entityManager.persistAndFlush(manager);

        // When
        List<Employee> employees = employeeRepository.findByRole(Role_Enum.EMPLOYEE);
        List<Employee> managers = employeeRepository.findByRole(Role_Enum.MANAGER);

        // Then
        assertThat(employees).hasSize(1);
        assertThat(managers).hasSize(1);
        assertThat(employees.get(0).getName()).isEqualTo("John Doe");
        assertThat(managers.get(0).getName()).isEqualTo("Jane Manager");
    }

    @Test
    @DisplayName("Should find employees by name containing")
    void shouldFindEmployeesByNameContaining() {
        // Given
        Employee anotherEmployee = new Employee();
        anotherEmployee.setName("John Smith");
        anotherEmployee.setEmail("john.smith@example.com");
        anotherEmployee.setRole(Role_Enum.EMPLOYEE);
        entityManager.persistAndFlush(anotherEmployee);

        // When
        List<Employee> employees = employeeRepository.findByNameContainingIgnoreCase("john");

        // Then
        assertThat(employees).hasSize(2);
    }

    @Test
    @DisplayName("Should find all active employees")
    void shouldFindAllActiveEmployees() {
        // Given
        Employee deletedEmployee = new Employee();
        deletedEmployee.setName("Deleted Employee");
        deletedEmployee.setEmail("deleted@example.com");
        deletedEmployee.setRole(Role_Enum.EMPLOYEE);
        deletedEmployee.softDelete(); // Soft delete
        entityManager.persistAndFlush(deletedEmployee);

        // When
        List<Employee> activeEmployees = employeeRepository.findAllActiveEmployees();

        // Then
        assertThat(activeEmployees).hasSize(1);
        assertThat(activeEmployees.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should find all managers")
    void shouldFindAllManagers() {
        // Given
        Employee manager = new Employee();
        manager.setName("Manager One");
        manager.setEmail("manager1@example.com");
        manager.setRole(Role_Enum.MANAGER);
        entityManager.persistAndFlush(manager);

        Employee admin = new Employee();
        admin.setName("Admin One");
        admin.setEmail("admin1@example.com");
        admin.setRole(Role_Enum.ADMIN);
        entityManager.persistAndFlush(admin);

        // When
        List<Employee> managers = employeeRepository.findAllManagers();

        // Then
        assertThat(managers).hasSize(2);
    }

    @Test
    @DisplayName("Should count employees by department")
    void shouldCountEmployeesByDepartment() {
        // Given
        Employee anotherEmployee = new Employee();
        anotherEmployee.setName("Another Employee");
        anotherEmployee.setEmail("another@example.com");
        anotherEmployee.setRole(Role_Enum.EMPLOYEE);
        anotherEmployee.setDepartment(testDepartment);
        entityManager.persistAndFlush(anotherEmployee);

        // When
        Long count = employeeRepository.countEmployeesByDepartment(testDepartment.getId());

        // Then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should save employee with all relationships")
    void shouldSaveEmployeeWithRelationships() {
        // Given
        Employee newEmployee = new Employee();
        newEmployee.setName("New Employee");
        newEmployee.setEmail("new@example.com");
        newEmployee.setRole(Role_Enum.HR);
        newEmployee.setDepartment(testDepartment);

        // When
        Employee saved = employeeRepository.save(newEmployee);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getVersion()).isNotNull();
        assertThat(saved.getDepartment()).isEqualTo(testDepartment);
    }
}
