package com.techcorp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.techcorp.CompanyStatistics;
import com.techcorp.Employee;
import com.techcorp.Role;
import com.techcorp.exception.DuplicateEmailException;
import com.techcorp.service.EmployeeService;

public class EmployeeServiceTest
{
    private EmployeeService employeeService;
    
    private final String LAST_NAME_1    = "Baggins";
    private final String FIRST_NAME_1   = "Frodo";
    private final String EMAIL_1        = "frodo.baggins@techcorp.com";
    private final String COMPANY_NAME_1 = "TechCorp";
    private final Role   ROLE_1         = Role.ENGINEER;
    private final int    SALARY_1       = 8500;
    
    private final String LAST_NAME_2    = "Gamgee";
    private final String FIRST_NAME_2   = "Sam";
    private final String EMAIL_2        = "sam.gamgee@techcorp.com";
    private final String COMPANY_NAME_2 = "TechCorp";
    private final Role   ROLE_2         = Role.INTERN;
    private final int    SALARY_2       = 3500;
    
    private final String LAST_NAME_3    = "Eustace";
    private final String FIRST_NAME_3   = "Scrubb";
    private final String EMAIL_3        = "scrubb.eustace@innovate.com";
    private final String COMPANY_NAME_3 = "Innovate";
    private final Role   ROLE_3         = Role.MANAGER;
    private final int    SALARY_3       = 13000;
    
    private final String LAST_NAME_4    = "Took";
    private final String FIRST_NAME_4   = "Pippin";
    private final String EMAIL_4        = "pippin.took@techcorp.com";
    private final String COMPANY_NAME_4 = "TechCorp";
    private final Role   ROLE_4         = Role.CEO;
    private final int    SALARY_4       = 30000;

    private final String NULL_EMPLOYEE_EXCEPTION_MESSAGE   = "Employee cannot be null.";
    private final String DUPLICATE_EMAIL_EXCEPTION_MESSAGE = "Employee with email %s already exists.";

    @BeforeEach
    public void setUp()
    {
        employeeService = new EmployeeService();
    }

    @Test
    @DisplayName("Should create an empty employee list when service is constructed")
    public void testConstructor()
    {
        assertNotNull(employeeService.getEmployees());
        assertEquals(0, employeeService.getEmployees().size());
    }
    
    @Nested
    @DisplayName("Add Employee Tests")
    class AddEmployeeTest {

        @Test
        @DisplayName("Should return 1 when employee is added successfully")
        public void shouldReturnOneWhenEmployeeIsAdded()
        {
            Employee employee = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
            );
            
            int result = employeeService.addEmployee(employee);
            
            assertEquals(1, result);
            assertEquals(1, employeeService.getEmployees().size());
            assertTrue(employeeService.getEmployees().contains(employee));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when employee is null")
        public void shouldThrowIllegalArgumentExceptionWhenEmployeeIsNull()
        {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.addEmployee(null)
            );
            
            assertTrue(exception.getMessage().contains(
                NULL_EMPLOYEE_EXCEPTION_MESSAGE
            ));
            assertEquals(0, employeeService.getEmployees().size());
        }

        @Test
        @DisplayName("Should contain two employees when two are added")
        public void shouldContainTwosWhenTwoAreAdded()
        {
            Employee employee1 = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
            );
            Employee employee2 = new Employee(
                LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, SALARY_2
            );
            
            employeeService.addEmployee(employee1);
            employeeService.addEmployee(employee2);
            
            assertEquals(2, employeeService.getEmployees().size());
            assertTrue(employeeService.getEmployees().contains(employee1));
            assertTrue(employeeService.getEmployees().contains(employee2));
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when duplicate email is added")
        public void shouldThrowDuplicateEmailExceptionWhenDuplicateEmailIsAdded()
        {
            Employee employee1 = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
            );
            Employee employee2 = new Employee(
                LAST_NAME_2, FIRST_NAME_2, EMAIL_1, COMPANY_NAME_2, ROLE_2, SALARY_2
            );
            
