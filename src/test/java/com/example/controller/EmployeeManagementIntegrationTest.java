package com.example.controller;

import com.example.DemoApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Employee Management System
 */
@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("Employee Management Integration Tests")
class EmployeeManagementIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("Should return all employees")
    void shouldReturnAllEmployees() throws Exception {
        mockMvc.perform(get("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should return employee statistics")
    void shouldReturnEmployeeStats() throws Exception {
        mockMvc.perform(get("/api/v1/employees/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalEmployees").exists())
                .andExpect(jsonPath("$.totalManagers").exists());
    }

    @Test
    @DisplayName("Should handle not found employee")
    void shouldHandleNotFoundEmployee() throws Exception {
        mockMvc.perform(get("/api/v1/employees/00000000-0000-0000-0000-000000000000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should validate employee creation")
    void shouldValidateEmployeeCreation() throws Exception {
        String invalidEmployee = """
            {
                "name": "",
                "email": "invalid-email",
                "role": "INVALID_ROLE"
            }
        """;

        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidEmployee))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should check API documentation endpoint")
    void shouldProvideApiDocumentation() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should provide health check")
    void shouldProvideHealthCheck() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
