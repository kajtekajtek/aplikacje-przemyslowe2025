package com.techcorp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompanyStatisticsTest {
    private final long   employeesCount          = 10;
    private final double averageSalary           = 10000;
    private final String highestPaidEmployeeName = "John Doe";

    @Test
    public void testCompanyStatistics() {
        CompanyStatistics companyStatistics = new CompanyStatistics(
            employeesCount, averageSalary, highestPaidEmployeeName
        );
        assertEquals(employeesCount, companyStatistics.getEmployeesCount());
        assertEquals(averageSalary, companyStatistics.getAverageSalary());
        assertEquals(highestPaidEmployeeName, companyStatistics.getHighestPaidEmployeeName());
    }
}
