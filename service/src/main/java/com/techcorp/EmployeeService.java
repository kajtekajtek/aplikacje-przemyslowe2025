package com.techcorp;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.techcorp.exception.EmployeeNotFoundException;
import com.techcorp.exception.DuplicateEmailException;

@Service
public class EmployeeService 
{
    private List<Employee> employees;

    public EmployeeService() { this.employees = new ArrayList<>(); }
    
    public int addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }

        boolean emailExists = this.employees.stream()
            .anyMatch(e -> e.getEmailAddress()
                            .equalsIgnoreCase(employee.getEmailAddress())
            );

        if (emailExists) {
            throw new com.techcorp.exception.DuplicateEmailException(
                "Employee with email " + employee.getEmailAddress() + " already exists."
            );
        }

        return this.employees.add(employee) ? 1 : 0;
    }

    public void removeEmployeeByEmail(String email) {
        Employee employee = getEmployeeByEmail(email)
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + email + " not found"
            ));
        this.employees.remove(employee);
    }

    public int removeEmployee(Employee employee) { 
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        return this.employees.remove(employee) ? 1 : 0;
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

    public Optional<Employee> getEmployeeByEmail(String email) {
        return this.employees.stream()
            .filter(e -> e.getEmailAddress().equalsIgnoreCase(email))
            .findFirst();
    }

    public List<Employee> getEmployeesByStatus(EmploymentStatus status) {
        return this.employees.stream()
            .filter(e -> e.getStatus() == status)
            .collect(Collectors.toList());
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

    public List<Employee> validateSalaryConsistency() {
        return this.employees.stream()
            .filter(e -> e.getSalary() < e.getRole().getBaseSalary())
            .collect(Collectors.toList());
    }

    public Map<String, CompanyStatistics> getCompanyStatistics() {
        return this.employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getCompanyName,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    employeeList -> {
                        long count = employeeList.size();
                        double avgSalary = employeeList.stream()
                            .mapToDouble(Employee::getSalary)
                            .average()
                            .orElse(0.0);
                        String highestPaidName = employeeList.stream()
                            .max(Comparator.comparing(Employee::getSalary))
                            .map(Employee::getFullName)
                            .orElse("N/A");
                        return new CompanyStatistics(count, avgSalary, highestPaidName);
                    }
                )
            ));
    }

    public void updateEmployeeStatus(String email, EmploymentStatus status) {
        Employee employee = getEmployeeByEmail(email)
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + email + " not found"
            ));
        employee.setStatus(status);
    }

    public void updateEmployee(String email, Employee updatedEmployee) {
        Employee existingEmployee = getEmployeeByEmail(email)
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + email + " not found"
            ));
        
        if (!email.equalsIgnoreCase(updatedEmployee.getEmailAddress())) {
            boolean emailExists = this.employees.stream()
                .anyMatch(e -> e.getEmailAddress().equalsIgnoreCase(updatedEmployee.getEmailAddress())
                            && !e.equals(existingEmployee));
            
            if (emailExists) {
                throw new DuplicateEmailException(
                    "Employee with email " + updatedEmployee.getEmailAddress() + " already exists"
                );
            }
        }

        int index = this.employees.indexOf(existingEmployee);
        if (index != -1) {
            this.employees.set(index, updatedEmployee);
        }
    }

}
