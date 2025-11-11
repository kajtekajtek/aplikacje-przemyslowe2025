package com.techcorp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import com.techcorp.ImportSummary;
import com.techcorp.service.FileStorageService;
import com.techcorp.service.ImportService;
import com.techcorp.service.RaportGeneratorService;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final ImportService importService;
    private final FileStorageService fileStorageService;
    private final RaportGeneratorService raportGeneratorService;
    public FileUploadController(
        ImportService importService, 
        FileStorageService fileStorageService,
        RaportGeneratorService raportGeneratorService
    ) {
        this.importService = importService;
        this.fileStorageService = fileStorageService;
        this.raportGeneratorService = raportGeneratorService;
    }

    @PostMapping({"/import/csv", "/import/xml"})
    public ResponseEntity<ImportSummary> uploadFile(
        @RequestParam("file") MultipartFile file
    ) {
        String filePath = fileStorageService.saveFile(file);
        ImportSummary summary = importService.importFromFile(filePath);
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

}