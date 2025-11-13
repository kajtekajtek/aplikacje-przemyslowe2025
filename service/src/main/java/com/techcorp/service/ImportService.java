package com.techcorp.service;

import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.techcorp.model.Employee;
import com.techcorp.model.ImportSummary;
import com.techcorp.model.Role;
import com.techcorp.model.exception.DuplicateEmailException;
import com.techcorp.model.exception.FileNotFoundException;
import com.techcorp.model.exception.InvalidDataException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.springframework.stereotype.Service;

@Service
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

    public ImportSummary importFromFile(String filePath) {
        validateFilePath(filePath);
        validateFileExists(filePath);

        String extension = getFileExtension(filePath);
        
        if (extension.equalsIgnoreCase("csv")) {
            return importFromCsv(filePath);
        } else if (extension.equalsIgnoreCase("xml")) {
            return importFromXml(filePath);
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + extension);
        }
    }

    private void validateFilePath(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        if (filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty");
        }
    }

    private void validateFileExists(String filePath) {
        if (!Files.exists(Path.of(filePath))) {
            throw new FileNotFoundException("File does not exist");
        }
    }

    private String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filePath.length() - 1) {
            return "";
        }
        return filePath.substring(lastDotIndex + 1);
    }

    private ImportSummary importFromCsv(String filePath) {
        summary = new ImportSummary();

        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            int    lineIdx  = 0;
            while ((line = reader.readLine()) != null) {
                lineIdx++;

                Employee employee = parseCsvLine(line, lineIdx);

                if (employee == null) continue;

                handleEmployee(employee, lineIdx);
            }
        } catch (IOException e) {
            summary.addError(0, new InvalidDataException(
                0, "Error reading CSV file: " + e.getMessage()
            ));
            return summary;
        }

        return summary;
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

        Role role = parseRole(fields[4], lineIdx);
        if (role == null) {
            return null;
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

        return new Employee(lastName, firstName, email, company, role, salary);
    }

    private ImportSummary importFromXml(String filePath) {
        summary = new ImportSummary();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(Path.of(filePath).toFile());
            document.getDocumentElement().normalize();

            NodeList employeeNodes = document.getElementsByTagName("employee");
            
            for (int i = 0; i < employeeNodes.getLength(); i++) {
                Element employeeElement = (Element) employeeNodes.item(i);
                int lineIdx = i + 1;
                
                Employee employee = parseXmlElement(employeeElement, lineIdx);
                
                if (employee == null) continue;
                
                handleEmployee(employee, lineIdx);
            }
        } catch (Exception e) {
            summary.addError(0, new InvalidDataException(
                0, "Error parsing XML file: " + e.getMessage()
            ));
            return summary;
        }

        return summary;
    }

    private Employee parseXmlElement(Element element, int lineIdx) {
        try {
            String firstName = getTextContent(element, "firstName");
            String lastName = getTextContent(element, "lastName");
            String email = getTextContent(element, "email");
            String company = getTextContent(element, "company");
            String positionStr = getTextContent(element, "position");
            String salaryStr = getTextContent(element, "salary");

            if (firstName.isEmpty() || lastName.isEmpty() || 
                email.isEmpty() || company.isEmpty()) {
                summary.addError(lineIdx, new InvalidDataException(
                    lineIdx, "Required fields cannot be empty"
                ));
                return null;
            }

            Role role = parseRole(positionStr, lineIdx);
            if (role == null) {
                return null;
            }

            int salary;
            try {
                salary = Integer.parseInt(salaryStr);
                if (salary <= 0) {
                    summary.addError(lineIdx, new InvalidDataException(
                        lineIdx, "Salary must be positive"
                    ));
                    return null;
                }
            } catch (NumberFormatException e) {
                summary.addError(lineIdx, new InvalidDataException(
                    lineIdx, "Invalid salary: " + salaryStr
                ));
                return null;
            }

            return new Employee(lastName, firstName, email, company, role, salary);
        } catch (Exception e) {
            summary.addError(lineIdx, new InvalidDataException(
                lineIdx, "Error parsing employee data: " + e.getMessage()
            ));
            return null;
        }
    }

    private String getTextContent(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            String content = nodeList.item(0).getTextContent();
            return content != null ? content.trim() : "";
        }
        return "";
    }

    private void handleEmployee(Employee employee, int lineIdx) {
        try {
            employeeService.addEmployee(employee);
            summary.addSuccessfullImport();
        } catch (IllegalArgumentException | DuplicateEmailException e) {
            summary.addError(lineIdx, new InvalidDataException(
                lineIdx, e.getMessage()
            ));
        }
    }

    private Role parseRole(String roleStr, int lineIdx) {
        if (roleStr == null || roleStr.isEmpty()) {
            summary.addError(lineIdx, new InvalidDataException(
                lineIdx, "Role cannot be empty"
            ));
            return null;
        }

        switch (roleStr.trim().toUpperCase()) {
            case "CEO":         return Role.CEO;
            case "VP":          return Role.VP;
            case "MANAGER":     return Role.MANAGER;
            case "ENGINEER":    return Role.ENGINEER;
            case "PROGRAMISTA": return Role.ENGINEER;
            case "INTERN":      return Role.INTERN;
            default:
                summary.addError(lineIdx, new InvalidDataException(
                    lineIdx, "Invalid role: " + roleStr
                ));
                return null;
        }
    }

}
