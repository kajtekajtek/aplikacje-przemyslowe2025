package com.techcorp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import com.techcorp.dto.EmployeeDTO;
import com.techcorp.EmployeeService;
import com.techcorp.EmployeeMapper;
import org.springframework.http.ResponseEntity;
import java.util.List;

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
}
