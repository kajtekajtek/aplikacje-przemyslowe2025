package com.techcorp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import com.techcorp.mapper.EmployeeMapper;
import com.techcorp.model.Employee;
import com.techcorp.model.EmploymentStatus;
import com.techcorp.model.dto.EmployeeDTO;
import com.techcorp.model.exception.DuplicateEmailException;
import com.techcorp.model.exception.EmployeeNotFoundException;
import com.techcorp.service.EmployeeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(
            EmployeeMapper.entityToDTOList(employeeService.getEmployees())
        );
    }

    @GetMapping(params = "company")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByCompanyName(
            @RequestParam(value = "company", required = false) String companyName) {
        List<Employee> employees;
        if (companyName == null || companyName.trim().isEmpty()) {
            employees = employeeService.getEmployees();
        } else {
            employees = employeeService.getEmployeesByCompanyName(companyName);
        }
        return ResponseEntity.ok(
            EmployeeMapper.entityToDTOList(employees)
        );
    }

    @GetMapping("/{email}")
    public ResponseEntity<EmployeeDTO> getEmployeeByEmail(@PathVariable String email) {
        Employee employee = employeeService.getEmployeeByEmail(email)
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + email + " not found"
            ));
        return ResponseEntity.ok(EmployeeMapper.entityToDTO(employee));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByStatus(
        @PathVariable String status
    ) {
        EmploymentStatus employmentStatus;
        try {
            employmentStatus = EmploymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        
        List<Employee> employees = employeeService.getEmployeesByStatus(employmentStatus);
        return ResponseEntity.ok(
            EmployeeMapper.entityToDTOList(employees)
        );
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = EmployeeMapper.dtoToEntity(employeeDTO);
        employeeService.addEmployee(employee);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{email}")
            .buildAndExpand(employee.getEmailAddress())
            .toUri();
        
        return ResponseEntity
            .created(location)
            .body(EmployeeMapper.entityToDTO(employee));
    }

    @PutMapping("/{email}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
        @PathVariable String email,
        @RequestBody EmployeeDTO employeeDTO
    ) {
        Employee updatedEmployee = EmployeeMapper.dtoToEntity(employeeDTO);
        employeeService.updateEmployee(email, updatedEmployee);
        
        Employee savedEmployee = employeeService.getEmployeeByEmail(updatedEmployee.getEmailAddress())
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + updatedEmployee.getEmailAddress() + " not found after update"
            ));
        
        return ResponseEntity.ok(EmployeeMapper.entityToDTO(savedEmployee));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String email) {
        employeeService.removeEmployeeByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{email}/status")
    public ResponseEntity<EmployeeDTO> updateEmployeeStatus(
        @PathVariable String email,
        @RequestBody Map<String, String> statusMap
    ) {
        String statusStr = statusMap.get("status");
        if (statusStr == null) {
            throw new IllegalArgumentException("Status field is required");
        }
        
        EmploymentStatus status;
        try {
            status = EmploymentStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + statusStr);
        }
        
        employeeService.updateEmployeeStatus(email, status);
        
        Employee employee = employeeService.getEmployeeByEmail(email)
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + email + " not found after status update"
            ));
        
        return ResponseEntity.ok(EmployeeMapper.entityToDTO(employee));
    }
}
