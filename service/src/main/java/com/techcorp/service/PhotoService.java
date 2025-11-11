package com.techcorp.service;

import com.techcorp.model.Employee;
import com.techcorp.model.exception.EmployeeNotFoundException;
import com.techcorp.model.exception.InvalidFileException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class PhotoService {

    private static final String UPLOAD_DIR = "uploads/photos";
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_SIZE_BYTES = 2 * 1024 * 1024; // 2MB
    
    private final FileStorageService fileStorageService;
    private final EmployeeService employeeService;

    public PhotoService(FileStorageService fileStorageService, EmployeeService employeeService) {
        this.fileStorageService = fileStorageService;
        this.employeeService = employeeService;
        
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
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
        String extension = getFileExtension(file.getOriginalFilename());
        String safeFileName = normalizedEmail.replaceAll("[^a-zA-Z0-9@._-]", "_") + "." + extension;

        if (employee.getPhotoFileName() != null) {
            deletePhoto(email);
        }

        employee.setPhotoFileName(safeFileName);

        fileStorageService.saveFile(file, UPLOAD_DIR + "/" + normalizedEmail);

        return safeFileName;
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

        return fileStorageService.loadFile(employee.getPhotoFileName());
    }

    public void deletePhoto(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Employee email cannot be null or empty");
        }

        Employee employee = employeeService.getEmployeeByEmail(email)
            .orElseThrow(() -> new EmployeeNotFoundException(
                "Employee with email " + email + " not found"
            ));

        fileStorageService.deleteFile(employee.getPhotoFileName());
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

