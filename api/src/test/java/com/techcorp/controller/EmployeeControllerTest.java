package com.techcorp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.Employee;
import com.techcorp.EmployeeService;
import com.techcorp.EmploymentStatus;
import com.techcorp.Role;
import com.techcorp.dto.EmployeeDTO;
import com.techcorp.exception.DuplicateEmailException;
import com.techcorp.exception.EmployeeNotFoundException;
import com.techcorp.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.techcorp.exception.GlobalExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeeController.class)
@ContextConfiguration(classes = {EmployeeController.class, GlobalExceptionHandler.class})
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private static Employee    testEmployee;
    private static EmployeeDTO testEmployeeDTO;

    @BeforeAll
    static void setUp() {
        testEmployee = new Employee(
            "Doe", "John", "john.doe@example.com",
            "TechCorp", Role.ENGINEER, 10000, EmploymentStatus.ACTIVE
        );
        testEmployeeDTO = new EmployeeDTO(
            "John", "Doe", "john.doe@example.com",
            "TechCorp", Role.ENGINEER, 10000, EmploymentStatus.ACTIVE
        );
    }

    @Test
    void getAllEmployees_ShouldReturn200AndListOfEmployees() throws Exception {
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeService.getEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].emailAddress").value("john.doe@example.com"))
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[0].lastName").value("Doe"))
            .andExpect(jsonPath("$[0].companyName").value("TechCorp"))
            .andExpect(jsonPath("$[0].role").value("ENGINEER"))
            .andExpect(jsonPath("$[0].salary").value(10000))
            .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    void getEmployeeByEmail_ShouldReturn200AndEmployee() throws Exception {
        when(employeeService.getEmployeeByEmail("john.doe@example.com"))
            .thenReturn(Optional.of(testEmployee));

        mockMvc.perform(get("/api/employees/john.doe@example.com"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.emailAddress").value("john.doe@example.com"))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.companyName").value("TechCorp"))
            .andExpect(jsonPath("$.role").value("ENGINEER"))
            .andExpect(jsonPath("$.salary").value(10000))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(employeeService, times(1)).getEmployeeByEmail("john.doe@example.com");
    }

    @Test
    void getEmployeeByEmail_WhenEmployeeNotFound_ShouldReturn404() throws Exception {
        when(employeeService.getEmployeeByEmail("notfound@example.com"))
            .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employees/notfound@example.com"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Employee with email notfound@example.com not found"))
            .andExpect(jsonPath("$.status").value(404));

        verify(employeeService, times(1)).getEmployeeByEmail("notfound@example.com");
    }

    @Test
    void getEmployeesByCompanyName_WithCompanyParameter_ShouldReturnFilteredEmployees() throws Exception {
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeService.getEmployeesByCompanyName("TechCorp")).thenReturn(employees);

        mockMvc.perform(get("/api/employees")
                .param("company", "TechCorp"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].companyName").value("TechCorp"))
            .andExpect(jsonPath("$[0].emailAddress").value("john.doe@example.com"))
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[0].lastName").value("Doe"))
            .andExpect(jsonPath("$[0].role").value("ENGINEER"))
            .andExpect(jsonPath("$[0].salary").value(10000))
            .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(employeeService, times(1)).getEmployeesByCompanyName("TechCorp");
    }

    @Test
    void getEmployeesByCompanyName_WithoutCompanyParameter_ShouldReturnAllEmployees() throws Exception {
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeService.getEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].companyName").value("TechCorp"))
            .andExpect(jsonPath("$[0].emailAddress").value("john.doe@example.com"))
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[0].lastName").value("Doe"))
            .andExpect(jsonPath("$[0].role").value("ENGINEER"))
            .andExpect(jsonPath("$[0].salary").value(10000))
            .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(employeeService, times(1)).getEmployees();
        verify(employeeService, never()).getEmployeesByCompanyName(anyString());
    }

    @Test
    void createEmployee_ShouldReturn201Created() throws Exception {
        when(employeeService.addEmployee(any(Employee.class))).thenReturn(1);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployeeDTO)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", "http://localhost/api/employees/john.doe@example.com"))
            .andExpect(jsonPath("$.emailAddress").value("john.doe@example.com"))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.companyName").value("TechCorp"))
            .andExpect(jsonPath("$.role").value("ENGINEER"))
            .andExpect(jsonPath("$.salary").value(10000))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(employeeService, times(1)).addEmployee(any(Employee.class));
    }

    @Test
    void createEmployee_WhenEmailExists_ShouldReturn409Conflict() throws Exception {
        when(employeeService.addEmployee(any(Employee.class)))
            .thenThrow(new DuplicateEmailException("Employee with email john.doe@example.com already exists."));

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployeeDTO)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Employee with email john.doe@example.com already exists."))
            .andExpect(jsonPath("$.status").value(409));

        verify(employeeService, times(1)).addEmployee(any(Employee.class));
    }

    @Test
    void updateEmployee_ShouldReturn200Ok() throws Exception {
        Employee updatedEmployee = new Employee(
            "Doe", "John", "john.doe@example.com",
            "TechCorp", Role.MANAGER, 12000, EmploymentStatus.ACTIVE
        );
        EmployeeDTO updatedDTO = new EmployeeDTO(
            "John", "Doe", "john.doe@example.com",
            "TechCorp", Role.MANAGER, 12000, EmploymentStatus.ACTIVE
        );
        
        doNothing().when(employeeService).updateEmployee(eq("john.doe@example.com"), any(Employee.class));
        when(employeeService.getEmployeeByEmail("john.doe@example.com"))
            .thenReturn(Optional.of(updatedEmployee));

        mockMvc.perform(put("/api/employees/john.doe@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.emailAddress").value("john.doe@example.com"))
            .andExpect(jsonPath("$.role").value("MANAGER"))
            .andExpect(jsonPath("$.salary").value(12000));

        verify(employeeService, times(1)).updateEmployee(eq("john.doe@example.com"), any(Employee.class));
        verify(employeeService, times(1)).getEmployeeByEmail("john.doe@example.com");
    }

    @Test
    void updateEmployee_WhenEmployeeNotFound_ShouldReturn404() throws Exception {
        doThrow(new EmployeeNotFoundException("Employee with email notfound@example.com not found"))
            .when(employeeService).updateEmployee(eq("notfound@example.com"), any(Employee.class));

        mockMvc.perform(put("/api/employees/notfound@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployeeDTO)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Employee with email notfound@example.com not found"))
            .andExpect(jsonPath("$.status").value(404));

        verify(employeeService, times(1)).updateEmployee(eq("notfound@example.com"), any(Employee.class));
    }

    @Test
    void deleteEmployee_ShouldReturn204NoContent() throws Exception {
        doNothing().when(employeeService).removeEmployeeByEmail("john.doe@example.com");

        mockMvc.perform(delete("/api/employees/john.doe@example.com"))
            .andExpect(status().isNoContent());

        verify(employeeService, times(1)).removeEmployeeByEmail("john.doe@example.com");
    }

    @Test
    void deleteEmployee_WhenEmployeeNotFound_ShouldReturn404() throws Exception {
        doThrow(new EmployeeNotFoundException("Employee with email notfound@example.com not found"))
            .when(employeeService).removeEmployeeByEmail("notfound@example.com");

        mockMvc.perform(delete("/api/employees/notfound@example.com"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Employee with email notfound@example.com not found"))
            .andExpect(jsonPath("$.status").value(404));

        verify(employeeService, times(1)).removeEmployeeByEmail("notfound@example.com");
    }

    @Test
    void updateEmployeeStatus_ShouldReturn200Ok() throws Exception {
        Employee employeeWithUpdatedStatus = new Employee(
            "Doe", "John", "john.doe@example.com",
            "TechCorp", Role.ENGINEER, 10000, EmploymentStatus.ON_LEAVE
        );
        
        doNothing().when(employeeService).updateEmployeeStatus("john.doe@example.com", EmploymentStatus.ON_LEAVE);
        when(employeeService.getEmployeeByEmail("john.doe@example.com"))
            .thenReturn(Optional.of(employeeWithUpdatedStatus));

        mockMvc.perform(patch("/api/employees/john.doe@example.com/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"ON_LEAVE\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.emailAddress").value("john.doe@example.com"))
            .andExpect(jsonPath("$.status").value("ON_LEAVE"));

        verify(employeeService, times(1)).updateEmployeeStatus("john.doe@example.com", EmploymentStatus.ON_LEAVE);
        verify(employeeService, times(1)).getEmployeeByEmail("john.doe@example.com");
    }

    @Test
    void getEmployeesByStatus_ShouldReturnFilteredEmployees() throws Exception {
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeService.getEmployeesByStatus(EmploymentStatus.ACTIVE)).thenReturn(employees);

        mockMvc.perform(get("/api/employees/status/ACTIVE"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(employeeService, times(1)).getEmployeesByStatus(EmploymentStatus.ACTIVE);
    }

    @Test
    void getEmployeesByStatus_WithInvalidStatus_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/employees/status/INVALID"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid status: INVALID"))
            .andExpect(jsonPath("$.status").value(400));

        verify(employeeService, never()).getEmployeesByStatus(any(EmploymentStatus.class));
    }
}

