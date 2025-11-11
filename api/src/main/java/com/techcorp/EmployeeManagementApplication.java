package com.techcorp;

import java.util.List;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import com.techcorp.model.Employee;
import com.techcorp.service.EmployeeService;

@SpringBootApplication
@ImportResource("classpath:employees-beans.xml")
public class EmployeeManagementApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeManagementApplication.class);
    
    private final EmployeeService employeeService;
    private final List<Employee>  xmlEmployees;

    public EmployeeManagementApplication(
        EmployeeService employeeService,
        List<Employee> xmlEmployees
    ) {
        this.employeeService = employeeService;
        this.xmlEmployees = xmlEmployees;
    }

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }

    @PostConstruct
    public void initializeSampleData() {
        logger.info("Initializing Employee Management API...");
        logger.info("Loading {} employees from XML configuration...", xmlEmployees.size());
        
        int loadedCount = 0;
        for (Employee employee : xmlEmployees) {
            try {
            employeeService.addEmployee(employee);
                loadedCount++;
        } catch (Exception e) {
                logger.warn("Failed to load employee {}: {}", employee.getEmailAddress(), e.getMessage());
            }
        }
        
        logger.info("Successfully loaded {}/{} employees from XML configuration", loadedCount, xmlEmployees.size());
        logger.info("Employee Management API is ready and listening on port 8080");
    }
}
