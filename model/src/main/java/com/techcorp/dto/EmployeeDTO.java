package com.techcorp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.techcorp.EmploymentStatus;
import com.techcorp.Role;

public class EmployeeDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String company;
    private Role   position;
    private int    salary;
    private EmploymentStatus status;

    public EmployeeDTO() {
    }

    @JsonCreator
    public EmployeeDTO(
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName")  String lastName,
        @JsonProperty("email")     String email,
        @JsonProperty("company")   String company,
        @JsonProperty("position")  Role   position,
        @JsonProperty("salary")    int    salary,
        @JsonProperty("status")    EmploymentStatus status
    ) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.company   = company;
        this.position  = position;
        this.salary    = salary;
        this.status    = status;
    }

    public String getFirstName()        { return firstName; }
    public String getLastName()         { return lastName; }
    public String getEmail()            { return email; }
    public String getCompany()          { return company; }
    public Role   getPosition()         { return position; }
    public int    getSalary()           { return salary; }
    public EmploymentStatus getStatus() { return status; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName = lastName; }
    public void setEmail(String email)         { this.email = email; }
    public void setCompany(String company)     { this.company = company; }
    public void setPosition(Role position)     { this.position = position; }
    public void setSalary(int salary)          { this.salary = salary; }
    public void setStatus(EmploymentStatus status) { this.status = status; }

}
