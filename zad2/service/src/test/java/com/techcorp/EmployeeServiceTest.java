package com.techcorp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @BeforeEach
    public void setUp()
    {
        employeeService = new EmployeeService();
    }

    @Test
    public void testConstructor()
    {
        assertNotNull(employeeService.getEmployees());
        assertEquals(0, employeeService.getEmployees().size());
    }

    @Test
    public void testAddEmployee()
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
    public void testAddEmployeeNull()
    {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> employeeService.addEmployee(null)
        );
        
        assertTrue(exception.getMessage().contains("cannot be null"));
        assertEquals(0, employeeService.getEmployees().size());
    }

    @Test
    public void testAddMultipleEmployees()
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
    public void testAddEmployeeWithDuplicateEmail()
    {
        Employee employee1 = new Employee(
            LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        Employee employee2 = new Employee(
            LAST_NAME_2, FIRST_NAME_2, EMAIL_1, COMPANY_NAME_2, ROLE_2, SALARY_2
        );
        
        employeeService.addEmployee(employee1);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> employeeService.addEmployee(employee2)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        assertEquals(1, employeeService.getEmployees().size());
    }

    @Test
    public void testAddEmployeeWithDuplicateEmailCaseInsensitive()
    {
        Employee employee1 = new Employee(
            LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        Employee employee2 = new Employee(
            LAST_NAME_2, FIRST_NAME_2, EMAIL_1.toUpperCase(), COMPANY_NAME_2, ROLE_2, SALARY_2
        );
        
        employeeService.addEmployee(employee1);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> employeeService.addEmployee(employee2)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    public void testRemoveEmployee()
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
    public void testRemoveEmployeeNull()
    {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> employeeService.removeEmployee(null)
        );
        
        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    public void testRemoveEmployeeNotFound()
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
    public void testRemoveEmployeeFromMultiple()
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

    @Test
    public void testGetEmployees()
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

    @Test
    public void testGetEmployeesByCompanyName()
    {
        Employee employee1 = new Employee(
            LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        Employee employee2 = new Employee(
            LAST_NAME_2, FIRST_NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, SALARY_2
        );
        Employee employee3 = new Employee(
            LAST_NAME_3, FIRST_NAME_3, EMAIL_3, COMPANY_NAME_3, ROLE_3, SALARY_3
        );
        
        employeeService.addEmployee(employee1);
        employeeService.addEmployee(employee2);
        employeeService.addEmployee(employee3);
        
        List<Employee> techCorpEmployees = employeeService.getEmployeesByCompanyName("TechCorp");
        
        assertEquals(2, techCorpEmployees.size());
        assertTrue(techCorpEmployees.contains(employee1));
        assertTrue(techCorpEmployees.contains(employee2));
        assertFalse(techCorpEmployees.contains(employee3));
    }

    @Test
    public void testGetEmployeesByCompanyNameCaseInsensitive()
    {
        Employee employee = new Employee(
            LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        
        employeeService.addEmployee(employee);
        
        List<Employee> employees = employeeService.getEmployeesByCompanyName("techcorp");
        
        assertEquals(1, employees.size());
        assertTrue(employees.contains(employee));
    }

    @Test
    public void testGetEmployeesByCompanyNameNoMatches()
    {
        Employee employee = new Employee(
            LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        
        employeeService.addEmployee(employee);
        
        List<Employee> employees = employeeService.getEmployeesByCompanyName("NonExistent");
        
        assertEquals(0, employees.size());
    }

    @Test
    public void testGetEmployeesByRole()
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
    public void testGetEmployeesByRoleEmpty()
    {
        Map<Role, List<Employee>> employeesByRole = employeeService.getEmployeesByRole();
        
        assertEquals(0, employeesByRole.size());
    }

    @Test
    public void testGetEmployeesAlphabetically()
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
    public void testGetEmployeesAlphabeticallyEmpty()
    {
        List<Employee> sortedEmployees = employeeService.getEmployeesAlphabetically();
        
        assertEquals(0, sortedEmployees.size());
    }

    @Test
    public void testGetEmployeeCountByRole()
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
    public void testGetEmployeeCountByRoleEmpty()
    {
        Map<Role, Long> countByRole = employeeService.getEmployeeCountByRole();
        
        assertEquals(0, countByRole.size());
    }

    @Test
    public void testGetEmployeeWithHighestSalary()
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
    public void testGetEmployeeWithHighestSalaryEmpty()
    {
        Optional<Employee> highestPaid = employeeService.getEmployeeWithHighestSalary();
        
        assertFalse(highestPaid.isPresent());
    }

    @Test
    public void testGetEmployeeWithHighestSalarySingleEmployee()
    {
        Employee employee = new Employee(
            LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        
        employeeService.addEmployee(employee);
        
        Optional<Employee> highestPaid = employeeService.getEmployeeWithHighestSalary();
        
        assertTrue(highestPaid.isPresent());
        assertEquals(employee, highestPaid.get());
    }

    @Test
    public void testGetAverageSalary()
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
    public void testGetAverageSalarySingleEmployee()
    {
        Employee employee = new Employee(
            LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        
        employeeService.addEmployee(employee);
        
        Double averageSalary = employeeService.getAverageSalary();
        
        assertEquals(SALARY_1, averageSalary, 0.01);
    }

    @Test
    public void testGetAverageSalaryEmpty()
    {
        Double averageSalary = employeeService.getAverageSalary();
        
        assertEquals(0.0, averageSalary, 0.01);
    }

    @Test
    public void testPrintEmployees()
    {
        Employee employee = new Employee(
            LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        
        employeeService.addEmployee(employee);
        
        assertDoesNotThrow(() -> employeeService.printEmployees());
    }

    @Test
    public void testGetEmployeesReturnsActualList()
    {
        Employee employee = new Employee(
            LAST_NAME_1, FIRST_NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        
        employeeService.addEmployee(employee);
        
        List<Employee> employees1 = employeeService.getEmployees();
        List<Employee> employees2 = employeeService.getEmployees();
        
        assertSame(employees1, employees2);
    }

    @Test
    public void testMultipleRolesGrouping()
    {
        Employee ceo = new Employee(
            "Smith", "John", "john@test.com", "TestCorp", Role.CEO, 30000
        );
        Employee vp = new Employee(
            "Doe", "Jane", "jane@test.com", "TestCorp", Role.VP, 20000
        );
        Employee manager = new Employee(
            "Brown", "Bob", "bob@test.com", "TestCorp", Role.MANAGER, 13000
        );
        Employee engineer = new Employee(
            "White", "Alice", "alice@test.com", "TestCorp", Role.ENGINEER, 9000
        );
        Employee intern = new Employee(
            "Green", "Tom", "tom@test.com", "TestCorp", Role.INTERN, 3500
        );
        
        employeeService.addEmployee(ceo);
        employeeService.addEmployee(vp);
        employeeService.addEmployee(manager);
        employeeService.addEmployee(engineer);
        employeeService.addEmployee(intern);
        
        Map<Role, List<Employee>> byRole = employeeService.getEmployeesByRole();
        
        assertEquals(5, byRole.size());
        assertEquals(1, byRole.get(Role.CEO).size());
        assertEquals(1, byRole.get(Role.VP).size());
        assertEquals(1, byRole.get(Role.MANAGER).size());
        assertEquals(1, byRole.get(Role.ENGINEER).size());
        assertEquals(1, byRole.get(Role.INTERN).size());
    }

    @Test
    public void testValidateSalaryConsistency_NoInconsistencies()
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
    public void testValidateSalaryConsistency_WithInconsistencies()
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
    public void testValidateSalaryConsistency_Empty()
    {
        List<Employee> inconsistentEmployees = employeeService.validateSalaryConsistency();
        
        assertEquals(0, inconsistentEmployees.size());
    }

    @Test
    public void testValidateSalaryConsistency_AllInconsistent()
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
    public void testValidateSalaryConsistency_BoundaryCases()
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

    @Test
    public void testGetCompanyStatistics_SingleCompany()
    {
        Employee employee1 = new Employee(
            LAST_NAME_1, FIRST_NAME_1, EMAIL_1, "TechCorp", Role.ENGINEER, 8000
        );
        Employee employee2 = new Employee(
            LAST_NAME_2, FIRST_NAME_2, EMAIL_2, "TechCorp", Role.MANAGER, 12000
        );
        Employee employee3 = new Employee(
            LAST_NAME_3, FIRST_NAME_3, EMAIL_3, "TechCorp", Role.INTERN, 4000
        );
        
        employeeService.addEmployee(employee1);
        employeeService.addEmployee(employee2);
        employeeService.addEmployee(employee3);
        
        Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
        
        assertEquals(1, stats.size());
        assertTrue(stats.containsKey("TechCorp"));
        
        CompanyStatistics techCorpStats = stats.get("TechCorp");
        assertEquals(3, techCorpStats.getEmployeesCount());
        assertEquals(8000.0, techCorpStats.getAverageSalary(), 0.01);
        assertEquals("Sam Gamgee", techCorpStats.getHighestPaidEmployeeName());
    }

    @Test
    public void testGetCompanyStatistics_MultipleCompanies()
    {
        Employee employee1 = new Employee(
            "Smith", "John", "john@techcorp.com", "TechCorp", Role.ENGINEER, 9000
        );
        Employee employee2 = new Employee(
            "Doe", "Jane", "jane@techcorp.com", "TechCorp", Role.MANAGER, 15000
        );
        Employee employee3 = new Employee(
            "Brown", "Bob", "bob@innovate.com", "Innovate", Role.INTERN, 3500
        );
        Employee employee4 = new Employee(
            "White", "Alice", "alice@innovate.com", "Innovate", Role.ENGINEER, 10000
        );
        
        employeeService.addEmployee(employee1);
        employeeService.addEmployee(employee2);
        employeeService.addEmployee(employee3);
        employeeService.addEmployee(employee4);
        
        Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
        
        assertEquals(2, stats.size());
        assertTrue(stats.containsKey("TechCorp"));
        assertTrue(stats.containsKey("Innovate"));
        
        CompanyStatistics techCorpStats = stats.get("TechCorp");
        assertEquals(2, techCorpStats.getEmployeesCount());
        assertEquals(12000.0, techCorpStats.getAverageSalary(), 0.01);
        assertEquals("Jane Doe", techCorpStats.getHighestPaidEmployeeName());
        
        CompanyStatistics innovateStats = stats.get("Innovate");
        assertEquals(2, innovateStats.getEmployeesCount());
        assertEquals(6750.0, innovateStats.getAverageSalary(), 0.01);
        assertEquals("Alice White", innovateStats.getHighestPaidEmployeeName());
    }

    @Test
    public void testGetCompanyStatistics_Empty()
    {
        Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
        
        assertEquals(0, stats.size());
    }

    @Test
    public void testGetCompanyStatistics_SingleEmployee()
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
        assertEquals("Frodo Baggins", companyStats.getHighestPaidEmployeeName());
    }

    @Test
    public void testGetCompanyStatistics_SameSalary()
    {
        Employee employee1 = new Employee(
            "Smith", "John", "john@techcorp.com", "TechCorp", Role.ENGINEER, 8000
        );
        Employee employee2 = new Employee(
            "Doe", "Jane", "jane@techcorp.com", "TechCorp", Role.ENGINEER, 8000
        );
        
        employeeService.addEmployee(employee1);
        employeeService.addEmployee(employee2);
        
        Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
        
        CompanyStatistics techCorpStats = stats.get("TechCorp");
        assertEquals(2, techCorpStats.getEmployeesCount());
        assertEquals(8000.0, techCorpStats.getAverageSalary(), 0.01);
        assertTrue(
            techCorpStats.getHighestPaidEmployeeName().equals("John Smith") ||
            techCorpStats.getHighestPaidEmployeeName().equals("Jane Doe")
        );
    }

    @Test
    public void testGetCompanyStatistics_ThreeCompanies()
    {
        Employee ceo = new Employee(
            "Gates", "Bill", "bill@microsoft.com", "Microsoft", Role.CEO, 30000
        );
        Employee vp = new Employee(
            "Jobs", "Steve", "steve@apple.com", "Apple", Role.VP, 20000
        );
        Employee engineer = new Employee(
            "Torvalds", "Linus", "linus@linux.com", "Linux", Role.ENGINEER, 9000
        );
        
        employeeService.addEmployee(ceo);
        employeeService.addEmployee(vp);
        employeeService.addEmployee(engineer);
        
        Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
        
        assertEquals(3, stats.size());
        assertEquals(1, stats.get("Microsoft").getEmployeesCount());
        assertEquals(1, stats.get("Apple").getEmployeesCount());
        assertEquals(1, stats.get("Linux").getEmployeesCount());
        assertEquals(30000.0, stats.get("Microsoft").getAverageSalary(), 0.01);
        assertEquals(20000.0, stats.get("Apple").getAverageSalary(), 0.01);
        assertEquals(9000.0, stats.get("Linux").getAverageSalary(), 0.01);
    }
}

