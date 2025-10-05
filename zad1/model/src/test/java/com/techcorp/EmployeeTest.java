package com.techcorp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTest
{
    private final String SURNAME = "Baggins";
    private final String NAME    = "Frodo";
    private final String EMAIL   = "frodo.baggins@techcorp.com";
    private final Role   ROLE    = Role.ENGINEER;
    private final int    SALARY  = 8500;

    @Test
    public void testEmployeeConstructorAndGetters()
    {
        Employee employee = new Employee(SURNAME, NAME, EMAIL, ROLE, SALARY);
                    
        assertEquals(SURNAME, employee.getSurname());
        assertEquals(NAME, employee.getName());
        assertEquals(EMAIL, employee.getEmailAddress());
        assertEquals(ROLE, employee.getRole());
        assertEquals(SALARY, employee.getSalary());
    }

    @Test
    public void testCreateEmployee()
    {
        Employee employee = Employee.createEmployee(SURNAME, NAME, EMAIL, ROLE);

        assertEquals(SURNAME, employee.getSurname());
        assertEquals(NAME, employee.getName());
        assertEquals(EMAIL, employee.getEmailAddress());
        assertEquals(ROLE, employee.getRole());
        assertEquals(ROLE.getBaseSalary(), employee.getSalary());
    }

    @Test
    public void testGetFullName()
    {
        String fullName = NAME + " " + SURNAME;

        Employee employee = new Employee(SURNAME, NAME, EMAIL, ROLE, SALARY);

        assertEquals(fullName, employee.getFullName());
    }

    @Test
    public void testSetSalary()
    {
        Employee employee = new Employee(SURNAME, NAME, EMAIL, ROLE, SALARY);
        employee.setSalary(10000);
        assertEquals(10000, employee.getSalary());
    }
    
    @Test
    public void testToString()
    {
        Employee employee = new Employee(SURNAME, NAME, EMAIL, ROLE, SALARY);
        assertEquals(NAME + " " + SURNAME + " " + ROLE.toString() + " " + EMAIL, employee.toString());
    }
    
    @Test
    public void testEqualsTrue()
    {
        Employee employee1 = new Employee(SURNAME, NAME, EMAIL, ROLE, SALARY);
        Employee employee2 = new Employee(SURNAME, NAME, EMAIL, ROLE, SALARY);
        assertTrue(employee1.equals(employee2));
    }

    @Test
    public void testEqualsFalse()
    {
        Employee employee1 = new Employee(SURNAME, NAME, EMAIL, ROLE, SALARY);
        Employee employee2 = new Employee(SURNAME, NAME, "different@email.com", ROLE, SALARY);
        assertFalse(employee1.equals(employee2));
    }
    
    @Test
    public void testHashCode()
    {
        Employee employee = new Employee(SURNAME, NAME, EMAIL, ROLE, SALARY);
        assertEquals(EMAIL.hashCode(), employee.hashCode());
    }

}
