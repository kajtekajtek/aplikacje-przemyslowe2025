package com.techcorp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTest
{
    private final String SURNAME      = "Baggins";
    private final String NAME         = "Frodo";
    private final String EMAIL        = "frodo.baggins@techcorp.com";
    private final String COMPANY_NAME = "TechCorp";
    private final Role   ROLE         = Role.ENGINEER;
    private final int    SALARY       = 8500;

    @Test
    public void testEmployeeConstructorAndGetters()
    {
        Employee employee = new Employee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY
        );
                    
        assertEmployeeDetails(employee, SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, SALARY);
    }

    @Test
    public void testCreateEmployee()
    {
        Employee employee = Employee.createEmployee(
            SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE
        );

        assertEmployeeDetails(employee, SURNAME, NAME, EMAIL, COMPANY_NAME, ROLE, ROLE.getBaseSalary());
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
        assertEquals(NAME + " " + SURNAME + " " + ROLE.toString() + " " + EMAIL, employee.toString());
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

    private void assertEmployeeDetails(Employee employee, 
                                       String   lastName, 
                                       String   firstName, 
                                       String   email, 
                                       String   companyName, 
                                       Role     role, 
                                       int      salary) {
        assertEquals(lastName, employee.getLastName());
        assertEquals(firstName, employee.getFirstName());
        assertEquals(email, employee.getEmailAddress());
        assertEquals(companyName, employee.getCompanyName());
        assertEquals(role, employee.getRole());
        assertEquals(salary, employee.getSalary());
    }

}
