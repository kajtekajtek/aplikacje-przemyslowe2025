package com.techcorp.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;

import com.techcorp.model.DocumentType;
import com.techcorp.model.EmployeeDocument;
import com.techcorp.model.exception.EmployeeNotFoundException;
import com.techcorp.model.ImportSummary;
import com.techcorp.model.dto.EmployeeDTO;
import com.techcorp.mapper.EmployeeMapper;
import com.techcorp.service.DocumentService;
import com.techcorp.service.FileStorageService;
import com.techcorp.service.ImportService;
import com.techcorp.service.PhotoService;
import com.techcorp.service.RaportGeneratorService;
import com.techcorp.service.EmployeeService;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final ImportService importService;
    private final FileStorageService fileStorageService;
    private final RaportGeneratorService raportGeneratorService;
    private final DocumentService documentService;
    private final PhotoService photoService;
    private final EmployeeService employeeService;
    
    public FileUploadController(
        ImportService importService, 
        FileStorageService fileStorageService,
        RaportGeneratorService raportGeneratorService,
        DocumentService documentService,
        PhotoService photoService,
        EmployeeService employeeService
    ) {
        this.importService = importService;
        this.fileStorageService = fileStorageService;
        this.raportGeneratorService = raportGeneratorService;
        this.documentService = documentService;
        this.photoService = photoService;
        this.employeeService = employeeService;
    }

    @PostMapping(value = {"/import/csv", "/import/xml"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImportSummary> uploadFile(
        @RequestParam("file") MultipartFile file
    ) {
        String filename = fileStorageService.saveFile(file);
        String fullPath = fileStorageService.getFullPath(filename);
        ImportSummary summary = importService.importFromFile(fullPath);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportCsv(
        @RequestParam(required = false) String companyName
    ) {
        String csv = companyName != null 
            ? raportGeneratorService.generateCsvReport(companyName) 
            : raportGeneratorService.generateCsvReport();
        
        byte[] csvBytes = csv.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(csvBytes);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "employees.csv");
        headers.setContentLength(csvBytes.length);
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(resource);
    }

    @GetMapping("/reports/statistics/{companyName}")
    public ResponseEntity<Resource> getStatisticsReport(
        @PathVariable String companyName
    ) {
        byte[] pdfBytes = raportGeneratorService.generatePdfReport(companyName);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "statistics_" + companyName + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(resource);
    }

    @PostMapping(value = "/documents/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeDocument> uploadDocument(
        @PathVariable String email,
        @RequestParam("file") MultipartFile file,
        @RequestParam("type") String type
    ) {
        DocumentType documentType = DocumentType.valueOf(type.toUpperCase());
        EmployeeDocument document = documentService.saveDocument(email, file, documentType);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(document);
    }

    @GetMapping("/documents/{email}")
    public ResponseEntity<List<EmployeeDocument>> getEmployeeDocuments(
        @PathVariable String email
    ) {
        List<EmployeeDocument> documents = documentService.getDocuments(email);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/documents/{email}/{documentId}")
    public ResponseEntity<Resource> getDocument(
        @PathVariable String email,
        @PathVariable String documentId
    ) {
        Optional<EmployeeDocument> documentOpt = documentService.getDocument(email, documentId);
        
        if (documentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        EmployeeDocument document = documentOpt.get();
        File file = new File(document.getFilePath());
        
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        FileSystemResource resource = new FileSystemResource(file);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", document.getOriginalFileName());
        headers.setContentLength(file.length());
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(resource);
    }

    @DeleteMapping("/documents/{email}/{documentId}")
    public ResponseEntity<Void> deleteDocument(
        @PathVariable String email,
        @PathVariable String documentId
    ) {
        documentService.deleteDocument(email, documentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/photos/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeDTO> uploadPhoto(
        @PathVariable String email,
        @RequestParam("file") MultipartFile file
    ) {
        photoService.savePhoto(email, file);
        
        EmployeeDTO employeeDTO = employeeService.getEmployeeByEmail(email)
            .map(EmployeeMapper::entityToDTO)
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + email + " not found"
            ));
        
        return ResponseEntity.ok(employeeDTO);
    }

    @GetMapping("/photos/{email}")
    public ResponseEntity<Resource> getPhoto(
        @PathVariable String email
    ) {
        Resource resource = photoService.loadPhoto(email);
        
        String filename = employeeService.getEmployeeByEmail(email)
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + email + " not found"
            ))
            .getPhotoFileName();
        
        String contentType = photoService.getContentType(filename);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("inline", filename);
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(resource);
    }

    @DeleteMapping("/photos/{email}")
    public ResponseEntity<Void> deletePhoto(
        @PathVariable String email
    ) {
        photoService.deletePhoto(email);
        return ResponseEntity.noContent().build();
    }

}