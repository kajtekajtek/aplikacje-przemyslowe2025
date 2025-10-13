package com.techcorp;

import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImportService {
    private static final String HEADER = "firstName,lastName,email,company,position,salary";

    private EmployeeService employeeService;

    public ImportService(EmployeeService employeeService) {
        if (!(employeeService == null)) {
            this.employeeService = employeeService; return;
        }
        throw new IllegalArgumentException("Employee service cannot be null");
    }

    public ImportSummary importFromCsv(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        if (!Files.exists(Path.of(filePath))) {
            throw new IOException("File does not exist");
        }

        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(HEADER) || line.isEmpty()) continue;

                String[] fields = line.split(",");
            }
        }

        return new ImportSummary();
    }
}
