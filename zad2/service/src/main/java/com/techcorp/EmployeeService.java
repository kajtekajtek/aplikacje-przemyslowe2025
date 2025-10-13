package com.techcorp;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmployeeService 
{
    private List<Employee> employees;

    public EmployeeService() { this.employees = new ArrayList<>(); }
    
    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }

        boolean emailExists = this.employees.stream()
            .anyMatch(e -> e.getEmailAddress()
                            .equalsIgnoreCase(employee.getEmailAddress())
            );

        if (emailExists) {
            throw new IllegalArgumentException("Employee with email " + employee.getEmailAddress() + " already exists.");
        }

        this.employees.add(employee);
    }

    public void removeEmployee(Employee employee) { 
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        this.employees.remove(employee);
    }

    public List<Employee> getEmployees() { return this.employees; }
    public List<Employee> getEmployeesByCompanyName(String companyName) {
        return this.employees.stream()
            .filter(e -> e.getCompanyName().equalsIgnoreCase(companyName))
            .collect(Collectors.toList());
    }
    public Map<Role, List<Employee>> getEmployeesByRole() {
        return this.employees.stream()
            .collect(Collectors.groupingBy(Employee::getRole));
    }
    public List<Employee> getEmployeesAlphabetically() {
        return this.employees.stream()
            .sorted(Comparator.comparing(Employee::getLastName))
            .collect(Collectors.toList());
    }
    
    public Map<Role, Long> getEmployeeCountByRole() {
        return this.employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getRole, Collectors.counting()
            ));
    }

    public Optional<Employee> getEmployeeWithHighestSalary() {
        return this.employees.stream()
            .max(Comparator.comparing(Employee::getSalary));
    }

    public Double getAverageSalary() {
        return this.employees.stream()
            .mapToDouble(Employee::getSalary)
            .average()
            .orElse(0.0);
    }
    
    public void printEmployees() {
        this.employees.forEach(System.out::println);
    }

}
