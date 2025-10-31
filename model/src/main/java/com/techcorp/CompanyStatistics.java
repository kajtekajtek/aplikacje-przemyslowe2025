package com.techcorp;

public class CompanyStatistics {
    
    private final String companyName;
    private final long   employeesCount;
    private final double averageSalary;
    private final int    highestSalary;
    private final String topEarnerName;

    public CompanyStatistics(
        String companyName,
        long   employeesCount,
        int    highestSalary,
        double averageSalary, 
        String topEarnerName
    ) {
        this.companyName             = companyName;
        this.employeesCount          = employeesCount;
        this.highestSalary           = highestSalary;
        this.averageSalary           = averageSalary;
        this.topEarnerName           = topEarnerName;
    }

    public String getCompanyName()             { return companyName; }
    public long   getEmployeesCount()          { return employeesCount; }
    public int    getHighestSalary()           { return highestSalary; }
    public double getAverageSalary()           { return averageSalary; }
    public String getTopEarnerName()           { return topEarnerName; }
}
