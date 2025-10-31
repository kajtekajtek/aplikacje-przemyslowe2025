package com.techcorp;

import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.techcorp.exception.ApiException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:employees-beans.xml")
public class EmployeeManagementApplication implements CommandLineRunner {
    private final EmployeeService employeeService;
    private final ImportService   importService;
    private final ApiService      apiService;
    private final Scanner         scanner;
    private final List<Employee>  xmlEmployees;

    public EmployeeManagementApplication(
        EmployeeService employeeService,
        ImportService importService,
        ApiService apiService,
        Scanner scanner,
        List<Employee> xmlEmployees
    ) {
        this.employeeService = employeeService;
        this.importService = importService;
        this.apiService = apiService;
        this.scanner = scanner;
        this.xmlEmployees = xmlEmployees;
    }

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }

    @Override
    public void run(String... args) {
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
                case 11:
                    importFromCSV();
                    break;
                case 12:
                    fetchFromAPI();
                    break;
                case 13:
                    showSalaryValidationIssues();
                    break;
                case 14:
                    showCompanyStatistics();
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
    
    private void initializeSampleData() {
        System.out.println("Loading " + xmlEmployees.size() + " employees from XML configuration...");
        for (Employee employee : xmlEmployees) {
            employeeService.addEmployee(employee);
        }
    }
    
    private void displayMenu() {
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
        System.out.println("11. Import Employees from CSV");
        System.out.println("12. Fetch Employees from API");
        System.out.println("13. Validate Salary Consistency");
        System.out.println("14. Show Company Statistics");
        System.out.println("0.  Exit");
        System.out.println("==============================================");
        System.out.print("Enter your choice: ");
    }
    
    private int getMenuChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return choice;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void addNewEmployee() {
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
    
    private void removeEmployee() {
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
    
    private void listAllEmployees() {
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
    
    private void listEmployeesByCompany() {
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
    
    private void listEmployeesByRole() {
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
    
    private void listEmployeesAlphabetically() {
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
    
    private void showEmployeeCountByRole() {
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
    
    private void showEmployeeWithHighestSalary() {
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
    
    private void showAverageSalary() {
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
    
    private void showStatistics() {
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
    
    private void importFromCSV() {
        System.out.println("=== Import Employees from CSV ===\n");
        
        System.out.print("Enter CSV file path (or press Enter for 'employees.csv'): ");
        String filePath = scanner.nextLine().trim();
        
        if (filePath.isEmpty()) {
            filePath = "employees.csv";
        }
        
        try {
            System.out.println("\nImporting from: " + filePath);
            ImportSummary summary = importService.importFromCsv(filePath);
            
            System.out.println("\n┌─────────────────────────────────────────────────┐");
            System.out.println("│              IMPORT SUMMARY                     │");
            System.out.println("└─────────────────────────────────────────────────┘");
            System.out.println("Successfully imported: " + summary.getSuccessCount() + " employees");
            
            Map<Integer, Exception> errors = summary.getErrors();
            if (!errors.isEmpty()) {
                System.out.println("\nErrors encountered: " + errors.size());
                System.out.println("─────────────────────────────────────────────────");
                errors.forEach((lineNumber, error) -> {
                    System.out.println("Line " + lineNumber + ": " + error.getMessage());
                });
            } else {
                System.out.println("\nNo errors encountered during import.");
            }
            
            System.out.println("═════════════════════════════════════════════════");
            
        } catch (Exception e) {
            System.out.println("\nError importing from CSV: " + e.getMessage());
        }
    }
    
    private void fetchFromAPI() {
        System.out.println("=== Fetch Employees from API ===\n");
        
        System.out.print("Enter API URL (or press Enter for default): ");
        String apiUrl = scanner.nextLine().trim();
        
        if (apiUrl.isEmpty()) {
            apiUrl = "https://jsonplaceholder.typicode.com/users";
        }
        
        try {
            System.out.println("\nFetching from: " + apiUrl);
            System.out.println("Please wait...\n");
            
            List<Employee> fetchedEmployees = apiService.fetchEmployeesFromApi(apiUrl);
            
            System.out.println("┌─────────────────────────────────────────────────┐");
            System.out.println("│              API FETCH SUMMARY                  │");
            System.out.println("└─────────────────────────────────────────────────┘");
            System.out.println("Fetched " + fetchedEmployees.size() + " employees from API");
            
            if (!fetchedEmployees.isEmpty()) {
                System.out.println("\nDo you want to add these employees? (y/n): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                
                if (confirm.equals("y") || confirm.equals("yes")) {
                    int addedCount = 0;
                    int skippedCount = 0;
                    
                    for (Employee emp : fetchedEmployees) {
                        try {
                            employeeService.addEmployee(emp);
                            addedCount++;
                        } catch (IllegalArgumentException e) {
                            skippedCount++;
                        }
                    }
                    
                    System.out.println("\n✓ Added: " + addedCount + " employees");
                    if (skippedCount > 0) {
                        System.out.println("✗ Skipped: " + skippedCount + " (duplicates)");
                    }
                } else {
                    System.out.println("\nImport cancelled.");
                }
            }
            
            System.out.println("═════════════════════════════════════════════════");
            
        } catch (ApiException e) {
            System.out.println("\nAPI Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nUnexpected error: " + e.getMessage());
        }
    }
    
    private void showSalaryValidationIssues() {
        System.out.println("=== Salary Consistency Validation ===\n");
        
        List<Employee> inconsistentEmployees = employeeService.validateSalaryConsistency();
        
        if (inconsistentEmployees.isEmpty()) {
            System.out.println("✓ All employee salaries are consistent with their role's base salary.");
            System.out.println("  No issues found.\n");
        } else {
            System.out.println("⚠ Found " + inconsistentEmployees.size() + " employee(s) with salary below base:");
            System.out.println("─────────────────────────────────────────────────────────────────────────");
            System.out.printf("%-20s %-15s %-12s %-12s %-12s%n", 
                "Name", "Role", "Current", "Base", "Difference");
            System.out.println("─────────────────────────────────────────────────────────────────────────");
            
            for (Employee emp : inconsistentEmployees) {
                int difference = emp.getRole().getBaseSalary() - emp.getSalary();
                System.out.printf("%-20s %-15s $%,10d $%,10d $%,10d%n",
                    emp.getFullName(),
                    emp.getRole(),
                    emp.getSalary(),
                    emp.getRole().getBaseSalary(),
                    difference
                );
            }
            
            System.out.println("\n⚠ These employees are being paid below the minimum for their role.");
        }
    }
    
    private void showCompanyStatistics() {
        System.out.println("=== Company Statistics ===\n");
        
        Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
        
        if (stats.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        
        System.out.println("Statistics for " + stats.size() + " compan" + (stats.size() == 1 ? "y" : "ies") + ":\n");
        
        stats.forEach((companyName, companyStats) -> {
            System.out.println("┌─────────────────────────────────────────────────────────────────┐");
            System.out.println("│  " + String.format("%-60s", companyName) + "  │");
            System.out.println("└─────────────────────────────────────────────────────────────────┘");
            System.out.println("  Total Employees:       " + companyStats.getEmployeesCount());
            System.out.println("  Average Salary:        $" + String.format("%,.2f", companyStats.getAverageSalary()));
            System.out.println("  Highest Paid Employee: " + companyStats.getHighestPaidEmployeeName());
            System.out.println();
        });
        
        System.out.println("═════════════════════════════════════════════════════════════════");
    }
}