            employeeService.addEmployee(employee1);
            
            DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> employeeService.addEmployee(employee2)
            );
            
            assertTrue(exception.getMessage().contains(
                DUPLICATE_EMAIL_EXCEPTION_MESSAGE.formatted(EMAIL_1)
            ));
            assertEquals(1, employeeService.getEmployees().size());
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when duplicate email is added (case insensitive)")
        public void shouldThrowDuplicateEmailExceptionWhenDuplicateEmailIsAddedCaseInsensitive()
        {
            Employee employee1 = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
            );
            Employee employee2 = new Employee(
                LAST_NAME_2, FIRST_NAME_2, EMAIL_1.toUpperCase(), COMPANY_NAME_2, ROLE_2, SALARY_2
            );
            
            employeeService.addEmployee(employee1);
            
            DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> employeeService.addEmployee(employee2)
            );
            
            assertTrue(exception.getMessage().contains(
                DUPLICATE_EMAIL_EXCEPTION_MESSAGE.formatted(EMAIL_1)
            ));
        }

    }

    @Nested
    @DisplayName("Remove Employee Tests")
    class RemoveEmployeeTest {

        @Test
        @DisplayName("Should remove employee successfully when employee exists")
        public void shouldRemoveEmployee()
        {
            Employee employee = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
            );
            
            employeeService.addEmployee(employee);
            assertEquals(1, employeeService.getEmployees().size());
            
            int result = employeeService.removeEmployee(employee);
            
            assertEquals(1, result);
            assertEquals(0, employeeService.getEmployees().size());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when employee to remove is null")
        public void shouldThrowIllegalArgumentExceptionWhenEmployeeIsNull()
        {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.removeEmployee(null)
            );
            
            assertTrue(exception.getMessage().contains(
                NULL_EMPLOYEE_EXCEPTION_MESSAGE
            ));
        }

        @Test
        @DisplayName("Should return 0 when employee to remove is not found")
        public void shouldReturnZeroWhenEmployeeIsNotFound()
        {
            Employee employee1 = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
            );
            Employee employee2 = new Employee(
                LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, SALARY_2
            );
            
            employeeService.addEmployee(employee1);
            
            int result = employeeService.removeEmployee(employee2);
            
            assertEquals(0, result);
            assertEquals(1, employeeService.getEmployees().size());
            assertTrue(employeeService.getEmployees().contains(employee1));
        }

        @Test
        @DisplayName("Should remove only the specified employee from multiple employees")
        public void shouldRemoveOnlyTheSpecifiedEmployeeFromMultipleEmployees()
        {
            Employee employee1 = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
            );
            Employee employee2 = new Employee(
                LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, SALARY_2
            );
            
            employeeService.addEmployee(employee1);
            employeeService.addEmployee(employee2);
            
            employeeService.removeEmployee(employee1);
            
            assertEquals(1, employeeService.getEmployees().size());
            assertFalse(employeeService.getEmployees().contains(employee1));
            assertTrue(employeeService.getEmployees().contains(employee2));
        }

    }

    @Nested
    @DisplayName("Getters Tests")
    class GetTest {

        @Test
        @DisplayName("Should return all employees when getting all employees")
        public void shouldReturnAllWhenGettingAllEmployees()
        {
            Employee employee1 = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
            );
            Employee employee2 = new Employee(
                LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, SALARY_2
            );
            
            employeeService.addEmployee(employee1);
            employeeService.addEmployee(employee2);
            
            List<Employee> employees = employeeService.getEmployees();
            
            assertEquals(2, employees.size());
            assertTrue(employees.contains(employee1));
            assertTrue(employees.contains(employee2));
        }

        @Nested
        @DisplayName("Get Employees by Company Name Tests")
        class EmployeesByCompanyNameTest {

            @Test
            @DisplayName("Should return employees filtered by company name")
            public void shouldReturnEmployeesByCompanyName()
            {
                Employee employee1 = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
                );
                Employee employee2 = new Employee(
                    LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_1, ROLE_2, SALARY_2
                );
                Employee employee3 = new Employee(
                    LAST_NAME_3, FIRST_NAME_3, EMAIL_3, COMPANY_NAME_3, ROLE_3, SALARY_3
                );
                
                employeeService.addEmployee(employee1);
                employeeService.addEmployee(employee2);
                employeeService.addEmployee(employee3);
                
                List<Employee> techCorpEmployees = employeeService
                    .getEmployeesByCompanyName(COMPANY_NAME_1);
                
                assertEquals(2, techCorpEmployees.size());
                assertTrue(techCorpEmployees.contains(employee1));
                assertTrue(techCorpEmployees.contains(employee2));
                assertFalse(techCorpEmployees.contains(employee3));
            }

            @Test
            @DisplayName("Should return employees by company name (case insensitive)")
            public void shouldReturnEmployeesByCompanyNameCaseInsensitive()
            {
                Employee employee = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
                );
                
                employeeService.addEmployee(employee);
                
                List<Employee> employees = employeeService
                    .getEmployeesByCompanyName(COMPANY_NAME_1.toLowerCase());
                
                assertEquals(1, employees.size());
                assertTrue(employees.contains(employee));
            }

            @Test
            @DisplayName("Should return empty list when company name is not found")
            public void shouldReturnEmptyListWhenCompanyNameIsNotFound()
            {
                Employee employee = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
                );
                
                employeeService.addEmployee(employee);
                
                List<Employee> employees = employeeService
                    .getEmployeesByCompanyName(COMPANY_NAME_3);
                
                assertEquals(0, employees.size());
            }
        }

        @Nested
        @DisplayName("Get Employees by Role Tests")
        class EmployeesByRoleTest {

            @Test
            @DisplayName("Should return employees grouped by role")
            public void shouldReturnEmployeesGroupedByRole()
            {
                Employee employee1 = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, Role.ENGINEER, SALARY_1
                );
                Employee employee2 = new Employee(
                    LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, Role.INTERN, SALARY_2
                );
                Employee employee3 = new Employee(
                    LAST_NAME_3, FIRST_NAME_3, EMAIL_3, COMPANY_NAME_3, Role.ENGINEER, SALARY_3
                );
                
                employeeService.addEmployee(employee1);
                employeeService.addEmployee(employee2);
                employeeService.addEmployee(employee3);
                
                Map<Role, List<Employee>> employeesByRole = employeeService.getEmployeesByRole();
                
                assertEquals(2, employeesByRole.size());
                assertEquals(2, employeesByRole.get(Role.ENGINEER).size());
                assertEquals(1, employeesByRole.get(Role.INTERN).size());
                assertTrue(employeesByRole.get(Role.ENGINEER).contains(employee1));
                assertTrue(employeesByRole.get(Role.ENGINEER).contains(employee3));
                assertTrue(employeesByRole.get(Role.INTERN).contains(employee2));
            }

            @Test
            @DisplayName("Should return empty map when no employees exist")
            public void shouldReturnEmptyMapWhenNoEmployeesExist()
            {
                Map<Role, List<Employee>> employeesByRole = employeeService.getEmployeesByRole();
                
                assertEquals(0, employeesByRole.size());
            } 

        }

        @Nested
        @DisplayName("Get Employees Alphabetically Tests")
        class EmployeesAlphabeticallyTest {

            @Test
            @DisplayName("Should return employees sorted alphabetically by last name")
            public void shouldReturnEmployeesAlphabetically()
            {
                Employee employee1 = new Employee(
                    "Smith", "John", "john.smith@test.com", "TestCorp", Role.ENGINEER, 9000
                );
                Employee employee2 = new Employee(
                    "Adams", "Alice", "alice.adams@test.com", "TestCorp", Role.MANAGER, 12000
                );
                Employee employee3 = new Employee(
                    "Brown", "Bob", "bob.brown@test.com", "TestCorp", Role.INTERN, 3500
                );
                
                employeeService.addEmployee(employee1);
                employeeService.addEmployee(employee2);
                employeeService.addEmployee(employee3);
                
                List<Employee> sortedEmployees = employeeService.getEmployeesAlphabetically();
                
                assertEquals(3, sortedEmployees.size());
                assertEquals("Adams", sortedEmployees.get(0).getLastName());
                assertEquals("Brown", sortedEmployees.get(1).getLastName());
                assertEquals("Smith", sortedEmployees.get(2).getLastName());
            }

            @Test
            @DisplayName("Should return empty list when no employees are added")
            public void shouldReturnEmptyListWhenNoEmployeesAreAdded()
            {
                List<Employee> sortedEmployees = employeeService.getEmployeesAlphabetically();
                
                assertEquals(0, sortedEmployees.size());
            }
            
        }

        @Nested
        @DisplayName("Get Employee Count by Role Tests")
        class EmployeeCountByRoleTest {

            @Test
            @DisplayName("Should return employee count grouped by role")
            public void shouldReturnEmployeeCountByRole()
            {
                Employee employee1 = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, Role.ENGINEER, SALARY_1
                );
                Employee employee2 = new Employee(
                    LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, Role.INTERN, SALARY_2
                );
                Employee employee3 = new Employee(
                    LAST_NAME_3, FIRST_NAME_3, EMAIL_3, COMPANY_NAME_3, Role.ENGINEER, SALARY_3
                );
                Employee employee4 = new Employee(
                    LAST_NAME_4, FIRST_NAME_4, EMAIL_4, COMPANY_NAME_4, Role.ENGINEER, SALARY_4
                );
                
                employeeService.addEmployee(employee1);
                employeeService.addEmployee(employee2);
                employeeService.addEmployee(employee3);
                employeeService.addEmployee(employee4);
                
                Map<Role, Long> countByRole = employeeService.getEmployeeCountByRole();
                
                assertEquals(2, countByRole.size());
                assertEquals(3L, countByRole.get(Role.ENGINEER));
                assertEquals(1L, countByRole.get(Role.INTERN));
            }

            @Test
            @DisplayName("Should return empty map when no employees are added")
            public void shouldReturnEmptyMapWhenNoEmployeesAreAdded()
            {
                Map<Role, Long> countByRole = employeeService.getEmployeeCountByRole();
                
                assertEquals(0, countByRole.size());
            }     

        }

        @Nested
        @DisplayName("Get Employee with Highest Salary Tests")
        class EmployeeWithHighestSalaryTest {

            @Test
            @DisplayName("Should return employee with the highest salary")
            public void shouldReturnEmployeeWithHighestSalary()
            {
                Employee employee1 = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, 8500
                );
                Employee employee2 = new Employee(
                    LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, 3500
                );
                Employee employee3 = new Employee(
                    LAST_NAME_3, FIRST_NAME_3, EMAIL_3, COMPANY_NAME_3, ROLE_3, 15000
                );
                
                employeeService.addEmployee(employee1);
                employeeService.addEmployee(employee2);
                employeeService.addEmployee(employee3);
                
                Optional<Employee> highestPaid = employeeService.getEmployeeWithHighestSalary();
                
                assertTrue(highestPaid.isPresent());
                assertEquals(employee3, highestPaid.get());
                assertEquals(15000, highestPaid.get().getSalary());
            }

            @Test
            @DisplayName("Should return empty Optional when no employees are added")
            public void shouldReturnEmptyOptionalWhenNoEmployeesAreAdded()
            {
                Optional<Employee> highestPaid = employeeService.getEmployeeWithHighestSalary();
                
                assertFalse(highestPaid.isPresent());
            }

            @Test
            @DisplayName("Should return the only employee when single employee is added")
            public void shouldReturnEmployeeWithHighestSalaryWhenSingleEmployeeIsAdded()
            {
                Employee employee = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
                );
                
                employeeService.addEmployee(employee);
                
                Optional<Employee> highestPaid = employeeService.getEmployeeWithHighestSalary();
                
                assertTrue(highestPaid.isPresent());
                assertEquals(employee, highestPaid.get());
            }

        }

        @Nested
        @DisplayName("Get Average Salary Tests")
        class AverageSalaryTest {

            @Test
            @DisplayName("Should return average salary of all employees")
            public void shouldReturnAverageSalary()
            {
                Employee employee1 = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, 8000
                );
                Employee employee2 = new Employee(
                    LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, 4000
                );
                Employee employee3 = new Employee(
                    LAST_NAME_3, FIRST_NAME_3, EMAIL_3, COMPANY_NAME_3, ROLE_3, 12000
                );
                
                employeeService.addEmployee(employee1);
                employeeService.addEmployee(employee2);
                employeeService.addEmployee(employee3);
                
                Double averageSalary = employeeService.getAverageSalary();
                
                assertEquals(8000.0, averageSalary, 0.01);
            }

            @Test
            @DisplayName("Should return employee's salary when single employee is added")
            public void shouldReturnAverageSalaryWhenSingleEmployeeIsAdded()
            {
                Employee employee = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
                );
                
                employeeService.addEmployee(employee);
                
                Double averageSalary = employeeService.getAverageSalary();
                
                assertEquals(SALARY_1, averageSalary, 0.01);
            }

            @Test
            @DisplayName("Should return 0.0 when no employees are added")
            public void shouldReturnZeroWhenNoEmployeesAreAdded()
            {
                Double averageSalary = employeeService.getAverageSalary();
                
                assertEquals(0.0, averageSalary, 0.01);
            }

        }

        @Nested
        @DisplayName("Get Company Statistics Tests")
        class CompanyStatisticsTest {

            @Test
            @DisplayName("Should return valid statistics for a single company")
            public void shouldReturnValidCompanyStatisticsWhenSingleCompanyIsAdded()
            {
                Employee employee1 = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, Role.ENGINEER, 8000
                );
                Employee employee2 = new Employee(
                    LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_1, Role.MANAGER, 12000
                );
                Employee employee3 = new Employee(
                    LAST_NAME_3, FIRST_NAME_3, EMAIL_3, COMPANY_NAME_1, Role.INTERN, 4000
                );
                
                employeeService.addEmployee(employee1);
                employeeService.addEmployee(employee2);
                employeeService.addEmployee(employee3);
                
                Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
                
                assertEquals(1, stats.size());
                assertTrue(stats.containsKey(COMPANY_NAME_1));
                
                CompanyStatistics companyStats = stats.get(COMPANY_NAME_1);
                assertEquals(3, companyStats.getEmployeesCount());
                assertEquals(8000.0, companyStats.getAverageSalary(), 0.01);
                assertEquals("Sam Gamgee", companyStats.getTopEarnerName());
            }

            @Test
            @DisplayName("Should return valid statistics for multiple companies")
            public void shouldReturnValidCompanyStatisticsWhenMultipleCompaniesAreAdded()
            {
                Employee employee1 = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, "TechCorp", Role.ENGINEER, 8000
                );
                Employee employee2 = new Employee(
                    LAST_NAME_2, FIRST_NAME_2, EMAIL_2, "TechCorp", Role.MANAGER, 12000
                );
                Employee employee3 = new Employee(
                    LAST_NAME_3, FIRST_NAME_3, EMAIL_3, "Innovate", Role.INTERN, 4000
                );
                Employee employee4 = new Employee(
                    LAST_NAME_4, FIRST_NAME_4, EMAIL_4, "Innovate", Role.ENGINEER, 10000
                );
                
                employeeService.addEmployee(employee1);
                employeeService.addEmployee(employee2);
                employeeService.addEmployee(employee3);
                employeeService.addEmployee(employee4);
                
                Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
                
                assertEquals(2, stats.size());
                assertTrue(stats.containsKey("TechCorp"));
                assertTrue(stats.containsKey("Innovate"));
                
                CompanyStatistics companyStats = stats.get("TechCorp");
                assertEquals(2, companyStats.getEmployeesCount());
                assertEquals(10000.0, companyStats.getAverageSalary(), 0.01);
                assertEquals(employee2.getFullName(), companyStats.getTopEarnerName());
                
                companyStats = stats.get(COMPANY_NAME_3);
                assertEquals(2, companyStats.getEmployeesCount());
                assertEquals(7000.0, companyStats.getAverageSalary(), 0.01);
                assertEquals(employee4.getFullName(), companyStats.getTopEarnerName());
            }

            @Test
            @DisplayName("Should return empty map when no employees are added")
            public void shouldReturnEmptyMapWhenNoEmployeesAreAdded()
            {
                Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
                
                assertEquals(0, stats.size());
            }

            @Test
            @DisplayName("Should return valid statistics when single employee is added")
            public void shouldReturnValidCompanyStatisticsWhenSingleEmployeeIsAdded()
            {
                Employee employee = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, Role.ENGINEER, 8500
                );
                
                employeeService.addEmployee(employee);
                
                Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
                
                assertEquals(1, stats.size());
                CompanyStatistics companyStats = stats.get(COMPANY_NAME_1);
                
                assertEquals(1, companyStats.getEmployeesCount());
                assertEquals(8500.0, companyStats.getAverageSalary(), 0.01);
                assertEquals(employee.getFullName(), companyStats.getTopEarnerName());
            }

            @Test
            @DisplayName("Should handle employees with same salary correctly")
            public void shouldReturnValidCompanyStatisticsWhenSameSalary()
            {
                Employee employee1 = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, Role.ENGINEER, 8000
                );
                Employee employee2 = new Employee(
                    LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_1, Role.ENGINEER, 8000
                );
                
                employeeService.addEmployee(employee1);
                employeeService.addEmployee(employee2);
                
                Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
                
                CompanyStatistics companyStats = stats.get(COMPANY_NAME_1);
                assertEquals(2, companyStats.getEmployeesCount());
                assertEquals(8000.0, companyStats.getAverageSalary(), 0.01);
                assertTrue(
                    companyStats.getTopEarnerName().equals(employee1.getFullName()) ||
                    companyStats.getTopEarnerName().equals(employee2.getFullName())
                );
            }

            @Test
            @DisplayName("Should return valid statistics for three different companies")
            public void shouldReturnValidCompanyStatisticsWhenThreeCompaniesAreAdded()
            {
                Employee ceo = new Employee(
                    LAST_NAME_1, FIRST_NAME_1, EMAIL_1, "Microsoft", Role.CEO, 30000
                );
                Employee vp = new Employee(
                    LAST_NAME_2, FIRST_NAME_2, EMAIL_2, "Apple", Role.VP, 20000
                );
                Employee engineer = new Employee(
                    LAST_NAME_3, FIRST_NAME_3, EMAIL_3, "Nvidia", Role.ENGINEER, 9000
                );
                
                employeeService.addEmployee(ceo);
                employeeService.addEmployee(vp);
                employeeService.addEmployee(engineer);
                
                Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
                
                assertEquals(3, stats.size());
                assertTrue(stats.containsKey("Microsoft"));
                assertTrue(stats.containsKey("Apple"));
                assertTrue(stats.containsKey("Nvidia"));
                assertEquals(1, stats.get("Microsoft").getEmployeesCount());
                assertEquals(1, stats.get("Apple").getEmployeesCount());
                assertEquals(1, stats.get("Nvidia").getEmployeesCount());
                assertEquals(30000.0, stats.get("Microsoft").getAverageSalary(), 0.01);
                assertEquals(20000.0, stats.get("Apple").getAverageSalary(), 0.01);
                assertEquals(9000.0, stats.get("Nvidia").getAverageSalary(), 0.01);
            }
        }
    }

    @Nested
    @DisplayName("Validate Salary Consistency Tests")
    class ValidateSalaryConsistencyTest {

        @Test
        @DisplayName("Should return empty list when no inconsistencies are found")
        public void shouldReturnEmptyListWhenNotFound()
        {
            Employee employee1 = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, Role.ENGINEER, 8000
            );
            Employee employee2 = new Employee(
                LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, Role.INTERN, 3000
            );
            
            employeeService.addEmployee(employee1);
            employeeService.addEmployee(employee2);
            
            List<Employee> inconsistentEmployees = employeeService.validateSalaryConsistency();
            
            assertEquals(0, inconsistentEmployees.size());
        }

        @Test
        @DisplayName("Should return list of employees with salary inconsistencies")
        public void shouldReturnListOfInconsistenciesWhenAreFound()
        {
            Employee employee1 = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, Role.ENGINEER, 5000
            );
            Employee employee2 = new Employee(
                LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, Role.INTERN, 3500
            );
            Employee employee3 = new Employee(
                LAST_NAME_3, FIRST_NAME_3, EMAIL_3, COMPANY_NAME_3, Role.MANAGER, 10000
            );
            
            employeeService.addEmployee(employee1);
            employeeService.addEmployee(employee2);
            employeeService.addEmployee(employee3);
            
            List<Employee> inconsistentEmployees = employeeService.validateSalaryConsistency();
            
            assertEquals(2, inconsistentEmployees.size());
            assertTrue(inconsistentEmployees.contains(employee1));
            assertTrue(inconsistentEmployees.contains(employee3));
            assertFalse(inconsistentEmployees.contains(employee2));
        }

        @Test
        @DisplayName("Should return empty list when employee list is empty")
        public void shouldReturnEmptyListWhenEmployeeListIsEmpty()
        {
            List<Employee> inconsistentEmployees = employeeService.validateSalaryConsistency();
            
            assertEquals(0, inconsistentEmployees.size());
        }

        @Test
        @DisplayName("Should return all employees when all have salary inconsistencies")
        public void shouldReturnListOfInconsistenciesWhenAllInconsistent()
        {
            Employee employee1 = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, Role.ENGINEER, 1000
            );
            Employee employee2 = new Employee(
                LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, Role.INTERN, 100
            );
            
            employeeService.addEmployee(employee1);
            employeeService.addEmployee(employee2);
            
            List<Employee> inconsistentEmployees = employeeService.validateSalaryConsistency();
            
            assertEquals(2, inconsistentEmployees.size());
            assertTrue(inconsistentEmployees.contains(employee1));
            assertTrue(inconsistentEmployees.contains(employee2));
        }

        @Test
        @DisplayName("Should correctly identify inconsistencies at salary boundaries")
        public void shouldReturnListOfInconsistenciesWhenBoundaryCases()
        {
            Employee employee1 = new Employee(
                LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, Role.ENGINEER, 7999
            );
            Employee employee2 = new Employee(
                LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, Role.ENGINEER, 8000
            );
            Employee employee3 = new Employee(
                LAST_NAME_3, FIRST_NAME_3, EMAIL_3, COMPANY_NAME_3, Role.ENGINEER, 8001
            );
            
            employeeService.addEmployee(employee1);
            employeeService.addEmployee(employee2);
            employeeService.addEmployee(employee3);
            
            List<Employee> inconsistentEmployees = employeeService.validateSalaryConsistency();
            
            assertEquals(1, inconsistentEmployees.size());
            assertTrue(inconsistentEmployees.contains(employee1));
            assertFalse(inconsistentEmployees.contains(employee2));
            assertFalse(inconsistentEmployees.contains(employee3));
        }
    }



    @Test
    @DisplayName("Should not throw exception when printing employees")
    public void shouldNotThrowWhenPrintingEmployees()
    {
        Employee employee = new Employee(
            LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        
        employeeService.addEmployee(employee);
        
        assertDoesNotThrow(() -> employeeService.printEmployees());
    }

}

