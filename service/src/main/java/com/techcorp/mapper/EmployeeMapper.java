package com.techcorp.mapper;

import com.google.gson.JsonObject;
import com.techcorp.Employee;
import com.techcorp.Role;
import com.techcorp.dto.EmployeeDTO;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeMapper {

    public static EmployeeDTO entityToDTO(Employee employee) {
        return new EmployeeDTO(
            employee.getFirstName(),
            employee.getLastName(),
            employee.getEmailAddress(),
            employee.getCompanyName(),
            employee.getRole(),
            employee.getSalary(),
            employee.getStatus()
        );
    }

    public static Employee dtoToEntity(EmployeeDTO employeeDTO) {
        return new Employee(
            employeeDTO.getLastName(),
            employeeDTO.getFirstName(),
            employeeDTO.getEmailAddress(),
            employeeDTO.getCompanyName(),
            employeeDTO.getRole(),
            employeeDTO.getSalary(),
            employeeDTO.getStatus()
        );
    }

    public static List<EmployeeDTO> entityToDTOList(List<Employee> employees) {
        return employees.stream()
            .map(EmployeeMapper::entityToDTO)
            .collect(Collectors.toList());
    }

    public static List<Employee> dtoToEntitiesList(List<EmployeeDTO> employeeDTOs) {
        return employeeDTOs.stream()
            .map(EmployeeMapper::dtoToEntity)
            .collect(Collectors.toList());
    }

    public static Employee jsonObjectToEmployee(JsonObject jsonObject) {
        String fullName = jsonObject.get("name").getAsString();
        String[] fullNameSplit = parseFullName(fullName);
        String firstName = fullNameSplit[0];
        String lastName  = fullNameSplit[1];

        String email = jsonObject.get("email").getAsString();

        String companyName = jsonObject.getAsJsonObject("company")
                                       .get("name")
                                       .getAsString();

        Employee employee = Employee.createEmployee(
                lastName,
                firstName,
                email,
                companyName,
                Role.ENGINEER
        );

        return employee;
    }

    private static String[] parseFullName(String fullName) {
        String[] fullNameSplit = fullName.trim().split("\\s+", 2);
        String firstName = fullNameSplit.length > 0 
            ? fullNameSplit[0] 
            : "";
        String lastName = fullNameSplit.length > 1 
            ? fullNameSplit[1] 
            : "";

        return new String[] { firstName, lastName };
    }
}
