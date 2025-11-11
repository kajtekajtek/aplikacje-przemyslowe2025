package com.techcorp.service;

import com.techcorp.model.Employee;
import com.techcorp.model.exception.EmployeeNotFoundException;
import com.techcorp.model.exception.InvalidFileException;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class PhotoService {

    private final String uploadPathString;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_SIZE_BYTES = 2 * 1024 * 1024; // 2MB
    
    private final FileStorageService fileStorageService;
    private final EmployeeService employeeService;

    public PhotoService(
        FileStorageService fileStorageService, 
        EmployeeService employeeService,
        @Value("${app.upload.directory}") String uploadPathString
    ) {
        this.uploadPathString = uploadPathString + "/photos";
        this.fileStorageService = fileStorageService;
        this.employeeService = employeeService;
        
        try {
            Files.createDirectories(Paths.get(this.uploadPathString));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory for photos!", e);
        }
    }

    public String savePhoto(String email, MultipartFile file) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Employee email cannot be null or empty");
        }
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Cannot save empty file");
        }

        Employee employee = employeeService.getEmployeeByEmail(email)
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + email + " not found"
            ));

        validatePhoto(file);

        String normalizedEmail = email.toLowerCase();

        if (employee.getPhotoFileName() != null) {
            deletePhoto(email);
        }

        String fullPath = fileStorageService.saveFile(file, this.uploadPathString + "/" + normalizedEmail);
        
        String fileName = fullPath.substring(fullPath.lastIndexOf('/') + 1);
        employee.setPhotoFileName(fileName);

        return fileName;
    }

    public Resource loadPhoto(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Employee email cannot be null or empty");
        }

        Employee employee = employeeService.getEmployeeByEmail(email)
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + email + " not found"
            ));

        if (employee.getPhotoFileName() == null) {
            throw new com.techcorp.model.exception.FileNotFoundException(
                "Employee " + email + " has no photo"
            );
        }

        String normalizedEmail = email.toLowerCase();
        String relativePath = "photos/" + normalizedEmail + "/" + employee.getPhotoFileName();
        return fileStorageService.loadFile(relativePath);
    }

    public void deletePhoto(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Employee email cannot be null or empty");
        }

        Employee employee = employeeService.getEmployeeByEmail(email)
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + email + " not found"
            ));

        if (employee.getPhotoFileName() != null) {
            String normalizedEmail = email.toLowerCase();
            String relativePath = "photos/" + normalizedEmail + "/" + employee.getPhotoFileName();
            fileStorageService.deleteFile(relativePath);
        }
        employee.setPhotoFileName(null);
    }

    private void validatePhoto(MultipartFile file) {
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new InvalidFileException(
                "File is too large. Maximum size: 2MB"
            );
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidFileException(
                "Invalid file format. Allowed formats: JPG, PNG"
            );
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot + 1);
    }

    public String getContentType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }
}

