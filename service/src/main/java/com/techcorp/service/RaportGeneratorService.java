package com.techcorp.service;

import org.springframework.stereotype.Service;
import com.techcorp.Employee;
import java.util.List;

@Service
public class RaportGeneratorService {
    private final EmployeeService employeeService;

    public RaportGeneratorService(
        EmployeeService employeeService
    ) {
        this.employeeService = employeeService;
    }

    public String generateCsvReport() {
        return generateCsvReport(employeeService.getEmployees());
    }

    public String generateCsvReport(String companyName) {
        return generateCsvReport(employeeService.getEmployeesByCompanyName(companyName));
    }

    private String generateCsvReport(List<Employee> employees) {
        StringBuilder csv = new StringBuilder();
        csv.append("firstName,lastName,email,company,position,salary,status\n");
        for (Employee employee : employees) {
            csv.append(employee.getFirstName()).append(",");
            csv.append(employee.getLastName()).append(",");
            csv.append(employee.getEmailAddress()).append(",");
            csv.append(employee.getCompanyName()).append(",");
            csv.append(employee.getRole()).append(",");
            csv.append(employee.getSalary()).append(",");
            csv.append(employee.getStatus()).append("\n");
        }
        return csv.toString();
    }

}
