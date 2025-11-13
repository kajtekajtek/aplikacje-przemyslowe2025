package com.techcorp.service;

import com.techcorp.model.DocumentType;
import com.techcorp.model.EmployeeDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DocumentService {

    private static final String UPLOAD_DIR = "uploads/documents";
    private final Map<String, List<EmployeeDocument>> documentsStore;
    private final FileStorageService fileStorageService;

    public DocumentService(FileStorageService fileStorageService) {
        this.documentsStore = new ConcurrentHashMap<>();
        this.fileStorageService = fileStorageService;
    }

    public EmployeeDocument saveDocument(String email, MultipartFile file, DocumentType type) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Employee email cannot be null or empty");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Document type cannot be null");
        }

        String normalizedEmail = email.toLowerCase();
        String employeeDir = UPLOAD_DIR + "/" + normalizedEmail;
        
        String filePath = fileStorageService.saveFile(file, employeeDir);
        
        String fileName = Paths.get(filePath).getFileName().toString();

        EmployeeDocument document = new EmployeeDocument(
            normalizedEmail,
            fileName,
            file.getOriginalFilename(),
            type,
            filePath
        );

        documentsStore.computeIfAbsent(normalizedEmail, k -> new ArrayList<>()).add(document);

        return document;
    }

    public List<EmployeeDocument> getDocuments(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Employee email cannot be null or empty");
        }

        String normalizedEmail = email.toLowerCase();
        return new ArrayList<>(documentsStore.getOrDefault(normalizedEmail, Collections.emptyList()));
    }

    public Optional<EmployeeDocument> getDocument(String email, String documentId) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Employee email cannot be null or empty");
        }
        if (documentId == null || documentId.isEmpty()) {
            throw new IllegalArgumentException("Document ID cannot be null or empty");
        }

        String normalizedEmail = email.toLowerCase();
        List<EmployeeDocument> documents = documentsStore.getOrDefault(normalizedEmail, Collections.emptyList());
        
        return documents.stream()
            .filter(doc -> doc.getId().equals(documentId))
            .findFirst();
    }

    public void deleteDocument(String email, String documentId) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Employee email cannot be null or empty");
        }
        if (documentId == null || documentId.isEmpty()) {
            throw new IllegalArgumentException("Document ID cannot be null or empty");
        }

        String normalizedEmail = email.toLowerCase();
        Optional<EmployeeDocument> documentOpt = getDocument(normalizedEmail, documentId);
        
        if (documentOpt.isEmpty()) {
            throw new IllegalArgumentException("Document not found");
        }

        EmployeeDocument document = documentOpt.get();
        
        fileStorageService.deleteFile(document.getFileName());

        List<EmployeeDocument> documents = documentsStore.get(normalizedEmail);
        if (documents != null) {
            documents.removeIf(doc -> doc.getId().equals(documentId));
        }
    }

    protected void clearAll() {
        documentsStore.clear();
    }
}

