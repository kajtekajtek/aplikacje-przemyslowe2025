package com.techcorp.controller;

import com.techcorp.model.ImportSummary;
import com.techcorp.exception.GlobalExceptionHandler;
import com.techcorp.model.exception.InvalidDataException;
import com.techcorp.model.exception.FileStorageException;
import com.techcorp.model.exception.InvalidDataException;
import com.techcorp.service.FileStorageService;
import com.techcorp.service.ImportService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FileUploadController.class)
@ContextConfiguration(classes = {FileUploadController.class, GlobalExceptionHandler.class})
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImportService importService;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private com.techcorp.service.RaportGeneratorService raportGeneratorService;

    private MockMultipartFile validCsvFile;
    private MockMultipartFile validXmlFile;
    private ImportSummary successSummary;
    private ImportSummary summaryWithErrors;

    @BeforeEach
    void setUp() {
        // Valid CSV file
        String csvContent = 
            "firstName,lastName,email,company,position,salary\n" +
            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500\n" +
            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500\n";
        validCsvFile = new MockMultipartFile(
            "file",
            "employees.csv",
            "text/csv",
            csvContent.getBytes()
        );

        // Valid XML file
        String xmlContent = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<employees>\n" +
            "    <employee>\n" +
            "        <firstName>John</firstName>\n" +
            "        <lastName>Doe</lastName>\n" +
            "        <email>john.doe@techcorp.com</email>\n" +
            "        <company>TechCorp</company>\n" +
            "        <position>ENGINEER</position>\n" +
            "        <salary>8500</salary>\n" +
            "    </employee>\n" +
            "</employees>";
        validXmlFile = new MockMultipartFile(
            "file",
            "employees.xml",
            "application/xml",
            xmlContent.getBytes()
        );

        // Success summary
        successSummary = new ImportSummary();
        successSummary.addSuccessfullImport();
        successSummary.addSuccessfullImport();

        // Summary with errors
        summaryWithErrors = new ImportSummary();
        summaryWithErrors.addSuccessfullImport();
        summaryWithErrors.addError(2, new InvalidDataException(2, "Invalid role"));
    }

    @Test
    void uploadCsvFile_WithValidFile_ShouldReturn200AndImportSummary() throws Exception {
        when(fileStorageService.saveFile(any())).thenReturn("/tmp/employees.csv");
        when(importService.importFromFile(anyString())).thenReturn(successSummary);

        mockMvc.perform(multipart("/api/files/import/csv")
                .file(validCsvFile))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(2))
            .andExpect(jsonPath("$.errors").isEmpty());

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, times(1)).importFromFile(anyString());
    }

    @Test
    void uploadXmlFile_WithValidFile_ShouldReturn200AndImportSummary() throws Exception {
        when(fileStorageService.saveFile(any())).thenReturn("/tmp/employees.xml");
        when(importService.importFromFile(anyString())).thenReturn(successSummary);

        mockMvc.perform(multipart("/api/files/import/xml")
                .file(validXmlFile))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(2))
            .andExpect(jsonPath("$.errors").isEmpty());

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, times(1)).importFromFile(anyString());
    }

    @Test
    void uploadFile_WithErrors_ShouldReturn200AndSummaryWithErrors() throws Exception {
        when(fileStorageService.saveFile(any())).thenReturn("/tmp/employees.csv");
        when(importService.importFromFile(anyString())).thenReturn(summaryWithErrors);

        mockMvc.perform(multipart("/api/files/import/csv")
                .file(validCsvFile))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(1))
            .andExpect(jsonPath("$.errors").isNotEmpty())
            .andExpect(jsonPath("$.errors.2").exists());

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, times(1)).importFromFile(anyString());
    }

    @Test
    void uploadFile_WhenFileStorageFails_ShouldReturn500() throws Exception {
        when(fileStorageService.saveFile(any()))
            .thenThrow(new FileStorageException("Failed to store file"));

        mockMvc.perform(multipart("/api/files/import/csv")
                .file(validCsvFile))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("Failed to store file"))
            .andExpect(jsonPath("$.status").value(500));

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, never()).importFromFile(anyString());
    }

    @Test
    void uploadFile_WhenImportFails_ShouldReturn500() throws Exception {
        when(fileStorageService.saveFile(any())).thenReturn("/tmp/employees.csv");
        when(importService.importFromFile(anyString()))
            .thenThrow(new RuntimeException("Failed to parse file"));

        mockMvc.perform(multipart("/api/files/import/csv")
                .file(validCsvFile))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred: Failed to parse file"))
            .andExpect(jsonPath("$.status").value(500));

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, times(1)).importFromFile(anyString());
    }

    @Test
    void uploadFile_WithEmptyFile_ShouldReturn400() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.csv",
            "text/csv",
            new byte[0]
        );

        when(fileStorageService.saveFile(any()))
            .thenThrow(new IllegalArgumentException("File is empty"));

        mockMvc.perform(multipart("/api/files/import/csv")
                .file(emptyFile))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("File is empty"))
            .andExpect(jsonPath("$.status").value(400));

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, never()).importFromFile(anyString());
    }

    @Test
    void uploadFile_WithUnsupportedFormat_ShouldReturn400() throws Exception {
        MockMultipartFile unsupportedFile = new MockMultipartFile(
            "file",
            "employees.json",
            "application/json",
            "{\"employees\": []}".getBytes()
        );

        when(fileStorageService.saveFile(any())).thenReturn("/tmp/employees.json");
        when(importService.importFromFile(anyString()))
            .thenThrow(new IllegalArgumentException("Unsupported file format: json"));

        mockMvc.perform(multipart("/api/files/import/csv")
                .file(unsupportedFile))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Unsupported file format: json"))
            .andExpect(jsonPath("$.status").value(400));

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, times(1)).importFromFile(anyString());
    }

    @Test
    void uploadFile_WithMalformedCsv_ShouldReturnSummaryWithErrors() throws Exception {
        ImportSummary errorSummary = new ImportSummary();
        errorSummary.addError(1, new InvalidDataException(1, "Invalid number of fields"));
        errorSummary.addError(2, new InvalidDataException(2, "Invalid role"));

        when(fileStorageService.saveFile(any())).thenReturn("/tmp/malformed.csv");
        when(importService.importFromFile(anyString())).thenReturn(errorSummary);

        mockMvc.perform(multipart("/api/files/import/csv")
                .file(validCsvFile))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(0))
            .andExpect(jsonPath("$.errors").isNotEmpty())
            .andExpect(jsonPath("$.errors.1").exists())
            .andExpect(jsonPath("$.errors.2").exists());

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, times(1)).importFromFile(anyString());
    }

    @Test
    void uploadFile_WithPartialSuccess_ShouldReturn200WithMixedResults() throws Exception {
        ImportSummary mixedSummary = new ImportSummary();
        mixedSummary.addSuccessfullImport();
        mixedSummary.addSuccessfullImport();
        mixedSummary.addSuccessfullImport();
        mixedSummary.addError(2, new InvalidDataException(2, "Invalid salary"));
        mixedSummary.addError(4, new InvalidDataException(4, "Duplicate email"));

        when(fileStorageService.saveFile(any())).thenReturn("/tmp/employees.csv");
        when(importService.importFromFile(anyString())).thenReturn(mixedSummary);

        mockMvc.perform(multipart("/api/files/import/csv")
                .file(validCsvFile))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(3))
            .andExpect(jsonPath("$.errors").isNotEmpty())
            .andExpect(jsonPath("$.errors.2").exists())
            .andExpect(jsonPath("$.errors.4").exists());

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, times(1)).importFromFile(anyString());
    }

    @Test
    void uploadFile_WhenFileNotFound_ShouldReturn404() throws Exception {
        when(fileStorageService.saveFile(any())).thenReturn("/tmp/nonexistent.csv");
        when(importService.importFromFile(anyString()))
            .thenThrow(new com.techcorp.model.exception.FileNotFoundException("File does not exist"));

        mockMvc.perform(multipart("/api/files/import/csv")
                .file(validCsvFile))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("File does not exist"))
            .andExpect(jsonPath("$.status").value(404));

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, times(1)).importFromFile(anyString());
    }

    @Test
    void uploadCsvFile_WithLargeFile_ShouldReturn200() throws Exception {
        StringBuilder largeContent = new StringBuilder();
        largeContent.append("firstName,lastName,email,company,position,salary\n");
        for (int i = 0; i < 100; i++) {
            largeContent.append(String.format("John%d,Doe%d,john.doe%d@techcorp.com,TechCorp,ENGINEER,8500\n", i, i, i));
        }

        MockMultipartFile largeFile = new MockMultipartFile(
            "file",
            "large_employees.csv",
            "text/csv",
            largeContent.toString().getBytes()
        );

        ImportSummary largeSummary = new ImportSummary();
        for (int i = 0; i < 100; i++) {
            largeSummary.addSuccessfullImport();
        }

        when(fileStorageService.saveFile(any())).thenReturn("/tmp/large_employees.csv");
        when(importService.importFromFile(anyString())).thenReturn(largeSummary);

        mockMvc.perform(multipart("/api/files/import/csv")
                .file(largeFile))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(100))
            .andExpect(jsonPath("$.errors").isEmpty());

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, times(1)).importFromFile(anyString());
    }

    @Test
    void uploadXmlFile_WithComplexXml_ShouldReturn200() throws Exception {
        String complexXmlContent = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<employees>\n" +
            "    <employee>\n" +
            "        <firstName>John</firstName>\n" +
            "        <lastName>Doe</lastName>\n" +
            "        <email>john.doe@techcorp.com</email>\n" +
            "        <company>TechCorp</company>\n" +
            "        <position>CEO</position>\n" +
            "        <salary>25000</salary>\n" +
            "    </employee>\n" +
            "    <employee>\n" +
            "        <firstName>Jane</firstName>\n" +
            "        <lastName>Smith</lastName>\n" +
            "        <email>jane.smith@techcorp.com</email>\n" +
            "        <company>TechCorp</company>\n" +
            "        <position>VP</position>\n" +
            "        <salary>18000</salary>\n" +
            "    </employee>\n" +
            "</employees>";

        MockMultipartFile complexXmlFile = new MockMultipartFile(
            "file",
            "employees.xml",
            "application/xml",
            complexXmlContent.getBytes()
        );

        when(fileStorageService.saveFile(any())).thenReturn("/tmp/employees.xml");
        when(importService.importFromFile(anyString())).thenReturn(successSummary);

        mockMvc.perform(multipart("/api/files/import/xml")
                .file(complexXmlFile))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(2))
            .andExpect(jsonPath("$.errors").isEmpty());

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, times(1)).importFromFile(anyString());
    }

    @Test
    void uploadFile_WithSpecialCharacters_ShouldReturn200() throws Exception {
        String csvWithSpecialChars = 
            "firstName,lastName,email,company,position,salary\n" +
            "Jan,Kowalski,jan.kowalski@techcorp.com,TechCorp,PROGRAMISTA,8500\n" +
            "María,García,maria.garcia@techcorp.com,TechCorp,ENGINEER,9000\n";

        MockMultipartFile specialCharsFile = new MockMultipartFile(
            "file",
            "employees_special.csv",
            "text/csv",
            csvWithSpecialChars.getBytes("UTF-8")
        );

        when(fileStorageService.saveFile(any())).thenReturn("/tmp/employees_special.csv");
        when(importService.importFromFile(anyString())).thenReturn(successSummary);

        mockMvc.perform(multipart("/api/files/import/csv")
                .file(specialCharsFile))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(2));

        verify(fileStorageService, times(1)).saveFile(any());
        verify(importService, times(1)).importFromFile(anyString());
    }

    @Test
    @DisplayName("Should export CSV file with correct headers and content type")
    public void shouldExportCsvFileWithCorrectHeadersAndContentType() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport()).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "text/csv"))
            .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"employees.csv\""))
            .andExpect(header().exists("Content-Length"))
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV file with employee data")
    public void shouldExportCsvFileWithEmployeeData() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,ACTIVE\n" +
                           "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport()).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv"))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV file with empty employee list")
    public void shouldExportCsvFileWithEmptyEmployeeList() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n";
        
        when(raportGeneratorService.generateCsvReport()).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv"))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV file with correct content length")
    public void shouldExportCsvFileWithCorrectContentLength() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport()).thenReturn(csvContent);

        int expectedLength = csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;

        mockMvc.perform(get("/api/files/export/csv"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Length", String.valueOf(expectedLength)));

        verify(raportGeneratorService, times(1)).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV file with multiple employees")
    public void shouldExportCsvFileWithMultipleEmployees() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,ACTIVE\n" +
                           "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500,ACTIVE\n" +
                           "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,INTERN,3500,ON_LEAVE\n";
        
        when(raportGeneratorService.generateCsvReport()).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv"))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV file with Polish characters")
    public void shouldExportCsvFileWithPolishCharacters() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "Jan,Kowalski,jan.kowalski@techcorp.com,TechCorp,ENGINEER,8500,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport()).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv"))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV file as attachment")
    public void shouldExportCsvFileAsAttachment() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n";
        
        when(raportGeneratorService.generateCsvReport()).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", 
                org.hamcrest.Matchers.containsString("attachment")))
            .andExpect(header().string("Content-Disposition", 
                org.hamcrest.Matchers.containsString("employees.csv")));

        verify(raportGeneratorService, times(1)).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV file with all role types")
    public void shouldExportCsvFileWithAllRoleTypes() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "John,Ceo,john.ceo@techcorp.com,TechCorp,CEO,25000,ACTIVE\n" +
                           "Jane,Vp,jane.vp@techcorp.com,TechCorp,VP,18000,ACTIVE\n" +
                           "Bob,Manager,bob.manager@techcorp.com,TechCorp,MANAGER,12000,ACTIVE\n" +
                           "Alice,Engineer,alice.engineer@techcorp.com,TechCorp,ENGINEER,8000,ACTIVE\n" +
                           "Tom,Intern,tom.intern@techcorp.com,TechCorp,INTERN,3000,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport()).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv"))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV file when raport generator throws exception")
    public void shouldHandleExceptionWhenRaportGeneratorFails() throws Exception {
        when(raportGeneratorService.generateCsvReport())
            .thenThrow(new RuntimeException("Failed to generate report"));

        mockMvc.perform(get("/api/files/export/csv"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred: Failed to generate report"))
            .andExpect(jsonPath("$.status").value(500));

        verify(raportGeneratorService, times(1)).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV file with large dataset")
    public void shouldExportCsvFileWithLargeDataset() throws Exception {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("firstName,lastName,email,company,position,salary,status\n");
        for (int i = 0; i < 50; i++) {
            csvBuilder.append("Employee").append(i).append(",");
            csvBuilder.append("LastName").append(i).append(",");
            csvBuilder.append("employee").append(i).append("@techcorp.com,");
            csvBuilder.append("TechCorp,ENGINEER,8000,ACTIVE\n");
        }
        String csvContent = csvBuilder.toString();
        
        when(raportGeneratorService.generateCsvReport()).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv"))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV filtered by company name")
    public void shouldExportCsvFilteredByCompanyName() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,ACTIVE\n" +
                           "Bob,Johnson,bob.johnson@techcorp.com,TechCorp,INTERN,3500,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport("TechCorp")).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv")
                .param("companyName", "TechCorp"))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport("TechCorp");
        verify(raportGeneratorService, never()).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV for all employees when company name not provided")
    public void shouldExportCsvForAllEmployeesWhenCompanyNameNotProvided() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,ACTIVE\n" +
                           "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport()).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv"))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport();
    }

    @Test
    @DisplayName("Should export CSV with only header when company has no employees")
    public void shouldExportCsvWithOnlyHeaderWhenCompanyHasNoEmployees() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n";
        
        when(raportGeneratorService.generateCsvReport("EmptyCompany")).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv")
                .param("companyName", "EmptyCompany"))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport("EmptyCompany");
    }

    @Test
    @DisplayName("Should export CSV for specific company with correct headers")
    public void shouldExportCsvForSpecificCompanyWithCorrectHeaders() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "Alice,Smith,alice@innovate.com,Innovate,ENGINEER,9000,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport("Innovate")).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv")
                .param("companyName", "Innovate"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "text/csv"))
            .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"employees.csv\""))
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport("Innovate");
    }

    @Test
    @DisplayName("Should export CSV for company with multiple employees and roles")
    public void shouldExportCsvForCompanyWithMultipleEmployeesAndRoles() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "Big,Boss,big.boss@startup.com,StartUp Inc,CEO,25000,ACTIVE\n" +
                           "John,Dev,john.dev@startup.com,StartUp Inc,ENGINEER,8000,ACTIVE\n" +
                           "Bob,Junior,bob.junior@startup.com,StartUp Inc,INTERN,3000,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport("StartUp Inc")).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv")
                .param("companyName", "StartUp Inc"))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport("StartUp Inc");
    }

    @Test
    @DisplayName("Should export CSV for different companies with same endpoint")
    public void shouldExportCsvForDifferentCompaniesWithSameEndpoint() throws Exception {
        String techCorpCsv = "firstName,lastName,email,company,position,salary,status\n" +
                            "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,ACTIVE\n";
        String innovateCsv = "firstName,lastName,email,company,position,salary,status\n" +
                            "Jane,Smith,jane.smith@innovate.com,Innovate,MANAGER,12500,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport("TechCorp")).thenReturn(techCorpCsv);
        when(raportGeneratorService.generateCsvReport("Innovate")).thenReturn(innovateCsv);

        // First request for TechCorp
        mockMvc.perform(get("/api/files/export/csv")
                .param("companyName", "TechCorp"))
            .andExpect(status().isOk())
            .andExpect(content().string(techCorpCsv));

        // Second request for Innovate
        mockMvc.perform(get("/api/files/export/csv")
                .param("companyName", "Innovate"))
            .andExpect(status().isOk())
            .andExpect(content().string(innovateCsv));

        verify(raportGeneratorService, times(1)).generateCsvReport("TechCorp");
        verify(raportGeneratorService, times(1)).generateCsvReport("Innovate");
    }

    @Test
    @DisplayName("Should export CSV with company name containing spaces")
    public void shouldExportCsvWithCompanyNameContainingSpaces() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "John,Doe,john@company.com,Tech Corp Inc,ENGINEER,8500,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport("Tech Corp Inc")).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv")
                .param("companyName", "Tech Corp Inc"))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport("Tech Corp Inc");
    }

    @Test
    @DisplayName("Should export CSV when company name parameter is empty string")
    public void shouldExportCsvWhenCompanyNameParameterIsEmptyString() throws Exception {
        String csvContent = "firstName,lastName,email,company,position,salary,status\n" +
                           "John,Doe,john.doe@techcorp.com,TechCorp,ENGINEER,8500,ACTIVE\n";
        
        when(raportGeneratorService.generateCsvReport("")).thenReturn(csvContent);

        mockMvc.perform(get("/api/files/export/csv")
                .param("companyName", ""))
            .andExpect(status().isOk())
            .andExpect(content().string(csvContent));

        verify(raportGeneratorService, times(1)).generateCsvReport("");
    }

    // PDF Statistics Report Tests

    @Test
    @DisplayName("Should get statistics PDF report with correct headers")
    public void shouldGetStatisticsPdfReportWithCorrectHeaders() throws Exception {
        byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46}; // %PDF
        when(raportGeneratorService.generatePdfReport("TechCorp")).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/files/reports/statistics/TechCorp"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "application/pdf"))
            .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"statistics_TechCorp.pdf\""))
            .andExpect(header().string("Content-Length", String.valueOf(pdfBytes.length)));

        verify(raportGeneratorService, times(1)).generatePdfReport("TechCorp");
    }

    @Test
    @DisplayName("Should get statistics PDF report for specific company")
    public void shouldGetStatisticsPdfReportForSpecificCompany() throws Exception {
        byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E, 0x34}; // %PDF-1.4
        when(raportGeneratorService.generatePdfReport("Innovate")).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/files/reports/statistics/Innovate"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andExpect(content().bytes(pdfBytes));

        verify(raportGeneratorService, times(1)).generatePdfReport("Innovate");
    }

    @Test
    @DisplayName("Should get statistics PDF with correct filename pattern")
    public void shouldGetStatisticsPdfWithCorrectFilenamePattern() throws Exception {
        byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46};
        when(raportGeneratorService.generatePdfReport("StartUp Inc")).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/files/reports/statistics/StartUp Inc"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", 
                org.hamcrest.Matchers.containsString("statistics_StartUp Inc.pdf")));

        verify(raportGeneratorService, times(1)).generatePdfReport("StartUp Inc");
    }

    @Test
    @DisplayName("Should get statistics PDF for multiple different companies")
    public void shouldGetStatisticsPdfForMultipleDifferentCompanies() throws Exception {
        byte[] techCorpPdf = new byte[]{0x25, 0x50, 0x44, 0x46, 0x01};
        byte[] innovatePdf = new byte[]{0x25, 0x50, 0x44, 0x46, 0x02};
        
        when(raportGeneratorService.generatePdfReport("TechCorp")).thenReturn(techCorpPdf);
        when(raportGeneratorService.generatePdfReport("Innovate")).thenReturn(innovatePdf);

        mockMvc.perform(get("/api/files/reports/statistics/TechCorp"))
            .andExpect(status().isOk())
            .andExpect(content().bytes(techCorpPdf));

        mockMvc.perform(get("/api/files/reports/statistics/Innovate"))
            .andExpect(status().isOk())
            .andExpect(content().bytes(innovatePdf));

        verify(raportGeneratorService, times(1)).generatePdfReport("TechCorp");
        verify(raportGeneratorService, times(1)).generatePdfReport("Innovate");
    }

    @Test
    @DisplayName("Should get statistics PDF as attachment")
    public void shouldGetStatisticsPdfAsAttachment() throws Exception {
        byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46};
        when(raportGeneratorService.generatePdfReport("Company")).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/files/reports/statistics/Company"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", 
                org.hamcrest.Matchers.containsString("attachment")));

        verify(raportGeneratorService, times(1)).generatePdfReport("Company");
    }

    @Test
    @DisplayName("Should handle exception when generating PDF statistics")
    public void shouldHandleExceptionWhenGeneratingPdfStatistics() throws Exception {
        when(raportGeneratorService.generatePdfReport("FailCompany"))
            .thenThrow(new RuntimeException("Failed to generate PDF"));

        mockMvc.perform(get("/api/files/reports/statistics/FailCompany"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred: Failed to generate PDF"))
            .andExpect(jsonPath("$.status").value(500));

        verify(raportGeneratorService, times(1)).generatePdfReport("FailCompany");
    }

    @Test
    @DisplayName("Should get statistics PDF with company name containing spaces")
    public void shouldGetStatisticsPdfWithCompanyNameContainingSpaces() throws Exception {
        byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46};
        when(raportGeneratorService.generatePdfReport("Tech Corp")).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/files/reports/statistics/Tech Corp"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", 
                org.hamcrest.Matchers.containsString("statistics_Tech Corp.pdf")));

        verify(raportGeneratorService, times(1)).generatePdfReport("Tech Corp");
    }

    @Test
    @DisplayName("Should get statistics PDF with company name containing special chars")
    public void shouldGetStatisticsPdfWithCompanyNameContainingSpecialChars() throws Exception {
        byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46};
        when(raportGeneratorService.generatePdfReport("Tech-Corp & Co.")).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/files/reports/statistics/Tech-Corp & Co."))
            .andExpect(status().isOk())
            .andExpect(content().bytes(pdfBytes));

        verify(raportGeneratorService, times(1)).generatePdfReport("Tech-Corp & Co.");
    }

    @Test
    @DisplayName("Should get statistics PDF with correct content length")
    public void shouldGetStatisticsPdfWithCorrectContentLength() throws Exception {
        byte[] pdfBytes = new byte[1024]; // 1KB PDF
        pdfBytes[0] = 0x25; // %
        pdfBytes[1] = 0x50; // P
        pdfBytes[2] = 0x44; // D
        pdfBytes[3] = 0x46; // F
        
        when(raportGeneratorService.generatePdfReport("BigCompany")).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/files/reports/statistics/BigCompany"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Length", "1024"));

        verify(raportGeneratorService, times(1)).generatePdfReport("BigCompany");
    }

    @Test
    @DisplayName("Should get statistics PDF for company with simple name")
    public void shouldGetStatisticsPdfForCompanyWithSimpleName() throws Exception {
        byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46};
        when(raportGeneratorService.generatePdfReport("ABC")).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/files/reports/statistics/ABC"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", 
                org.hamcrest.Matchers.containsString("statistics_ABC.pdf")));

        verify(raportGeneratorService, times(1)).generatePdfReport("ABC");
    }
}

