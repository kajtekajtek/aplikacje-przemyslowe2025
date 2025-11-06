package com.techcorp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path uploadDirectory;
    private final Path reportDirectory;

    public FileStorageServiceImpl(
        @Value("${app.upload.directory}")  String uploadDirectory, 
        @Value("${app.reports.directory}") String reportDirectory
    ) {
        this.uploadDirectory = Paths.get(uploadDirectory)
            .toAbsolutePath()
            .normalize();
        this.reportDirectory = Paths.get(reportDirectory)
            .toAbsolutePath()
            .normalize();

        try {
            Files.createDirectories(this.uploadDirectory);
            Files.createDirectories(this.reportDirectory);
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload directory", e);
        }
    }
    
    public String saveFile(MultipartFile file) {
        return null;
    }
    
    public Resource loadFile(String filename) {
        return null;
    }
    
    public void deleteFile(String filename) {
        return;
    }
    
    public boolean validateFile(
        MultipartFile file, String[] allowedExtensions, long maxSizeInBytes
    ) {
        return false;
    }

}