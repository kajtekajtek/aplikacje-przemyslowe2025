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
    
    private final String SURNAME_1      = "Baggins";
    private final String NAME_1         = "Frodo";
    private final String EMAIL_1        = "frodo.baggins@techcorp.com";
    private final String COMPANY_NAME_1 = "TechCorp";
    private final Role   ROLE_1         = Role.ENGINEER;
    private final int    SALARY_1       = 8500;
    
    private final String SURNAME_2      = "Gamgee";
    private final String NAME_2         = "Sam";
    private final String EMAIL_2        = "sam.gamgee@techcorp.com";
    private final String COMPANY_NAME_2 = "TechCorp";
    private final Role   ROLE_2         = Role.INTERN;
    private final int    SALARY_2       = 3500;
    
    private final String SURNAME_3      = "Eustace";
    private final String NAME_3         = "Scrubb";
    private final String EMAIL_3        = "scrubb.eustace@innovate.com";
    private final String COMPANY_NAME_3 = "Innovate";
    private final Role   ROLE_3         = Role.MANAGER;
    private final int    SALARY_3       = 13000;
    
    private final String SURNAME_4      = "Took";
    private final String NAME_4         = "Pippin";
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        
        employeeService.addEmployee(employee);
        
        assertEquals(1, employeeService.getEmployees().size());
        assertTrue(employeeService.getEmployees().contains(employee));
    }

    @Test
    public void testAddMultipleEmployees()
    {
        Employee employee1 = new Employee(
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        Employee employee2 = new Employee(
            SURNAME_2, NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, SALARY_2
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        Employee employee2 = new Employee(
            SURNAME_2, NAME_2, EMAIL_1, COMPANY_NAME_2, ROLE_2, SALARY_2
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        Employee employee2 = new Employee(
            SURNAME_2, NAME_2, EMAIL_1.toUpperCase(), COMPANY_NAME_2, ROLE_2, SALARY_2
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        
        employeeService.addEmployee(employee);
        assertEquals(1, employeeService.getEmployees().size());
        
        employeeService.removeEmployee(employee);
        assertEquals(0, employeeService.getEmployees().size());
    }

    @Test
    public void testRemoveEmployeeFromMultiple()
    {
        Employee employee1 = new Employee(
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        Employee employee2 = new Employee(
            SURNAME_2, NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, SALARY_2
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        Employee employee2 = new Employee(
            SURNAME_2, NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, SALARY_2
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        Employee employee2 = new Employee(
            SURNAME_2, NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, SALARY_2
        );
        Employee employee3 = new Employee(
            SURNAME_3, NAME_3, EMAIL_3, COMPANY_NAME_3, ROLE_3, SALARY_3
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        
        employeeService.addEmployee(employee);
        
        List<Employee> employees = employeeService.getEmployeesByCompanyName("NonExistent");
        
        assertEquals(0, employees.size());
    }

    @Test
    public void testGetEmployeesByRole()
    {
        Employee employee1 = new Employee(
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, Role.ENGINEER, SALARY_1
        );
        Employee employee2 = new Employee(
            SURNAME_2, NAME_2, EMAIL_2, COMPANY_NAME_2, Role.INTERN, SALARY_2
        );
        Employee employee3 = new Employee(
            SURNAME_3, NAME_3, EMAIL_3, COMPANY_NAME_3, Role.ENGINEER, SALARY_3
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
        assertEquals("Adams", sortedEmployees.get(0).getSurname());
        assertEquals("Brown", sortedEmployees.get(1).getSurname());
        assertEquals("Smith", sortedEmployees.get(2).getSurname());
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, Role.ENGINEER, SALARY_1
        );
        Employee employee2 = new Employee(
            SURNAME_2, NAME_2, EMAIL_2, COMPANY_NAME_2, Role.INTERN, SALARY_2
        );
        Employee employee3 = new Employee(
            SURNAME_3, NAME_3, EMAIL_3, COMPANY_NAME_3, Role.ENGINEER, SALARY_3
        );
        Employee employee4 = new Employee(
            SURNAME_4, NAME_4, EMAIL_4, COMPANY_NAME_4, Role.ENGINEER, SALARY_4
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, 8500
        );
        Employee employee2 = new Employee(
            SURNAME_2, NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, 3500
        );
        Employee employee3 = new Employee(
            SURNAME_3, NAME_3, EMAIL_3, COMPANY_NAME_3, ROLE_3, 15000
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, 8000
        );
        Employee employee2 = new Employee(
            SURNAME_2, NAME_2, EMAIL_2, COMPANY_NAME_2, ROLE_2, 4000
        );
        Employee employee3 = new Employee(
            SURNAME_3, NAME_3, EMAIL_3, COMPANY_NAME_3, ROLE_3, 12000
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
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
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
        );
        
        employeeService.addEmployee(employee);
        
        assertDoesNotThrow(() -> employeeService.printEmployees());
    }

    @Test
    public void testGetEmployeesReturnsActualList()
    {
        Employee employee = new Employee(
            SURNAME_1, NAME_1, EMAIL_1, COMPANY_NAME_1, ROLE_1, SALARY_1
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
}

