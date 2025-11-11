package com.techcorp.service;

import com.techcorp.Employee;
import com.techcorp.EmploymentStatus;
import com.techcorp.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaportGeneratorServiceTest {

    @Mock
    private EmployeeService employeeService;

    private RaportGeneratorService raportGeneratorService;

    private Employee testEmployee1;
    private Employee testEmployee2;
    private Employee testEmployee3;

    @BeforeEach
    void setUp() {
        raportGeneratorService = new RaportGeneratorService(employeeService);

        testEmployee1 = new Employee(
            "Doe", "John", "john.doe@techcorp.com",
            "TechCorp", Role.ENGINEER, 8500, EmploymentStatus.ACTIVE
        );

        testEmployee2 = new Employee(
            "Smith", "Jane", "jane.smith@innovate.com",
            "Innovate", Role.MANAGER, 12500, EmploymentStatus.ACTIVE
        );

        testEmployee3 = new Employee(
            "Johnson", "Bob", "bob.johnson@techcorp.com",
            "TechCorp", Role.INTERN, 3500, EmploymentStatus.ON_LEAVE
        );
    }

    @Test
    @DisplayName("Should generate CSV report with header and employee data")
    void shouldGenerateCsvReportWithHeaderAndEmployeeData() {
        List<Employee> employees = Arrays.asList(testEmployee1, testEmployee2);
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        assertNotNull(csv);
        assertTrue(csv.startsWith("firstName,lastName,email,company,position,salary,status"));
        assertTrue(csv.contains("John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,ACTIVE"));
        assertTrue(csv.contains("Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500,ACTIVE"));
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV with all employee fields in correct order")
    void shouldGenerateCsvWithAllEmployeeFieldsInCorrectOrder() {
        List<Employee> employees = Arrays.asList(testEmployee1);
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        String[] lines = csv.split("\n");
        assertEquals(2, lines.length); // header + 1 employee
        assertEquals("firstName,lastName,email,company,position,salary,status", lines[0]);
        assertEquals("John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,ACTIVE", lines[1]);
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV with multiple employees")
    void shouldGenerateCsvWithMultipleEmployees() {
        List<Employee> employees = Arrays.asList(testEmployee1, testEmployee2, testEmployee3);
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        String[] lines = csv.split("\n");
        assertEquals(4, lines.length); // header + 3 employees
        assertTrue(lines[1].contains("John"));
        assertTrue(lines[2].contains("Jane"));
        assertTrue(lines[3].contains("Bob"));
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV with only header when no employees")
    void shouldGenerateCsvWithOnlyHeaderWhenNoEmployees() {
        List<Employee> employees = new ArrayList<>();
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        assertNotNull(csv);
        assertEquals("firstName,lastName,email,company,position,salary,status\n", csv);
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV with different roles")
    void shouldGenerateCsvWithDifferentRoles() {
        Employee ceo = new Employee(
            "Boss", "Big", "big.boss@techcorp.com",
            "TechCorp", Role.CEO, 25000, EmploymentStatus.ACTIVE
        );
        Employee vp = new Employee(
            "Vice", "President", "vp@techcorp.com",
            "TechCorp", Role.VP, 18000, EmploymentStatus.ACTIVE
        );
        
        List<Employee> employees = Arrays.asList(ceo, vp);
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        assertTrue(csv.contains("CEO"));
        assertTrue(csv.contains("VP"));
        assertTrue(csv.contains("25000"));
        assertTrue(csv.contains("18000"));
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV with different employment statuses")
    void shouldGenerateCsvWithDifferentEmploymentStatuses() {
        Employee activeEmployee = new Employee(
            "Active", "User", "active@techcorp.com",
            "TechCorp", Role.ENGINEER, 8000, EmploymentStatus.ACTIVE
        );
        Employee onLeaveEmployee = new Employee(
            "Leave", "User", "leave@techcorp.com",
            "TechCorp", Role.ENGINEER, 8000, EmploymentStatus.ON_LEAVE
        );
        Employee terminatedEmployee = new Employee(
            "Terminated", "User", "terminated@techcorp.com",
            "TechCorp", Role.ENGINEER, 8000, EmploymentStatus.TERMINATED
        );
        
        List<Employee> employees = Arrays.asList(activeEmployee, onLeaveEmployee, terminatedEmployee);
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        assertTrue(csv.contains("ACTIVE"));
        assertTrue(csv.contains("ON_LEAVE"));
        assertTrue(csv.contains("TERMINATED"));
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV with special characters in names")
    void shouldGenerateCsvWithSpecialCharactersInNames() {
        Employee specialEmployee = new Employee(
            "O'Brien", "Patrick", "patrick@techcorp.com",
            "Tech-Corp", Role.ENGINEER, 8000, EmploymentStatus.ACTIVE
        );
        
        List<Employee> employees = Arrays.asList(specialEmployee);
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        assertTrue(csv.contains("O'Brien"));
        assertTrue(csv.contains("Tech-Corp"));
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV with different companies")
    void shouldGenerateCsvWithDifferentCompanies() {
        Employee emp1 = new Employee(
            "Smith", "John", "john@techcorp.com",
            "TechCorp", Role.ENGINEER, 8000, EmploymentStatus.ACTIVE
        );
        Employee emp2 = new Employee(
            "Brown", "Jane", "jane@innovate.com",
            "Innovate", Role.MANAGER, 12000, EmploymentStatus.ACTIVE
        );
        Employee emp3 = new Employee(
            "White", "Bob", "bob@startup.com",
            "StartUp Inc", Role.CEO, 25000, EmploymentStatus.ACTIVE
        );
        
        List<Employee> employees = Arrays.asList(emp1, emp2, emp3);
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        assertTrue(csv.contains("TechCorp"));
        assertTrue(csv.contains("Innovate"));
        assertTrue(csv.contains("StartUp Inc"));
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV with correct salary values")
    void shouldGenerateCsvWithCorrectSalaryValues() {
        Employee lowSalary = new Employee(
            "Intern", "Junior", "junior@techcorp.com",
            "TechCorp", Role.INTERN, 3000, EmploymentStatus.ACTIVE
        );
        Employee highSalary = new Employee(
            "CEO", "Senior", "ceo@techcorp.com",
            "TechCorp", Role.CEO, 25000, EmploymentStatus.ACTIVE
        );
        
        List<Employee> employees = Arrays.asList(lowSalary, highSalary);
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        assertTrue(csv.contains("3000"));
        assertTrue(csv.contains("25000"));
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV with proper line endings")
    void shouldGenerateCsvWithProperLineEndings() {
        List<Employee> employees = Arrays.asList(testEmployee1, testEmployee2);
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        // Check that each line ends with \n
        String[] lines = csv.split("\n", -1);
        // Should have header + 2 employees + empty string after last \n
        assertTrue(lines.length >= 3);
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should call employeeService.getEmployees exactly once")
    void shouldCallEmployeeServiceGetEmployeesExactlyOnce() {
        List<Employee> employees = Arrays.asList(testEmployee1);
        when(employeeService.getEmployees()).thenReturn(employees);

        raportGeneratorService.generateCsvReport();

        verify(employeeService, times(1)).getEmployees();
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    @DisplayName("Should generate CSV with email addresses in lowercase")
    void shouldGenerateCsvWithEmailAddressesInLowercase() {
        // Employee constructor converts email to lowercase
        List<Employee> employees = Arrays.asList(testEmployee1);
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        assertTrue(csv.contains("john.doe@techcorp.com"));
        assertFalse(csv.contains("JOHN.DOE@TECHCORP.COM"));
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate valid CSV structure")
    void shouldGenerateValidCsvStructure() {
        List<Employee> employees = Arrays.asList(testEmployee1, testEmployee2, testEmployee3);
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        String[] lines = csv.split("\n");
        
        // Verify header
        String header = lines[0];
        assertEquals(7, header.split(",").length);
        
        // Verify each data row has same number of columns
        for (int i = 1; i < lines.length && !lines[i].isEmpty(); i++) {
            String[] columns = lines[i].split(",");
            assertEquals(7, columns.length, "Row " + i + " should have 7 columns");
        }
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV with large number of employees")
    void shouldGenerateCsvWithLargeNumberOfEmployees() {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            employees.add(new Employee(
                "LastName" + i, "FirstName" + i, "employee" + i + "@techcorp.com",
                "TechCorp", Role.ENGINEER, 8000 + i, EmploymentStatus.ACTIVE
            ));
        }
        when(employeeService.getEmployees()).thenReturn(employees);

        String csv = raportGeneratorService.generateCsvReport();

        String[] lines = csv.split("\n");
        assertEquals(101, lines.length); // header + 100 employees
        
        verify(employeeService, times(1)).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV report filtered by company name")
    void shouldGenerateCsvReportFilteredByCompanyName() {
        List<Employee> techCorpEmployees = Arrays.asList(testEmployee1, testEmployee3);
        when(employeeService.getEmployeesByCompanyName("TechCorp")).thenReturn(techCorpEmployees);

        String csv = raportGeneratorService.generateCsvReport("TechCorp");

        assertNotNull(csv);
        assertTrue(csv.contains("TechCorp"));
        assertFalse(csv.contains("Innovate"));
        assertTrue(csv.contains("john.doe@techcorp.com"));
        assertTrue(csv.contains("bob.johnson@techcorp.com"));
        assertFalse(csv.contains("jane.smith@innovate.com"));
        
        verify(employeeService, times(1)).getEmployeesByCompanyName("TechCorp");
        verify(employeeService, never()).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV report for specific company with multiple employees")
    void shouldGenerateCsvReportForSpecificCompanyWithMultipleEmployees() {
        Employee emp1 = new Employee(
            "Smith", "Alice", "alice@innovate.com",
            "Innovate", Role.ENGINEER, 9000, EmploymentStatus.ACTIVE
        );
        Employee emp2 = new Employee(
            "Brown", "Charlie", "charlie@innovate.com",
            "Innovate", Role.MANAGER, 13000, EmploymentStatus.ACTIVE
        );
        List<Employee> innovateEmployees = Arrays.asList(emp1, emp2);
        when(employeeService.getEmployeesByCompanyName("Innovate")).thenReturn(innovateEmployees);

        String csv = raportGeneratorService.generateCsvReport("Innovate");

        String[] lines = csv.split("\n");
        assertEquals(3, lines.length); // header + 2 employees
        assertTrue(csv.contains("Innovate"));
        assertTrue(csv.contains("alice@innovate.com"));
        assertTrue(csv.contains("charlie@innovate.com"));
        
        verify(employeeService, times(1)).getEmployeesByCompanyName("Innovate");
    }

    @Test
    @DisplayName("Should generate CSV with only header when company has no employees")
    void shouldGenerateCsvWithOnlyHeaderWhenCompanyHasNoEmployees() {
        List<Employee> emptyList = new ArrayList<>();
        when(employeeService.getEmployeesByCompanyName("EmptyCompany")).thenReturn(emptyList);

        String csv = raportGeneratorService.generateCsvReport("EmptyCompany");

        assertNotNull(csv);
        assertEquals("firstName,lastName,email,company,position,salary,status\n", csv);
        
        verify(employeeService, times(1)).getEmployeesByCompanyName("EmptyCompany");
        verify(employeeService, never()).getEmployees();
    }

    @Test
    @DisplayName("Should generate CSV for company with different roles")
    void shouldGenerateCsvForCompanyWithDifferentRoles() {
        Employee ceo = new Employee(
            "Boss", "Big", "big.boss@startup.com",
            "StartUp Inc", Role.CEO, 25000, EmploymentStatus.ACTIVE
        );
        Employee engineer = new Employee(
            "Dev", "John", "john.dev@startup.com",
            "StartUp Inc", Role.ENGINEER, 8000, EmploymentStatus.ACTIVE
        );
        Employee intern = new Employee(
            "Junior", "Bob", "bob.junior@startup.com",
            "StartUp Inc", Role.INTERN, 3000, EmploymentStatus.ACTIVE
        );
        
        List<Employee> startupEmployees = Arrays.asList(ceo, engineer, intern);
        when(employeeService.getEmployeesByCompanyName("StartUp Inc")).thenReturn(startupEmployees);

        String csv = raportGeneratorService.generateCsvReport("StartUp Inc");

        assertTrue(csv.contains("CEO"));
        assertTrue(csv.contains("ENGINEER"));
        assertTrue(csv.contains("INTERN"));
        assertTrue(csv.contains("25000"));
        assertTrue(csv.contains("8000"));
        assertTrue(csv.contains("3000"));
        
        verify(employeeService, times(1)).getEmployeesByCompanyName("StartUp Inc");
    }

    @Test
    @DisplayName("Should generate CSV for company with different employment statuses")
    void shouldGenerateCsvForCompanyWithDifferentEmploymentStatuses() {
        Employee active = new Employee(
            "Active", "User", "active@company.com",
            "Company", Role.ENGINEER, 8000, EmploymentStatus.ACTIVE
        );
        Employee onLeave = new Employee(
            "Leave", "User", "leave@company.com",
            "Company", Role.ENGINEER, 8000, EmploymentStatus.ON_LEAVE
        );
        
        List<Employee> companyEmployees = Arrays.asList(active, onLeave);
        when(employeeService.getEmployeesByCompanyName("Company")).thenReturn(companyEmployees);

        String csv = raportGeneratorService.generateCsvReport("Company");

        assertTrue(csv.contains("ACTIVE"));
        assertTrue(csv.contains("ON_LEAVE"));
        
        verify(employeeService, times(1)).getEmployeesByCompanyName("Company");
    }

    @Test
    @DisplayName("Should call correct service method when company name provided")
    void shouldCallCorrectServiceMethodWhenCompanyNameProvided() {
        List<Employee> employees = Arrays.asList(testEmployee1);
        when(employeeService.getEmployeesByCompanyName("TechCorp")).thenReturn(employees);

        raportGeneratorService.generateCsvReport("TechCorp");

        verify(employeeService, times(1)).getEmployeesByCompanyName("TechCorp");
        verify(employeeService, never()).getEmployees();
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    @DisplayName("Should call correct service method when company name not provided")
    void shouldCallCorrectServiceMethodWhenCompanyNameNotProvided() {
        List<Employee> employees = Arrays.asList(testEmployee1);
        when(employeeService.getEmployees()).thenReturn(employees);

        raportGeneratorService.generateCsvReport();

        verify(employeeService, times(1)).getEmployees();
        verify(employeeService, never()).getEmployeesByCompanyName(anyString());
        verifyNoMoreInteractions(employeeService);
    }
}

