package com.techcorp;

import java.util.Objects;

public class Employee 
{
    private String lastName;
    private String firstName;
    private String emailAddress;
    private String companyName;
    private Role   role;
    private int    salary;
    
    public Employee(String lastName, 
                    String firstName, 
                    String emailAddress, 
                    String companyName,
                    Role   role,
                    int    salary) {
        this.lastName     = lastName;
        this.firstName    = firstName;
        this.emailAddress = emailAddress;
        this.companyName  = companyName;
        this.role         = role;
        this.salary       = salary;
    }

    public static Employee createEmployee(String lastName, 
                                          String firstName, 
                                          String emailAddress, 
                                          String companyName,
                                          Role   role) {
        return new Employee(
            lastName, firstName, emailAddress, companyName, role, role.getBaseSalary()
        );
}

    public String getFullName()     { return this.firstName + " " + this.lastName; }
    public String getLastName()     { return this.lastName; }
    public String getFirstName()    { return this.firstName; }
    public String getEmailAddress() { return this.emailAddress; }
    public String getCompanyName()  { return this.companyName; }
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
