package com.techcorp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyStatisticsDTO {

    private String companyName;
    private long   employeeCount;
    private double averageSalary;
    private int    highestSalary;
    private String topEarnerName;

    public CompanyStatisticsDTO() {}

    @JsonCreator
    public CompanyStatisticsDTO(
        @JsonProperty("companyName")   String companyName,
        @JsonProperty("employeeCount") long   employeeCount,
        @JsonProperty("averageSalary") double averageSalary,
        @JsonProperty("highestSalary") int    highestSalary,
        @JsonProperty("topEarnerName") String topEarnerName
    ) {
        this.companyName   = companyName;
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary;
        this.highestSalary = highestSalary;
        this.topEarnerName = topEarnerName;
    }

    public String getCompanyName()   { return companyName;   }
    public long   getEmployeeCount() { return employeeCount; }
    public double getAverageSalary() { return averageSalary; }
    public int    getHighestSalary() { return highestSalary; }
    public String getTopEarnerName() { return topEarnerName; }

    public void setCompanyName  (String companyName)   { this.companyName   = companyName;   }
    public void setEmployeeCount(long employeeCount)   { this.employeeCount = employeeCount; }
    public void setAverageSalary(double averageSalary) { this.averageSalary = averageSalary; }
    public void setHighestSalary(int highestSalary)    { this.highestSalary = highestSalary; }
    public void setTopEarnerName(String topEarnerName) { this.topEarnerName = topEarnerName; }
}
