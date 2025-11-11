package com.techcorp;

import org.junit.jupiter.api.Test;

import com.techcorp.model.CompanyStatistics;

import static org.junit.jupiter.api.Assertions.*;

public class CompanyStatisticsTest {
    private final String companyName             = "Tech Corp";
    private final long   employeesCount          = 10;
    private final double averageSalary           = 10000;
    private final int    highestSalary           = 100000;
    private final String topEarnerName           = "John Doe";

    @Test
    public void testCompanyStatistics() {
        CompanyStatistics companyStatistics = new CompanyStatistics(
            companyName, employeesCount, highestSalary, averageSalary, topEarnerName
        );
        assertEquals(employeesCount, companyStatistics.getEmployeesCount());
        assertEquals(averageSalary, companyStatistics.getAverageSalary());
        assertEquals(highestSalary, companyStatistics.getHighestSalary());
        assertEquals(topEarnerName, companyStatistics.getTopEarnerName());
    }
}
