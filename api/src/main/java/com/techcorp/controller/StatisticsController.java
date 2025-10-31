package com.techcorp.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import com.techcorp.EmployeeService;
import com.techcorp.CompanyStatistics;
import com.techcorp.dto.CompanyStatisticsDTO;
import com.techcorp.CompanyStatisticsMapper;
import com.techcorp.EmploymentStatus;
import com.techcorp.Role;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final EmployeeService employeeService;

    public StatisticsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/salary/average")
    public ResponseEntity<Map<String, Double>> getAverageSalary(
        @RequestParam(required = false) String company
    ) {
        Double averageSalary = employeeService.getAverageSalary(company);
        Map<String, Double> response = new HashMap<>();
        response.put("averageSalary", averageSalary);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyName}")
    public ResponseEntity<CompanyStatisticsDTO> getCompanyStatistics(
        @PathVariable String companyName
    ) {
        CompanyStatistics    cs  = employeeService.getCompanyStatistics(companyName);
        CompanyStatisticsDTO dto = CompanyStatisticsMapper.entityToDto(cs);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/positions")
    public ResponseEntity<Map<String, Long>> getPositionCount() {
        Map<Role, Long> roleCount = employeeService.getEmployeeCountByRole();
        Map<String, Long> response = roleCount.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().toString(),
                entry -> entry.getValue()
            ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Long>> getStatusDistribution() {
        Map<EmploymentStatus, Long> statusCount = employeeService.getStatusDistribution();
        Map<String, Long> response = statusCount.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().toString(),
                entry -> entry.getValue()
            ));
        return ResponseEntity.ok(response);
    }
}

