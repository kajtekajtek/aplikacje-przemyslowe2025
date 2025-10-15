package com.techcorp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ImportServiceTest
{
    private EmployeeService employeeService;
    private ImportService   importService;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp()
    {
        employeeService = new EmployeeService();
        importService   = new ImportService(employeeService);
    }

    @AfterEach
    public void tearDown()
    {
        employeeService = null;
        importService   = null;
    }

    @Test
    public void testConstructorWithEmployeeService()
    {
        EmployeeService service = new EmployeeService();
        ImportService importer  = new ImportService(service);
        
        assertNotNull(importer);
    }

    @Test
    public void testConstructorWithNullEmployeeService()
    {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ImportService(null)
        );
        
        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    public void testImportFromCsvWithValidData() throws IOException
    {
        Path csvFile = tempDir.resolve("valid_employees.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n" +
            "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,INTERN,3500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(3, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(3, employeeService.getEmployees().size());
    }

    @Test
    public void testImportFromCsvWithRoleMapping() throws IOException
    {
        Path csvFile = tempDir.resolve("role_mapping.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "Jan,Kowalski,jan.kowalski@techcorp.com,TechCorp,PROGRAMISTA,8500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(1, employeeService.getEmployees().size());
        
        Employee employee = employeeService.getEmployees().get(0);
        assertEquals(Role.ENGINEER, employee.getRole());
        assertEquals("Jan", employee.getFirstName());
        assertEquals("Kowalski", employee.getLastName());
    }

    @Test
    public void testImportFromCsvWithInvalidRole() throws IOException
    {
        Path csvFile = tempDir.resolve("invalid_role.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,INVALID_ROLE,8500\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
        assertEquals(1, employeeService.getEmployees().size());
        assertEquals("Jane", employeeService.getEmployees().get(0).getFirstName());
    }

    @Test
    public void testImportFromCsvWithNegativeSalary() throws IOException
    {
        Path csvFile = tempDir.resolve("negative_salary.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,-1000\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
        assertEquals(1, employeeService.getEmployees().size());
    }

    @Test
    public void testImportFromCsvWithZeroSalary() throws IOException
    {
        Path csvFile = tempDir.resolve("zero_salary.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,0\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
    }

    @Test
    public void testImportFromCsvWithNonNumericSalary() throws IOException
    {
        Path csvFile = tempDir.resolve("non_numeric_salary.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,invalid\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
    }

    @Test
    public void testImportFromCsvWithMissingFields() throws IOException
    {
        Path csvFile = tempDir.resolve("missing_fields.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
    }

    @Test
    public void testImportFromCsvWithEmptyLines() throws IOException
    {
        Path csvFile = tempDir.resolve("empty_lines.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n" +
            "\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n" +
            "\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(2, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(2, employeeService.getEmployees().size());
    }

    @Test
    public void testImportFromCsvWithOnlyHeader() throws IOException
    {
        Path csvFile = tempDir.resolve("only_header.csv");
        String csvContent = "firstName,lastName,email,company,position,salary\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(0, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(0, employeeService.getEmployees().size());
    }

    @Test
    public void testImportFromCsvWithEmptyFile() throws IOException
    {
        Path csvFile = tempDir.resolve("empty.csv");
        Files.writeString(csvFile, "");

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(0, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(0, employeeService.getEmployees().size());
    }

    @Test
    public void testImportFromCsvWithNonExistentFile()
    {
        String nonExistentPath = tempDir.resolve("nonexistent.csv").toString();

        IOException exception = assertThrows(
            IOException.class,
            () -> importService.importFromCsv(nonExistentPath)
        );
        
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testImportFromCsvWithNullFilePath()
    {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> importService.importFromCsv(null)
        );
        
        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    public void testImportFromCsvWithEmptyFilePath()
    {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> importService.importFromCsv("")
        );
        
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    public void testImportFromCsvWithDuplicateEmail() throws IOException
    {
        Path csvFile = tempDir.resolve("duplicate_email.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n" +
            "Jane,Doe,john.doe@techcorp.com,TechCorp,MANAGER,12500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(3));
        assertEquals(1, employeeService.getEmployees().size());
    }

    @Test
    public void testImportFromCsvContinuesOnErrors() throws IOException
    {
        Path csvFile = tempDir.resolve("mixed_data.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n" +
            "Invalid,Line,invalid@test.com,Test,INVALID,9000\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n" +
            "Bad,Salary,bad@test.com,Test,ENGINEER,-500\n" +
            "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,INTERN,3500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(3, summary.getSuccessCount());
        assertEquals(2, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(3));
        assertTrue(summary.getErrors().containsKey(5));
        assertEquals(3, employeeService.getEmployees().size());
    }

    @Test
    public void testImportFromCsvErrorLineNumbers() throws IOException
    {
        Path csvFile = tempDir.resolve("error_lines.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,INVALID_ROLE,8500\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,-100\n" +
            "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,ENGINEER,8500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(2, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
        assertTrue(summary.getErrors().containsKey(3));
        assertFalse(summary.getErrors().containsKey(4));
    }

    @Test
    public void testImportFromCsvWithAllRoles() throws IOException
    {
        Path csvFile = tempDir.resolve("all_roles.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Ceo,john.ceo@techcorp.com,TechCorp,CEO,25000\n" +
            "Jane,Vp,jane.vp@techcorp.com,TechCorp,VP,18000\n" +
            "Bob,Manager,bob.manager@techcorp.com,TechCorp,MANAGER,12000\n" +
            "Alice,Engineer,alice.engineer@techcorp.com,TechCorp,ENGINEER,8000\n" +
            "Tom,Intern,tom.intern@techcorp.com,TechCorp,INTERN,3000\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(5, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(5, employeeService.getEmployees().size());
        
        Map<Role, Long> countByRole = employeeService.getEmployeeCountByRole();
        assertEquals(1L, countByRole.get(Role.CEO));
        assertEquals(1L, countByRole.get(Role.VP));
        assertEquals(1L, countByRole.get(Role.MANAGER));
        assertEquals(1L, countByRole.get(Role.ENGINEER));
        assertEquals(1L, countByRole.get(Role.INTERN));
    }

    @Test
    public void testImportFromCsvWithWhitespaceInFields() throws IOException
    {
        Path csvFile = tempDir.resolve("whitespace_fields.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            " John , Doe , john.doe@techcorp.com , TechCorp , ENGINEER , 8500 \n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(1, employeeService.getEmployees().size());
        
        Employee employee = employeeService.getEmployees().get(0);
        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
        assertEquals("john.doe@techcorp.com", employee.getEmailAddress());
        assertEquals("TechCorp", employee.getCompanyName());
        assertEquals(Role.ENGINEER, employee.getRole());
        assertEquals(8500, employee.getSalary());
    }

    @Test
    public void testImportFromCsvWithExtraFields() throws IOException
    {
        Path csvFile = tempDir.resolve("extra_fields.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,extra,fields\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(1, employeeService.getEmployees().size());
    }

    @Test
    public void testImportFromCsvAddsToExistingEmployees() throws IOException
    {
        Employee existingEmployee = new Employee(
            "Existing", "Employee", "existing@techcorp.com", 
            "TechCorp", Role.MANAGER, 12000
        );
        employeeService.addEmployee(existingEmployee);
        
        Path csvFile = tempDir.resolve("additional_employees.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(2, employeeService.getEmployees().size());
        assertTrue(employeeService.getEmployees().contains(existingEmployee));
    }

    @Test
    public void testImportFromCsvWithCaseInsensitiveRoles() throws IOException
    {
        Path csvFile = tempDir.resolve("case_insensitive_roles.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,engineer,8500\n" +
            "Jane,Smith,jane.smith@techcorp.com,TechCorp,MANAGER,12500\n" +
            "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,Manager,12000\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(3, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(3, employeeService.getEmployees().size());
    }

    @Test
    public void testImportSummaryContainsCorrectErrorExceptions() throws IOException
    {
        Path csvFile = tempDir.resolve("error_types.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,INVALID_ROLE,8500\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,-100\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(0, summary.getSuccessCount());
        assertEquals(2, summary.getErrors().size());
        
        Exception error1 = summary.getErrors().get(2);
        Exception error2 = summary.getErrors().get(3);
        
        assertNotNull(error1);
        assertNotNull(error2);
        assertNotNull(error1.getMessage());
        assertNotNull(error2.getMessage());
    }

    @Test
    public void testImportFromCsvWithEmptyEmailField() throws IOException
    {
        Path csvFile = tempDir.resolve("empty_email.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,,TechCorp,ENGINEER,8500\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
    }

    @Test
    public void testImportFromCsvWithEmptyNameFields() throws IOException
    {
        Path csvFile = tempDir.resolve("empty_names.csv");
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            ",Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n" +
            "Jane,,jane.smith@innovate.com,Innovate,MANAGER,12500\n" +
            "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,INTERN,3500\n";
        Files.writeString(csvFile, csvContent);

        ImportSummary summary = importService.importFromCsv(csvFile.toString());

        assertEquals(1, summary.getSuccessCount());
        assertEquals(2, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
        assertTrue(summary.getErrors().containsKey(3));
    }
}

