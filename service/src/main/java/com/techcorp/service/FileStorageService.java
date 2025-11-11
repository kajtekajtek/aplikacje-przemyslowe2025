package com.techcorp.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public abstract class FileStorageService {
    
    public abstract String saveFile(MultipartFile file);
    
    public abstract String saveFile(MultipartFile file, String customDirectory);
    
    public abstract Resource loadFile(String filename);
    
    public abstract void deleteFile(String filename);
    
    public abstract String getFullPath(String filename);

}