package com.techcorp;

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
    
    public String getFullName()     { return this.name + " " + this.surname; }
    public String getName()         { return this.name; }
    public String getSurname()      { return this.surname; }
    public String getEmailAddress() { return this.emailAddress; }
    public Role   getRole()         { return this.role; }
    public int    getSalary()       { return this.salary; }

}
