package com.techcorp;

public class CompanyStatistics {
    private final long   employeesCount;
    private final double averageSalary;
    private final String highestPaidEmployeeName;

    public CompanyStatistics(
        long   employeesCount, 
        double averageSalary, 
        String highestPaidEmployeeName
    ) {
        this.employeesCount          = employeesCount;
        this.averageSalary           = averageSalary;
        this.highestPaidEmployeeName = highestPaidEmployeeName;
    }

    public long   getEmployeesCount()          { return employeesCount; }
    public double getAverageSalary()           { return averageSalary; }
    public String getHighestPaidEmployeeName() { return highestPaidEmployeeName; }
}
