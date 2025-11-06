package com.techcorp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTest
{
    private final String SURNAME      = "Baggins";
    private final String NAME         = "Frodo";
    private final String EMAIL        = "frodo.baggins@techcorp.com";
    private final String COMPANY_NAME = "TechCorp";
    private final Role   ROLE         = Role.ENGINEER;
    private final int    SALARY       = 8500;
    private final EmploymentStatus STATUS = EmploymentStatus.ACTIVE;

    @Nested
    class ConstructorTest {

        @Test
        public void givenValidParametersConstructorCreatesEmployee()
        {
            Employee employee = new Employee(
                SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
            );
                        
            assertEmployeeDetails(
                employee, SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY, STATUS
            );
        }

        @Test
        public void givenValidParametersWithStatusConstructorCreatesEmployee()
        {
            Employee employee = new Employee(
                SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY, EmploymentStatus.ON_LEAVE
            );
                        
            assertEmployeeDetails(
                employee, SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY, EmploymentStatus.ON_LEAVE
            );
        }

        @Test
        public void givenValidParametersCreateEmployeeCreatesEmployee()
        {
            Employee employee = Employee.createEmployee(
                SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE
            );

            assertEmployeeDetails(
                employee, SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, ROLE.getBaseSalary(), STATUS
            );
        }

        @Test
        public void givenEmptyParametersConstructorThrowsIllegalArgumentException()
        {
            assertAll(
                () -> assertConstructorThrowsIllegalArgumentException(
                    "", NAME, EMAIL, COMPANY_NAME, ROLE, SALARY, STATUS),
                () -> assertConstructorThrowsIllegalArgumentException(
                    SURNAME, "", EMAIL, COMPANY_NAME, ROLE, SALARY, STATUS),
                () -> assertConstructorThrowsIllegalArgumentException(
                    SURNAME, NAME, "", COMPANY_NAME, ROLE, SALARY, STATUS),
                () -> assertConstructorThrowsIllegalArgumentException(
                    SURNAME, NAME, EMAIL, "", ROLE, SALARY, STATUS)
            );
        }

        @Test
        public void givenNullParametersConstructorThrowsIllegalArgumentException()
        {
            assertAll(
                () -> assertConstructorThrowsIllegalArgumentException(
                    null, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY, STATUS),
                () -> assertConstructorThrowsIllegalArgumentException(
                    SURNAME, null, EMAIL, COMPANY_NAME, ROLE, SALARY, STATUS),
                () -> assertConstructorThrowsIllegalArgumentException(
                    SURNAME, NAME, null, COMPANY_NAME, ROLE, SALARY, STATUS),
                () -> assertConstructorThrowsIllegalArgumentException(
                    SURNAME, NAME, EMAIL, null, ROLE, SALARY, STATUS),
                () -> assertConstructorThrowsIllegalArgumentException(
                    SURNAME, NAME, EMAIL, COMPANY_NAME, null, SALARY, STATUS),
                () -> assertConstructorThrowsIllegalArgumentException(
                    SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY, null)
            );
        }

        @Test
        public void givenNegativeSalaryConstructorThrowsIllegalArgumentException()
        {
            assertAll(
                () -> assertConstructorThrowsIllegalArgumentException(
                    SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, -1, STATUS)
            );
        }

        private void assertConstructorThrowsIllegalArgumentException(
            String   lastName, 
            String   firstName, 
            String   email, 
            String   companyName, 
            Role     role, 
            int      salary,
            EmploymentStatus status
        ) {
            assertThrows(IllegalArgumentException.class, () -> new Employee(
                lastName, firstName, email, companyName, role, salary, status
            ));
        }

    }

    @Test
    public void testGetFullName()
    {
        String fullName = NAME + " " + SURNAME;

        Employee employee = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );

        assertEquals(fullName, employee.getFullName());
    }

    @Test
    public void testSetSalary()
    {
        Employee employee = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
        employee.setSalary(10000);
        assertEquals(10000, employee.getSalary());
    }
    
    @Test
    public void testToString()
    {
        Employee employee = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
        String expected = NAME + " " + SURNAME + " " + 
            ROLE.toString() + " " + EMAIL + " " + 
            employee.getStatus().toString();

        assertEquals(expected, employee.toString());
    }

    @Test
    public void testToStringWithDifferentStatuses()
    {
        Employee activeEmployee = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY, EmploymentStatus.ACTIVE
        );
        Employee onLeaveEmployee = new Employee(
            SURNAME, NAME, "onleave@techcorp.com", COMPANY_NAME, ROLE, SALARY, EmploymentStatus.ON_LEAVE
        );
        Employee terminatedEmployee = new Employee(
            SURNAME, NAME, "terminated@techcorp.com", COMPANY_NAME, ROLE, SALARY, EmploymentStatus.TERMINATED
        );

        assertTrue(activeEmployee.toString().contains("ACTIVE"));
        assertTrue(onLeaveEmployee.toString().contains("ON_LEAVE"));
        assertTrue(terminatedEmployee.toString().contains("TERMINATED"));
    }
    
    @Test
    public void testEqualsTrue()
    {
        Employee employee1 = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
        Employee employee2 = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
        assertTrue(employee1.equals(employee2));
    }

    @Test
    public void testEqualsFalse()
    {
        Employee employee1 = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
        Employee employee2 = new Employee(
            SURNAME, NAME, "different@email.com", COMPANY_NAME, ROLE, SALARY
        );
        assertFalse(employee1.equals(employee2));
    }
    
    @Test
    public void testHashCode()
    {
        Employee employee1 = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
        Employee employee2 = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
        assertEquals(employee1.hashCode(), employee2.hashCode());
    }

    @Test
    public void testGetStatus()
    {
        Employee employee = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
        assertEquals(EmploymentStatus.ACTIVE, employee.getStatus());
    }

    @Test
    public void testGetStatusWithDifferentStatus()
    {
        Employee employee = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY, EmploymentStatus.TERMINATED
        );
        assertEquals(EmploymentStatus.TERMINATED, employee.getStatus());
    }

    @Test
    public void testSetStatus()
    {
        Employee employee = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
        employee.setStatus(EmploymentStatus.ON_LEAVE);
        assertEquals(EmploymentStatus.ON_LEAVE, employee.getStatus());
    }

    @Test
    public void testSetStatusToTerminated()
    {
        Employee employee = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
        employee.setStatus(EmploymentStatus.TERMINATED);
        assertEquals(EmploymentStatus.TERMINATED, employee.getStatus());
    }

    @Test
    public void testSetStatusNullThrowsIllegalArgumentException()
    {
        Employee employee = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
        assertThrows(IllegalArgumentException.class, () -> employee.setStatus(null));
    }

    @Test
    public void testAllEmploymentStatuses()
    {
        Employee activeEmployee = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY, EmploymentStatus.ACTIVE
        );
        Employee onLeaveEmployee = new Employee(
            SURNAME, NAME, "onleave@techcorp.com", COMPANY_NAME, ROLE, SALARY, EmploymentStatus.ON_LEAVE
        );
        Employee terminatedEmployee = new Employee(
            SURNAME, NAME, "terminated@techcorp.com", COMPANY_NAME, ROLE, SALARY, EmploymentStatus.TERMINATED
        );

        assertEquals(EmploymentStatus.ACTIVE, activeEmployee.getStatus());
        assertEquals(EmploymentStatus.ON_LEAVE, onLeaveEmployee.getStatus());
        assertEquals(EmploymentStatus.TERMINATED, terminatedEmployee.getStatus());
    }

    private void assertEmployeeDetails(Employee employee, 
                                       String   lastName, 
                                       String   firstName, 
                                       String   email, 
                                       String   companyName, 
                                       Role     role, 
                                       int      salary,
                                       EmploymentStatus status) {
        assertEquals(lastName, employee.getLastName());
        assertEquals(firstName, employee.getFirstName());
        assertEquals(email, employee.getEmailAddress());
        assertEquals(companyName, employee.getCompanyName());
        assertEquals(role, employee.getRole());
        assertEquals(salary, employee.getSalary());
        assertEquals(status, employee.getStatus());
    }

}
