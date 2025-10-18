package com.techcorp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ImportServiceTest
{
    private final String NULL_FILE_PATH_EXCEPTION_MESSAGE      = "File path cannot be null";
    private final String EMPTY_FILE_PATH_EXCEPTION_MESSAGE     = "File path cannot be empty";
    private final String FILE_DOES_NOT_EXIST_EXCEPTION_MESSAGE = "File does not exist";

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
    @DisplayName("Should create ImportService with EmployeeService")
    public void shouldCreateImportServiceWithEmployeeService()
    {
        EmployeeService service = new EmployeeService();
        ImportService importer  = new ImportService(service);
        
        assertNotNull(importer);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when EmployeeService is null")
    public void shouldThrowIllegalArgumentExceptionWhenEmployeeServiceIsNull()
    {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ImportService(null)
        );
        
        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    @DisplayName("Should import from CSV with valid data")
    public void shouldImportFromCsvWithValidData() throws IOException
    {
        String csvPath = "valid_employees.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n" +
            "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,INTERN,3500\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(3, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(3, employeeService.getEmployees().size());
    }

    @Test
    @DisplayName("Should import from CSV with role mapping")
    public void shouldImportFromCsvWithRoleMapping() throws IOException
    {
        String csvPath = "role_mapping.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "Jan,Kowalski,jan.kowalski@techcorp.com,TechCorp,PROGRAMISTA,8500\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(1, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(1, employeeService.getEmployees().size());
        
        Employee employee = employeeService.getEmployees().get(0);
        assertEquals(Role.ENGINEER, employee.getRole());
        assertEquals("Jan", employee.getFirstName());
        assertEquals("Kowalski", employee.getLastName());
    }

    @Test
    @DisplayName("Should import from CSV with invalid role")
    public void shouldImportFromCsvWithInvalidRole() throws IOException
    {
        String csvPath = "invalid_role.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,INVALID_ROLE,8500\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
        assertEquals(1, employeeService.getEmployees().size());
        assertEquals("Jane", employeeService.getEmployees().get(0).getFirstName());
    }

    @Test
    @DisplayName("Should import from CSV with negative salary")
    public void shouldImportFromCsvWithNegativeSalary() throws IOException
    {
        String csvPath = "negative_salary.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,-1000\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
        assertEquals(1, employeeService.getEmployees().size());
    }

    @Test
    @DisplayName("Should import from CSV with zero salary")
    public void shouldImportFromCsvWithZeroSalary() throws IOException
    {
        String csvPath = "zero_salary.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,0\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
    }

    @Test
    @DisplayName("Should import from CSV with non-numeric salary")
    public void shouldImportFromCsvWithNonNumericSalary() throws IOException
    {
        String csvPath = "non_numeric_salary.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,invalid\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
    }

    @Test
    @DisplayName("Should import from CSV with missing fields")
    public void shouldImportFromCsvWithMissingFields() throws IOException
    {
        String csvPath = "missing_fields.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
    }

    @Test
    @DisplayName("Should import from CSV with empty lines")
    public void shouldImportFromCsvWithEmptyLines() throws IOException
    {
        String csvPath = "empty_lines.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n" +
            "\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n" +
            "\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(2, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(2, employeeService.getEmployees().size());
    }

    @Test
    @DisplayName("Should import from CSV with only header")
    public void shouldImportFromCsvWithOnlyHeader() throws IOException
    {
        String csvPath = "only_header.csv";
        String csvContent = "firstName,lastName,email,company,position,salary\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(0, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(0, employeeService.getEmployees().size());
    }

    @Test
    @DisplayName("Should import from CSV with empty file")
    public void shouldImportFromCsvWithEmptyFile() throws IOException
    {
        String csvPath = "empty.csv";
        csvPath = writeStringToFile(csvPath, "");

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(0, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(0, employeeService.getEmployees().size());
    }

    @Test
    @DisplayName("Should throw IOException when file does not exist")
    public void shouldThrowIOExceptionWhenFileDoesNotExist()
    {
        String nonExistentPath = "nonexistent.csv";
        IOException exception = assertThrows(
            IOException.class,
            () -> importService.importFromCsv(nonExistentPath)
        );
        assertTrue(exception.getMessage().contains(FILE_DOES_NOT_EXIST_EXCEPTION_MESSAGE));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when file path is null")
    public void shouldThrowIllegalArgumentExceptionWhenFilePathIsNull()
    {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> importService.importFromCsv((String) null)
        );
        
        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when file path is empty")
    public void shouldThrowIllegalArgumentExceptionWhenFilePathIsEmpty()
    {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> importService.importFromCsv("")
        );
        
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    @DisplayName("Should import from CSV with duplicate email")
    public void shouldImportFromCsvWithDuplicateEmail() throws IOException
    {
        String csvPath = "duplicate_email.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n" +
            "Jane,Doe,john.doe@techcorp.com,TechCorp,MANAGER,12500\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(3));
        assertEquals(1, employeeService.getEmployees().size());
    }

    @Test
    @DisplayName("Should import from CSV and continue on errors")
    public void shouldImportFromCsvAndContinueOnErrors() throws IOException
    {
        String csvPath = "mixed_data.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n" +
            "Invalid,Line,invalid@test.com,Test,INVALID,9000\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n" +
            "Bad,Salary,bad@test.com,Test,ENGINEER,-500\n" +
            "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,INTERN,3500\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(3, summary.getSuccessCount());
        assertEquals(2, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(3));
        assertTrue(summary.getErrors().containsKey(5));
        assertEquals(3, employeeService.getEmployees().size());
    }

    @Test
    @DisplayName("Should import from CSV and return error line numbers")
    public void shouldImportFromCsvAndReturnErrorLineNumbers() throws IOException
    {
        String csvPath = "error_lines.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,INVALID_ROLE,8500\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,-100\n" +
            "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,ENGINEER,8500\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(1, summary.getSuccessCount());
        assertEquals(2, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
        assertTrue(summary.getErrors().containsKey(3));
        assertFalse(summary.getErrors().containsKey(4));
    }

    @Test
    @DisplayName("Should import from CSV with all roles")
    public void shouldImportFromCsvWithAllRoles() throws IOException
    {
        String csvPath = "all_roles.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Ceo,john.ceo@techcorp.com,TechCorp,CEO,25000\n" +
            "Jane,Vp,jane.vp@techcorp.com,TechCorp,VP,18000\n" +
            "Bob,Manager,bob.manager@techcorp.com,TechCorp,MANAGER,12000\n" +
            "Alice,Engineer,alice.engineer@techcorp.com,TechCorp,ENGINEER,8000\n" +
            "Tom,Intern,tom.intern@techcorp.com,TechCorp,INTERN,3000\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

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
    @DisplayName("Should import from CSV with whitespace in fields")
    public void shouldImportFromCsvWithWhitespaceInFields() throws IOException
    {
        String csvPath = "whitespace_fields.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            " John , Doe , john.doe@techcorp.com , TechCorp , ENGINEER , 8500 \n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

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
    @DisplayName("Should import from CSV with extra fields")
    public void shouldImportFromCsvWithExtraFields() throws IOException
    {
        String csvPath = "extra_fields.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,extra,fields\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(1, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(1, employeeService.getEmployees().size());
    }

    @Test
    @DisplayName("Should import from CSV and add to existing employees")
    public void shouldImportFromCsvAndAddToExistingEmployees() throws IOException
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
    @DisplayName("Should import from CSV with case insensitive roles")
    public void shouldImportFromCsvWithCaseInsensitiveRoles() throws IOException
    {
        String csvPath = "case_insensitive_roles.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,engineer,8500\n" +
            "Jane,Smith,jane.smith@techcorp.com,TechCorp,MANAGER,12500\n" +
            "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,Manager,12000\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(3, summary.getSuccessCount());
        assertEquals(0, summary.getErrors().size());
        assertEquals(3, employeeService.getEmployees().size());
    }

    @Test
    @DisplayName("Should import from CSV and return correct error exceptions")
    public void shouldImportFromCsvAndReturnCorrectErrorExceptions() throws IOException
    {
        String csvPath = "error_types.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,INVALID_ROLE,8500\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,-100\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

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
    @DisplayName("Should import from CSV with empty email field")
    public void shouldImportFromCsvWithEmptyEmailField() throws IOException
    {
        String csvPath = "empty_email.csv";
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,,TechCorp,ENGINEER,8500\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        csvPath = writeStringToFile(csvPath, csvContent);

        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
    }

    @Test
    @DisplayName("Should import from CSV with empty name fields")
    public void shouldImportFromCsvWithEmptyNameFields() throws IOException
    {
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            ",Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n" +
            "Jane,,jane.smith@innovate.com,Innovate,MANAGER,12500\n" +
            "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,INTERN,3500\n";
        String csvPath = "empty_names.csv";
        csvPath = writeStringToFile(csvPath, csvContent);
        
        ImportSummary summary = importService.importFromCsv(csvPath);

        assertEquals(1, summary.getSuccessCount());
        assertEquals(2, summary.getErrors().size());
        assertTrue(summary.getErrors().containsKey(2));
        assertTrue(summary.getErrors().containsKey(3));
    }

    private String writeStringToFile(String filePath, String content) throws IOException {
        Path file = tempDir.resolve(filePath);
        Files.writeString(file, content);
        return tempDir.resolve(filePath).toString();
    }
}
