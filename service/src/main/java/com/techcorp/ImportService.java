package com.techcorp;

import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import com.techcorp.exception.InvalidDataException;

public class ImportService {
    private static final String HEADER     = "firstName,lastName,email,company,position,salary";
    private static final String DELIMITER  = ",";
    private static final int    NUM_FIELDS = 6;

    private ImportSummary summary;

    private EmployeeService employeeService;

    public ImportService(EmployeeService employeeService) {
        if (!(employeeService == null)) {
            this.employeeService = employeeService; return;
        }
        throw new IllegalArgumentException("Employee service cannot be null");
    }

    public ImportSummary importFromCsv(String filePath) throws IOException {
        validateFilePath(filePath);
        validateFileExists(filePath);

        summary = new ImportSummary();

        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            int    lineIdx  = 0;
            while ((line = reader.readLine()) != null) {
                lineIdx++;

                Employee employee = parseCsvLine(line, lineIdx);

                if (employee == null) continue;

                try {
                    employeeService.addEmployee(employee);
                } catch (IllegalArgumentException e) {
                    summary.addError(lineIdx, new InvalidDataException(
                        lineIdx, e.getMessage()
                    ));
                    continue;
                }

                summary.addSuccessfullImport();
            }
        }

        return summary;
    }

    private void validateFilePath(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        if (filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty");
        }
    }

    private void validateFileExists(String filePath) throws IOException {
        if (!Files.exists(Path.of(filePath))) {
            throw new IOException("File does not exist");
        }
    }

    private Employee parseCsvLine(String line, int lineIdx) {
        if (line.startsWith(HEADER) || line.isEmpty()) return null;

        String[] fields = line.split(DELIMITER);
        if (fields.length < NUM_FIELDS) {
            summary.addError(lineIdx, new InvalidDataException(
                lineIdx, "Invalid number of fields"
            ));
            return null;
        }

        String firstName = fields[0].trim();
        String lastName  = fields[1].trim();
        String email     = fields[2].trim();
        String company   = fields[3].trim();

        if (firstName.isEmpty() || lastName.isEmpty() || 
            email.isEmpty() || company.isEmpty()) {
            summary.addError(lineIdx, new InvalidDataException(
                lineIdx, "Required fields cannot be empty"
            ));
            return null;
        }

        Role role;
        switch (fields[4].trim().toUpperCase()) {
            case "CEO":         role = Role.CEO; break;
            case "VP":          role = Role.VP; break;
            case "MANAGER":     role = Role.MANAGER; break;
            case "ENGINEER":    role = Role.ENGINEER; break;
            case "PROGRAMISTA": role = Role.ENGINEER; break;
            case "INTERN":      role = Role.INTERN; break;
            default: {
                summary.addError(lineIdx, new InvalidDataException(
                    lineIdx, "Invalid role"
                ));
                return null;
            }
        }

        int salary;
        try {
            salary = Integer.parseInt(fields[5].trim());
            if (salary <= 0) {
                summary.addError(lineIdx, new InvalidDataException(
                    lineIdx, "Salary must be positive"
                ));
                return null;
            }
        } catch (NumberFormatException e) {
            summary.addError(lineIdx, new InvalidDataException(
                lineIdx, "Invalid salary"
            ));
            return null;
        }

        Employee employee = new Employee(
            lastName, firstName, email, company, role, salary
        );

   
        return employee;
    }
}
