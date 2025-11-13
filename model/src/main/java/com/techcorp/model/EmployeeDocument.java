package com.techcorp.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class EmployeeDocument {
    
    private final String id;
    private final String employeeEmail;
    private final String fileName;
    private final String originalFileName;
    private final DocumentType fileType;
    private final LocalDateTime uploadDate;
    private final String filePath;

    public EmployeeDocument(
        String employeeEmail,
        String fileName,
        String originalFileName,
        DocumentType fileType,
        String filePath
    ) {
        if (employeeEmail == null || employeeEmail.isEmpty()) {
            throw new IllegalArgumentException("Employee email cannot be null or empty");
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("Original file name cannot be null or empty");
        }
        if (fileType == null) {
            throw new IllegalArgumentException("File type cannot be null");
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        this.id = UUID.randomUUID().toString();
        this.employeeEmail = employeeEmail.toLowerCase();
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileType = fileType;
        this.uploadDate = LocalDateTime.now();
        this.filePath = filePath;
    }

    public EmployeeDocument(
        String id,
        String employeeEmail,
        String fileName,
        String originalFileName,
        DocumentType fileType,
        LocalDateTime uploadDate,
        String filePath
    ) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Document ID cannot be null or empty");
        }
        if (employeeEmail == null || employeeEmail.isEmpty()) {
            throw new IllegalArgumentException("Employee email cannot be null or empty");
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("Original file name cannot be null or empty");
        }
        if (fileType == null) {
            throw new IllegalArgumentException("File type cannot be null");
        }
        if (uploadDate == null) {
            throw new IllegalArgumentException("Upload date cannot be null");
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        this.id = id;
        this.employeeEmail = employeeEmail.toLowerCase();
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileType = fileType;
        this.uploadDate = uploadDate;
        this.filePath = filePath;
    }

    public String getId() {
        return id;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public String getFileName() {
        return fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public DocumentType getFileType() {
        return fileType;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EmployeeDocument that = (EmployeeDocument) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EmployeeDocument{" +
                "id='" + id + '\'' +
                ", employeeEmail='" + employeeEmail + '\'' +
                ", fileName='" + fileName + '\'' +
                ", originalFileName='" + originalFileName + '\'' +
                ", fileType=" + fileType +
                ", uploadDate=" + uploadDate +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
