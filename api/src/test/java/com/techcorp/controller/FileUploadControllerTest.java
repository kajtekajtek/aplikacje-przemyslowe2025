package com.techcorp.controller;

import com.techcorp.FileStorageService;
import com.techcorp.ImportService;
import com.techcorp.ImportSummary;
import com.techcorp.exception.GlobalExceptionHandler;
import com.techcorp.exception.InvalidDataException;
import com.techcorp.exception.FileStorageException;
import org.junit.jupiter.api.BeforeEach;
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
            .thenThrow(new com.techcorp.exception.FileNotFoundException("File does not exist"));

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
}

