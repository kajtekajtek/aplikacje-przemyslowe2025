package com.techcorp;

import com.techcorp.exception.FileStorageException;
import com.techcorp.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl extends FileStorageService {

    private final Path uploadPath;
    private final List<String> allowedExtensions;
    private final long maxSizeInBytes;

    public FileStorageServiceImpl(
            @Value("${app.upload.directory}")          String uploadDir,
            @Value("${app.upload.allowed-extensions}") String extensions,
            @Value("${app.upload.max-size}")           String maxSizeStr
    ) {
        this.maxSizeInBytes = parseSize(maxSizeStr);
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.allowedExtensions = Arrays.asList(extensions.split(","));

        createUploadDirectory();
    }
    
    private long parseSize(String sizeStr) {
        if (sizeStr == null || sizeStr.isEmpty()) {
            throw new IllegalArgumentException("Max size cannot be null or empty");
        }
        
        sizeStr = sizeStr.trim().toUpperCase();
        
        try {
            return Long.parseLong(sizeStr);
        } catch (NumberFormatException e) {
            if (sizeStr.endsWith("MB")) {
                String number = sizeStr.substring(0, sizeStr.length() - 2).trim();
                return Long.parseLong(number) * 1024 * 1024;
            } else if (sizeStr.endsWith("KB")) {
                String number = sizeStr.substring(0, sizeStr.length() - 2).trim();
                return Long.parseLong(number) * 1024;
            } else if (sizeStr.endsWith("GB")) {
                String number = sizeStr.substring(0, sizeStr.length() - 2).trim();
                return Long.parseLong(number) * 1024 * 1024 * 1024;
            }
            throw new IllegalArgumentException("Invalid size format: " + sizeStr);
        }
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException ex) {
            throw new FileStorageException("Cannot create upload directory", ex);
        }
    }

    @Override
    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException(
                "Cannot save empty file"
            );
        }

        String originalFilename = file.getOriginalFilename();
        String extension        = getFileExtension(originalFilename);

        isValidFileCheck(originalFilename, extension, file.getSize());

        String filename = generateUniqueFilename(originalFilename, extension);
        Path targetLocation = this.uploadPath.resolve(filename);

        try {
            Files.copy(file.getInputStream(), targetLocation, 
                StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException ex) {
            throw new FileStorageException("Error saving file", ex);
        }
    }

    @Override
    public Resource loadFile(String filename) {
        Path filePath = uploadPath.resolve(filename).normalize();
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException ex) {
            throw new FileStorageException("Error loading file", ex);
        }
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Path filePath = uploadPath.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Error deleting file", ex);
        }
    }

    private void isValidFileCheck(String name, String extension, long fileSize) {
        if (name == null) {
            throw new InvalidFileException("File name is required");
        }

        if (!allowedExtensions.contains(extension)) {
            throw new InvalidFileException(
                "Unallowed file extension. Allowed: " 
                + allowedExtensions
            );
        }

        if (fileSize > maxSizeInBytes) {
            throw new InvalidFileException("File is too large. Maximum size: " 
            + maxSizeInBytes + " bytes"
            );
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot + 1);
    }

    private String generateUniqueFilename(String bookTitle, String extension) {
        String cleanTitle = bookTitle.replaceAll("[^a-zA-Z0-9]", "_");
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return cleanTitle + "_" + uniqueId + "." + extension;
    }
}