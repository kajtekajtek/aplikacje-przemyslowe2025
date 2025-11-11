package com.techcorp.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.techcorp.model.EmploymentStatus;
import com.techcorp.model.Role;

public class EmployeeDTO {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String companyName;
    private Role   role;
    private int    salary;
    private EmploymentStatus status;

    public EmployeeDTO() { }

    @JsonCreator
    public EmployeeDTO(
        @JsonProperty("firstName")    String firstName,
        @JsonProperty("lastName")     String lastName,
        @JsonProperty("emailAddress") String emailAddress,
        @JsonProperty("companyName")  String companyName,
        @JsonProperty("role")         Role   role,
        @JsonProperty("salary")       int    salary,
        @JsonProperty("status") EmploymentStatus status
    ) {
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.emailAddress = emailAddress;
        this.companyName  = companyName;
        this.role         = role;
        this.salary       = salary;
        this.status       = status;
    }

    public String getFirstName()        { return firstName; }
    public String getLastName()         { return lastName; }
    public String getEmailAddress()     { return emailAddress; }
    public String getCompanyName()      { return companyName; }
    public Role   getRole()             { return role; }
    public int    getSalary()           { return salary; }
    public EmploymentStatus getStatus() { return status; }

    public void setFirstName(String firstName)       { this.firstName = firstName; }
    public void setLastName(String lastName)         { this.lastName = lastName; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public void setCompanyName(String companyName)   { this.companyName = companyName; }
    public void setRole(Role role)                   { this.role = role; }
    public void setSalary(int salary)                { this.salary = salary; }
    public void setStatus(EmploymentStatus status)   { this.status = status; }

}
