package com.techcorp;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public abstract class FileStorageService {
    
    public abstract String saveFile(MultipartFile file);
    
    public abstract Resource loadFile(String filename);
    
    public abstract void deleteFile(String filename);
    
    public abstract boolean validateFile(MultipartFile file, String[] allowedExtensions, long maxSizeInBytes);

}