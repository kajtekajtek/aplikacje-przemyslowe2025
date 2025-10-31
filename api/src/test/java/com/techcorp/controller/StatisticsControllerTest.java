package com.techcorp.controller;

import com.techcorp.CompanyStatistics;
import com.techcorp.EmployeeService;
import com.techcorp.Role;
import com.techcorp.dto.CompanyStatisticsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.techcorp.exception.GlobalExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StatisticsController.class)
@ContextConfiguration(classes = {StatisticsController.class, GlobalExceptionHandler.class})
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private CompanyStatistics testCompanyStatistics;

    @BeforeEach
    void setUp() {
        testCompanyStatistics = new CompanyStatistics(
            "TechCorp", 10, 15000, 12000.0, "John Doe"
        );
    }

    @Test
    void getAverageSalary_WithoutCompany_ShouldReturn200AndAverageSalary() throws Exception {
        Double averageSalary = 10000.0;
        when(employeeService.getAverageSalary((String) null)).thenReturn(averageSalary);

        mockMvc.perform(get("/api/statistics/salary/average"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.averageSalary").value(10000.0));

        verify(employeeService, times(1)).getAverageSalary((String) null);
    }

    @Test
    void getAverageSalary_WithCompany_ShouldReturn200AndAverageSalary() throws Exception {
        Double averageSalary = 12000.0;
        when(employeeService.getAverageSalary("TechCorp")).thenReturn(averageSalary);

        mockMvc.perform(get("/api/statistics/salary/average")
                .param("company", "TechCorp"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.averageSalary").value(12000.0));

        verify(employeeService, times(1)).getAverageSalary("TechCorp");
    }

    @Test
    void getCompanyStatistics_ShouldReturn200AndCompanyStatistics() throws Exception {
        when(employeeService.getCompanyStatistics("TechCorp")).thenReturn(testCompanyStatistics);

        mockMvc.perform(get("/api/statistics/company/TechCorp"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.companyName").value("TechCorp"))
            .andExpect(jsonPath("$.employeeCount").value(10))
            .andExpect(jsonPath("$.highestSalary").value(15000))
            .andExpect(jsonPath("$.averageSalary").value(12000.0))
            .andExpect(jsonPath("$.topEarnerName").value("John Doe"));

        verify(employeeService, times(1)).getCompanyStatistics("TechCorp");
    }

    @Test
    void getPositionCount_ShouldReturn200AndPositionCount() throws Exception {
        Map<Role, Long> roleCount = new HashMap<>();
        roleCount.put(Role.ENGINEER, 5L);
        roleCount.put(Role.MANAGER, 3L);
        roleCount.put(Role.CEO, 1L);
        when(employeeService.getEmployeeCountByRole()).thenReturn(roleCount);

        mockMvc.perform(get("/api/statistics/positions"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.ENGINEER").value(5))
            .andExpect(jsonPath("$.MANAGER").value(3))
            .andExpect(jsonPath("$.CEO").value(1));

        verify(employeeService, times(1)).getEmployeeCountByRole();
    }

    @Test
    void getStatusDistribution_ShouldReturn200AndStatusDistribution() throws Exception {
        Map<com.techcorp.EmploymentStatus, Long> statusCount = new HashMap<>();
        statusCount.put(com.techcorp.EmploymentStatus.ACTIVE, 8L);
        statusCount.put(com.techcorp.EmploymentStatus.ON_LEAVE, 2L);
        statusCount.put(com.techcorp.EmploymentStatus.TERMINATED, 1L);
        when(employeeService.getStatusDistribution()).thenReturn(statusCount);

        mockMvc.perform(get("/api/statistics/status"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.ACTIVE").value(8))
            .andExpect(jsonPath("$.ON_LEAVE").value(2))
            .andExpect(jsonPath("$.TERMINATED").value(1));

        verify(employeeService, times(1)).getStatusDistribution();
    }
}

