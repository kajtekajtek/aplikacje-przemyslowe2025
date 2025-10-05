package com.techcorp;

import java.util.Objects;

public class Employee 
{
    private String surname;
    private String name;
    private String emailAddress;
    private Role   role;
    private int    salary;
    
    public Employee(String surname, 
                    String name, 
                    String emailAddress, 
                    Role   role,
                    int    salary) {
        this.surname      = surname;
        this.name         = name;
        this.emailAddress = emailAddress;
        this.role         = role;
        this.salary       = salary;
    }

    public static Employee createEmployee(String surname, 
                                          String name, 
                                          String emailAddress, 
                                          Role   role) {
        return new Employee(
            surname, name, emailAddress, role, role.getBaseSalary()
        );
}

    public String getFullName()     { return this.name + " " + this.surname; }
    public String getName()         { return this.name; }
    public String getSurname()      { return this.surname; }
    public String getEmailAddress() { return this.emailAddress; }
    public Role   getRole()         { return this.role; }
    public int    getSalary()       { return this.salary; }

    public void setSalary(int salary) { this.salary = salary; }

    @Override
    public String toString() {
        return getFullName() 
             + " " 
             + getRole().toString() 
             + " " 
             + getEmailAddress();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;

        Employee employee = (Employee) obj;

        return Objects.equals(emailAddress, employee.emailAddress);
    }

    @Override
    public int hashCode() { return Objects.hash(emailAddress); }

}
