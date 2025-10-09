package com.techcorp;

import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class App {
    private static EmployeeService employeeService;
    private static Scanner         scanner;

    public static void main(String[] args) {
        employeeService = new EmployeeService();
        scanner         = new Scanner(System.in);
        
        initializeSampleData();
        
        System.out.println("==============================================");
        System.out.println("   Welcome to Employee Management System");
        System.out.println("==============================================\n");
        
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getMenuChoice();
            
            System.out.println();
            switch (choice) {
                case 1:
                    addNewEmployee();
                    break;
                case 2:
                    removeEmployee();
                    break;
                case 3:
                    listAllEmployees();
                    break;
                case 4:
                    listEmployeesByCompany();
                    break;
                case 5:
                    listEmployeesByRole();
                    break;
                case 6:
                    listEmployeesAlphabetically();
                    break;
                case 7:
                    showEmployeeCountByRole();
                    break;
                case 8:
                    showEmployeeWithHighestSalary();
                    break;
                case 9:
                    showAverageSalary();
                    break;
                case 10:
                    showStatistics();
                    break;
                case 0:
                    System.out.println("Thank you for using Employee Management System!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    private static void initializeSampleData() {
        employeeService.addEmployee(new Employee(
            "Smith", "John", "john.smith@techcorp.com", "TechCorp", Role.CEO, 25000
        ));
        employeeService.addEmployee(new Employee(
            "Johnson", "Emma", "emma.johnson@techcorp.com", "TechCorp", Role.VP, 18000
        ));
        employeeService.addEmployee(new Employee(
            "Williams", "Michael", "michael.williams@techcorp.com", "TechCorp", Role.MANAGER, 12000
        ));
        employeeService.addEmployee(new Employee(
            "Brown", "Sarah", "sarah.brown@techcorp.com", "TechCorp", Role.ENGINEER, 9000
        ));
        employeeService.addEmployee(new Employee(
            "Davis", "James", "james.davis@techcorp.com", "TechCorp", Role.ENGINEER, 8500
        ));
        employeeService.addEmployee(new Employee(
            "Miller", "Olivia", "olivia.miller@techcorp.com", "TechCorp", Role.INTERN, 3000
        ));
        
        employeeService.addEmployee(new Employee(
            "Wilson", "Robert", "robert.wilson@innovate.com", "Innovate Inc", Role.CEO, 28000
        ));
        employeeService.addEmployee(new Employee(
            "Moore", "Lisa", "lisa.moore@innovate.com", "Innovate Inc", Role.MANAGER, 13000
        ));
        employeeService.addEmployee(new Employee(
            "Taylor", "David", "david.taylor@innovate.com", "Innovate Inc", Role.ENGINEER, 8000
        ));
        employeeService.addEmployee(new Employee(
            "Anderson", "Jennifer", "jennifer.anderson@innovate.com", "Innovate Inc", Role.INTERN, 3500
        ));
    }
    
    private static void displayMenu() {
        System.out.println("\n==============================================");
        System.out.println("                MAIN MENU");
        System.out.println("==============================================");
        System.out.println("1.  Add New Employee");
        System.out.println("2.  Remove Employee");
        System.out.println("3.  List All Employees");
        System.out.println("4.  List Employees by Company");
        System.out.println("5.  List Employees by Role");
        System.out.println("6.  List Employees Alphabetically");
        System.out.println("7.  Show Employee Count by Role");
        System.out.println("8.  Show Employee with Highest Salary");
        System.out.println("9.  Show Average Salary");
        System.out.println("10. Show Complete Statistics");
        System.out.println("0.  Exit");
        System.out.println("==============================================");
        System.out.print("Enter your choice: ");
    }
    
    private static int getMenuChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return choice;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void addNewEmployee() {
        System.out.println("=== Add New Employee ===\n");
        
        try {
            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine().trim();
            
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine().trim();
            
            System.out.print("Enter email address: ");
            String email = scanner.nextLine().trim();
            
            System.out.print("Enter company name: ");
            String company = scanner.nextLine().trim();
            
            System.out.println("\nAvailable roles:");
            System.out.println("1. CEO (Base salary: 25000)");
            System.out.println("2. VP (Base salary: 18000)");
            System.out.println("3. MANAGER (Base salary: 12000)");
            System.out.println("4. ENGINEER (Base salary: 8000)");
            System.out.println("5. INTERN (Base salary: 3000)");
            System.out.print("Select role (1-5): ");
            int roleChoice = Integer.parseInt(scanner.nextLine().trim());
            
            Role role;
            switch (roleChoice) {
                case 1: role = Role.CEO; break;
                case 2: role = Role.VP; break;
                case 3: role = Role.MANAGER; break;
                case 4: role = Role.ENGINEER; break;
                case 5: role = Role.INTERN; break;
                default:
                    System.out.println("Invalid role selection. Using ENGINEER as default.");
                    role = Role.ENGINEER;
            }
            
            System.out.print("Enter salary (or press Enter for base salary): ");
            String salaryInput = scanner.nextLine().trim();

            int salary = salaryInput.isEmpty() 
                ? role.getBaseSalary() 
                : Integer.parseInt(salaryInput);
            
            Employee employee = new Employee(
                lastName, firstName, email, company, role, salary
            );

            employeeService.addEmployee(employee);
            
            System.out.println("\nEmployee added successfully!");
            System.out.println("Added: " + employee);
            
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError adding employee: " + e.getMessage());
        }
    }
    
    private static void removeEmployee() {
        System.out.println("=== Remove Employee ===\n");
        
        List<Employee> employees = employeeService.getEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees to remove.");
            return;
        }
        
        System.out.println("Current employees:");
        for (int i = 0; i < employees.size(); i++) {
            System.out.println((i + 1) + ". " + employees.get(i));
        }
        
        System.out.print("\nEnter employee number to remove (or 0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 0) {
                System.out.println("Operation cancelled.");
                return;
            }
            
            if (choice > 0 && choice <= employees.size()) {
                Employee employee = employees.get(choice - 1);
                employeeService.removeEmployee(employee);
                System.out.println("\nEmployee removed: " + employee.getFullName());
            } else {
                System.out.println("Invalid employee number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private static void listAllEmployees() {
        System.out.println("=== All Employees ===\n");
        
        List<Employee> employees = employeeService.getEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        
        System.out.println("Total employees: " + employees.size());
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.printf("%-20s %-15s %-30s %-15s %s%n", 
            "Name", "Role", "Email", "Company", "Salary");
        System.out.println("─────────────────────────────────────────────────────────────────");
        
        for (Employee emp : employees) {
            System.out.printf("%-20s %-15s %-30s %-15s $%,d%n",
                emp.getFullName(),
                emp.getRole(),
                emp.getEmailAddress(),
                emp.getCompanyName(),
                emp.getSalary()
            );
        }
    }
    
    private static void listEmployeesByCompany() {
        System.out.println("=== Employees by Company ===\n");
        
        System.out.print("Enter company name: ");
        String companyName = scanner.nextLine().trim();
        
        List<Employee> employees = employeeService.getEmployeesByCompanyName(companyName);
        
        if (employees.isEmpty()) {
            System.out.println("\nNo employees found for company: " + companyName);
            return;
        }
        
        System.out.println("\nEmployees at " + companyName + ": " + employees.size());
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.printf("%-20s %-15s %-30s %s%n", "Name", "Role", "Email", "Salary");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        for (Employee emp : employees) {
            System.out.printf("%-20s %-15s %-30s $%,d%n",
                emp.getFullName(),
                emp.getRole(),
                emp.getEmailAddress(),
                emp.getSalary()
            );
        }
    }
    
    private static void listEmployeesByRole() {
        System.out.println("=== Employees Grouped by Role ===\n");
        
        Map<Role, List<Employee>> employeesByRole = employeeService.getEmployeesByRole();
        
        if (employeesByRole.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        
        for (Role role : Role.values()) {
            List<Employee> employees = employeesByRole.get(role);
            if (employees != null && !employees.isEmpty()) {
                System.out.println("\n" + role + " (" + employees.size() + "):");
                System.out.println("─────────────────────────────────────────────────────────────");
                for (Employee emp : employees) {
                    System.out.printf("  • %-25s %-30s $%,d%n",
                        emp.getFullName(),
                        emp.getEmailAddress(),
                        emp.getSalary()
                    );
                }
            }
        }
    }
    
    private static void listEmployeesAlphabetically() {
        System.out.println("=== Employees (Alphabetically by Last Name) ===\n");
        
        List<Employee> employees = employeeService.getEmployeesAlphabetically();
        
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        
        System.out.println("Total employees: " + employees.size());
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.printf("%-20s %-15s %-30s %-15s %s%n", 
            "Name", "Role", "Email", "Company", "Salary");
        System.out.println("─────────────────────────────────────────────────────────────────");
        
        for (Employee emp : employees) {
            System.out.printf("%-20s %-15s %-30s %-15s $%,d%n",
                emp.getFullName(),
                emp.getRole(),
                emp.getEmailAddress(),
                emp.getCompanyName(),
                emp.getSalary()
            );
        }
    }
    
    private static void showEmployeeCountByRole() {
        System.out.println("=== Employee Count by Role ===\n");
        
        Map<Role, Long> countByRole = employeeService.getEmployeeCountByRole();
        
        if (countByRole.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        
        System.out.println("─────────────────────────────");
        System.out.printf("%-15s %s%n", "Role", "Count");
        System.out.println("─────────────────────────────");
        
        for (Role role : Role.values()) {
            Long count = countByRole.getOrDefault(role, 0L);
            System.out.printf("%-15s %d%n", role, count);
        }
    }
    
    private static void showEmployeeWithHighestSalary() {
        System.out.println("=== Employee with Highest Salary ===\n");
        
        Optional<Employee> highestPaid = employeeService.getEmployeeWithHighestSalary();
        
        if (highestPaid.isPresent()) {
            Employee emp = highestPaid.get();
            System.out.println("Highest paid employee:");
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("Name:     " + emp.getFullName());
            System.out.println("Role:     " + emp.getRole());
            System.out.println("Company:  " + emp.getCompanyName());
            System.out.println("Email:    " + emp.getEmailAddress());
            System.out.println("Salary:   $" + String.format("%,d", emp.getSalary()));
            System.out.println("─────────────────────────────────────────────────");
        } else {
            System.out.println("No employees found.");
        }
    }
    
    private static void showAverageSalary() {
        System.out.println("=== Average Salary ===\n");
        
        Double avgSalary = employeeService.getAverageSalary();
        int employeeCount = employeeService.getEmployees().size();
        
        if (employeeCount > 0) {
            System.out.println("─────────────────────────────────────");
            System.out.println("Number of employees: " + employeeCount);
            System.out.println("Average salary:      $" + String.format("%,.2f", avgSalary));
            System.out.println("─────────────────────────────────────");
        } else {
            System.out.println("No employees found.");
        }
    }
    
    private static void showStatistics() {
        System.out.println("=== Complete Employee Statistics ===\n");
        
        List<Employee> employees = employeeService.getEmployees();
        
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        
        System.out.println("┌─────────────────────────────────────────────────┐");
        System.out.println("│              GENERAL STATISTICS                 │");
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.println("Total employees:      " + employees.size());
        System.out.println("Average salary:       $" + String.format("%,.2f", employeeService.getAverageSalary()));
        
        Optional<Employee> highestPaid = employeeService.getEmployeeWithHighestSalary();
        if (highestPaid.isPresent()) {
            System.out.println("Highest salary:       $" + String.format("%,d", highestPaid.get().getSalary()) + 
                " (" + highestPaid.get().getFullName() + ")");
        }
        
        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.println("│            EMPLOYEES BY ROLE                    │");
        System.out.println("└─────────────────────────────────────────────────┘");
        Map<Role, Long> countByRole = employeeService.getEmployeeCountByRole();
        for (Role role : Role.values()) {
            Long count = countByRole.getOrDefault(role, 0L);
            if (count > 0) {
                double percentage = (count * 100.0) / employees.size();
                System.out.printf("%-15s : %2d  (%.1f%%)%n", role, count, percentage);
            }
        }

        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.println("│            EMPLOYEES BY COMPANY                 │");
        System.out.println("└─────────────────────────────────────────────────┘");
        Map<String, Long> employeesByCompany = employees.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Employee::getCompanyName, 
                java.util.stream.Collectors.counting()
            ));
        
        employeesByCompany.forEach((company, count) -> {
            double percentage = (count * 100.0) / employees.size();
            System.out.printf("%-20s : %2d  (%.1f%%)%n", company, count, percentage);
        });
        
        System.out.println("\n═════════════════════════════════════════════════\n");
    }
}
