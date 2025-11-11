package com.techcorp.service;

import com.techcorp.model.exception.FileStorageException;
import com.techcorp.model.exception.InvalidFileException;
import com.techcorp.service.FileStorageServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private FileStorageServiceImpl fileStorageService;
    private String uploadDirectory;
    private String allowedExtensions;
    private String maxSize;

    @BeforeEach
    void setUp() {
        uploadDirectory = tempDir.toString();
        allowedExtensions = "jpg,jpeg,png,gif,csv,xml,pdf";
        maxSize = "10485760";
        
        fileStorageService = new FileStorageServiceImpl(
            uploadDirectory,
            allowedExtensions,
            maxSize
        );
    }

    @Test
    void saveFile_ShouldSaveValidFile() throws IOException {
        String content = "test content";
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            content.getBytes()
        );

        String savedFilename = fileStorageService.saveFile(file);

        assertNotNull(savedFilename);
        assertTrue(savedFilename.endsWith(".jpg"));
        
        Path savedFile = Paths.get(uploadDirectory, savedFilename);
        assertTrue(Files.exists(savedFile));
        
        String savedContent = Files.readString(savedFile);
        assertEquals(content, savedContent);
    }

    @Test
    void saveFile_ShouldGenerateUniqueFilenames() {
        MockMultipartFile file1 = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "content1".getBytes()
        );
        
        MockMultipartFile file2 = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "content2".getBytes()
        );

        String filename1 = fileStorageService.saveFile(file1);
        String filename2 = fileStorageService.saveFile(file2);

        assertNotEquals(filename1, filename2);
    }

    @Test
    void saveFile_ShouldThrowException_WhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            new byte[0]
        );

        InvalidFileException exception = assertThrows(
            InvalidFileException.class,
            () -> fileStorageService.saveFile(emptyFile)
        );
        
        assertEquals("Cannot save empty file", exception.getMessage());
    }

    @Test
    void saveFile_ShouldThrowException_WhenExtensionNotAllowed() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "content".getBytes()
        );

        InvalidFileException exception = assertThrows(
            InvalidFileException.class,
            () -> fileStorageService.saveFile(file)
        );
        
        assertTrue(exception.getMessage().contains("Unallowed file extension. Allowed: "));
    }

    @Test
    void saveFile_ShouldThrowException_WhenFileTooLarge() {
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile largeFile = new MockMultipartFile(
            "file",
            "large.jpg",
            "image/jpeg",
            largeContent
        );

        InvalidFileException exception = assertThrows(
            InvalidFileException.class,
            () -> fileStorageService.saveFile(largeFile)
        );
        
        assertTrue(exception.getMessage().contains("File is too large. Maximum size: "));
    }

    @Test
    void saveFile_ShouldAcceptDifferentAllowedExtensions() {
        String[] extensions = {"jpg", "csv", "xml", "pdf", "png", "gif"};

        for (String ext : extensions) {
            MockMultipartFile file = new MockMultipartFile(
                "file",
                "test." + ext,
                "application/octet-stream",
                "content".getBytes()
            );

            String savedFilename = fileStorageService.saveFile(file);

            assertNotNull(savedFilename);
            assertTrue(savedFilename.endsWith("." + ext));
        }
    }

    @Test
    void loadFile_ShouldLoadExistingFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );
        String savedFilename = fileStorageService.saveFile(file);

        Resource resource = fileStorageService.loadFile(savedFilename);

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    void loadFile_ShouldReturnResource_WhenFileDoesNotExist() {
        String nonExistentFile = "nonexistent.jpg";

        Resource resource = fileStorageService.loadFile(nonExistentFile);

        assertNotNull(resource);
        assertFalse(resource.exists());
    }

    @Test
    void deleteFile_ShouldDeleteExistingFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );
        String savedFilename = fileStorageService.saveFile(file);
        Path filePath = Paths.get(uploadDirectory, savedFilename);
        assertTrue(Files.exists(filePath));

        fileStorageService.deleteFile(savedFilename);

        assertFalse(Files.exists(filePath));
    }

    @Test
    void deleteFile_ShouldNotThrowException_WhenFileDoesNotExist() {
        String nonExistentFile = "nonexistent.jpg";

        assertDoesNotThrow(() -> fileStorageService.deleteFile(nonExistentFile));
    }

    @Test
    void saveFile_ShouldHandleFileNamesWithSpecialCharacters() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test file@#$%.jpg",
            "image/jpeg",
            "content".getBytes()
        );

        String savedFilename = fileStorageService.saveFile(file);

        assertNotNull(savedFilename);
        assertTrue(savedFilename.endsWith(".jpg"));
        assertFalse(savedFilename.contains("@"));
        assertFalse(savedFilename.contains("#"));
        assertFalse(savedFilename.contains("$"));
    }

    @Test
    void saveFile_ShouldHandleFileNameWithoutExtension() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "testfile",
            "application/octet-stream",
            "content".getBytes()
        );

        InvalidFileException exception = assertThrows(
            InvalidFileException.class,
            () -> fileStorageService.saveFile(file)
        );
        
        assertTrue(exception.getMessage().contains("Unallowed file extension. Allowed: "));
    }

    @Test
    void saveFile_ShouldPreserveFileExtension() {
        MockMultipartFile csvFile = new MockMultipartFile(
            "file",
            "employees.csv",
            "text/csv",
            "name,email\nJohn,john@example.com".getBytes()
        );

        String savedFilename = fileStorageService.saveFile(csvFile);

        assertTrue(savedFilename.endsWith(".csv"));
    }

    @Test
    void saveFile_ShouldReplaceExistingFileWithSameName() throws IOException {
        String originalContent = "original content";
        String newContent = "new content";
        
        MockMultipartFile file1 = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            originalContent.getBytes()
        );
        
        String filename1 = fileStorageService.saveFile(file1);
        
        Path targetPath = Paths.get(uploadDirectory, filename1);
        Files.writeString(targetPath, originalContent);
        
        MockMultipartFile file2 = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            newContent.getBytes()
        );

        String filename2 = fileStorageService.saveFile(file2);

        assertNotEquals(filename1, filename2);
        
        assertTrue(Files.exists(Paths.get(uploadDirectory, filename1)));
        assertTrue(Files.exists(Paths.get(uploadDirectory, filename2)));
    }

    @Test
    void constructor_ShouldCreateUploadDirectory() {
        Path newTempDir = tempDir.resolve("new-uploads");
        assertFalse(Files.exists(newTempDir));

        new FileStorageServiceImpl(
            newTempDir.toString(),
            "jpg,png",
            "1048576"
        );

        assertTrue(Files.exists(newTempDir));
    }

    @Test
    void saveFile_ShouldHandleCaseInsensitiveExtensions() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.JPG",
            "image/jpeg",
            "content".getBytes()
        );

        InvalidFileException exception = assertThrows(
            InvalidFileException.class,
            () -> fileStorageService.saveFile(file)
        );
        
        assertTrue(exception.getMessage().contains("Unallowed file extension. Allowed: "));
    }

    @Test
    void saveFile_ShouldAcceptExactMaxSize() {
        int maxSizeBytes = 10485760; // 10MB
        byte[] content = new byte[maxSizeBytes];
        
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "maxsize.jpg",
            "image/jpeg",
            content
        );

        String savedFilename = fileStorageService.saveFile(file);

        assertNotNull(savedFilename);
    }
}

