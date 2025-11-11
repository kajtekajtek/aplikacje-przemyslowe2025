package com.techcorp.mapper;

import com.techcorp.CompanyStatistics;
import com.techcorp.dto.CompanyStatisticsDTO;

public class CompanyStatisticsMapper {

    public static CompanyStatisticsDTO entityToDto(
        CompanyStatistics stats
    ) {
        return new CompanyStatisticsDTO(
            stats.getCompanyName(),
            stats.getEmployeesCount(),
            stats.getAverageSalary(),
            stats.getHighestSalary(),
            stats.getTopEarnerName()
        );
    }

    public static CompanyStatistics dtoToEntity(CompanyStatisticsDTO dto) {
        return new CompanyStatistics(
            dto.getCompanyName(),
            dto.getEmployeeCount(),
            dto.getHighestSalary(),
            dto.getAverageSalary(),
            dto.getTopEarnerName()
        );
    }
}
