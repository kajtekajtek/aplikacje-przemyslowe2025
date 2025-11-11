package com.techcorp.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import com.techcorp.ImportService;
import com.techcorp.ImportSummary;
import com.techcorp.FileStorageService;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final ImportService importService;
    private final FileStorageService fileStorageService;

    public FileUploadController(ImportService importService, FileStorageService fileStorageService) {
        this.importService = importService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping({"/import/csv", "/import/xml"})
    public ResponseEntity<ImportSummary> uploadFile(
        @RequestParam("file") MultipartFile file
    ) {
        String filePath = fileStorageService.saveFile(file);
        ImportSummary summary = importService.importFromFile(filePath);
        return ResponseEntity.ok(summary);
    }

}