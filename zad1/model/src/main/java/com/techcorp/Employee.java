package com.techcorp;

public class Employee 
{
    private String surname;
    private String name;
    private String emailAddress;
    // TODO: enum stanowisko
    private int    salary;
    
    public Employee(String surname, 
                    String name, 
                    String emailAddress, 
                    int    salary) {
        this.surname      = surname;
        this.name         = name;
        this.emailAddress = emailAddress;
        this.salary       = salary;
    }
    
    public String getFullName()     { return this.name + " " + this.surname; }
    public String getName()         { return this.name; }
    public String getSurname()      { return this.surname; }
    public String getEmailAddress() { return this.emailAddress; }
    public int    getSalary()       { return this.salary; }

}
