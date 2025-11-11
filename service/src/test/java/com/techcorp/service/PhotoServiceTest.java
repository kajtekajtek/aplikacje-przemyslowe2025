package com.techcorp.service;

import com.techcorp.model.Employee;
import com.techcorp.model.Role;
import com.techcorp.model.exception.EmployeeNotFoundException;
import com.techcorp.model.exception.InvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PhotoService Tests")
class PhotoServiceTest {

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private EmployeeService employeeService;

    private PhotoService photoService;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        photoService = new PhotoService(fileStorageService, employeeService);
        testEmployee = Employee.createEmployee(
            "Doe", "John", "john@techcorp.com", "TechCorp", Role.ENGINEER
        );
    }

    @Nested
    @DisplayName("Save Photo Tests")
    class SavePhotoTests {

        @Test
        @DisplayName("Should save photo successfully")
        void shouldSavePhotoSuccessfully() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "photo content".getBytes()
            );

            when(employeeService.getEmployeeByEmail("john@techcorp.com"))
                .thenReturn(Optional.of(testEmployee));
            when(fileStorageService.saveFile(any(), eq("uploads/photos/john@techcorp.com")))
                .thenReturn("uploads/photos/john@techcorp.com/uuid_photo.jpg");

            String fileName = photoService.savePhoto("john@techcorp.com", file);

            assertNotNull(fileName);
            assertTrue(fileName.contains("john@techcorp.com"));
            assertEquals("john@techcorp.com.jpg", testEmployee.getPhotoFileName());
            verify(fileStorageService, times(1)).saveFile(any(), eq("uploads/photos/john@techcorp.com"));
        }

        @Test
        @DisplayName("Should save PNG photo")
        void shouldSavePngPhoto() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "photo.png", "image/png", "photo content".getBytes()
            );

            when(employeeService.getEmployeeByEmail("john@techcorp.com"))
                .thenReturn(Optional.of(testEmployee));
            when(fileStorageService.saveFile(any(), eq("uploads/photos/john@techcorp.com")))
                .thenReturn("uploads/photos/john@techcorp.com/uuid_photo.png");

            String fileName = photoService.savePhoto("john@techcorp.com", file);

            assertNotNull(fileName);
            assertTrue(fileName.endsWith(".png"));
        }

        @Test
        @DisplayName("Should throw exception when employee not found")
        void shouldThrowExceptionWhenEmployeeNotFound() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "photo content".getBytes()
            );

            when(employeeService.getEmployeeByEmail("notfound@techcorp.com"))
                .thenReturn(Optional.empty());

            assertThrows(
                EmployeeNotFoundException.class,
                () -> photoService.savePhoto("notfound@techcorp.com", file)
            );
        }

        @Test
        @DisplayName("Should throw exception when file is empty")
        void shouldThrowExceptionWhenFileIsEmpty() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", new byte[0]
            );

            assertThrows(
                InvalidFileException.class,
                () -> photoService.savePhoto("john@techcorp.com", file)
            );
        }

        @Test
        @DisplayName("Should throw exception when file is too large")
        void shouldThrowExceptionWhenFileIsTooLarge() {
            byte[] largeContent = new byte[3 * 1024 * 1024]; // 3MB
            MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", largeContent
            );

            when(employeeService.getEmployeeByEmail("john@techcorp.com"))
                .thenReturn(Optional.of(testEmployee));

            InvalidFileException exception = assertThrows(
                InvalidFileException.class,
                () -> photoService.savePhoto("john@techcorp.com", file)
            );

            assertTrue(exception.getMessage().contains("too large"));
        }

        @Test
        @DisplayName("Should throw exception for invalid file format")
        void shouldThrowExceptionForInvalidFileFormat() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "pdf content".getBytes()
            );

            when(employeeService.getEmployeeByEmail("john@techcorp.com"))
                .thenReturn(Optional.of(testEmployee));

            InvalidFileException exception = assertThrows(
                InvalidFileException.class,
                () -> photoService.savePhoto("john@techcorp.com", file)
            );

            assertTrue(exception.getMessage().contains("Invalid file format"));
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "photo content".getBytes()
            );

            assertThrows(
                IllegalArgumentException.class,
                () -> photoService.savePhoto(null, file)
            );
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "photo content".getBytes()
            );

            assertThrows(
                IllegalArgumentException.class,
                () -> photoService.savePhoto("", file)
            );
        }

        @Test
        @DisplayName("Should accept JPEG extension")
        void shouldAcceptJpegExtension() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpeg", "image/jpeg", "photo content".getBytes()
            );

            when(employeeService.getEmployeeByEmail("john@techcorp.com"))
                .thenReturn(Optional.of(testEmployee));
            when(fileStorageService.saveFile(any(), eq("uploads/photos/john@techcorp.com")))
                .thenReturn("uploads/photos/john@techcorp.com/uuid_photo.jpeg");

            assertDoesNotThrow(() -> photoService.savePhoto("john@techcorp.com", file));
        }

        @Test
        @DisplayName("Should replace old photo when uploading new one")
        void shouldReplaceOldPhoto() {
            testEmployee.setPhotoFileName("old_photo.jpg");

            MockMultipartFile file = new MockMultipartFile(
                "file", "new_photo.jpg", "image/jpeg", "new photo content".getBytes()
            );

            when(employeeService.getEmployeeByEmail("john@techcorp.com"))
                .thenReturn(Optional.of(testEmployee))
                .thenReturn(Optional.of(testEmployee));
            doNothing().when(fileStorageService).deleteFile("old_photo.jpg");
            when(fileStorageService.saveFile(any(), eq("uploads/photos/john@techcorp.com")))
                .thenReturn("uploads/photos/john@techcorp.com/uuid_new_photo.jpg");

            String fileName = photoService.savePhoto("john@techcorp.com", file);

            assertNotNull(fileName);
            assertEquals("john@techcorp.com.jpg", testEmployee.getPhotoFileName());
            verify(fileStorageService, times(1)).deleteFile("old_photo.jpg");
        }
    }

    @Nested
    @DisplayName("Load Photo Tests")
    class LoadPhotoTests {

        @Test
        @DisplayName("Should throw exception when employee not found")
        void shouldThrowExceptionWhenEmployeeNotFound() {
            when(employeeService.getEmployeeByEmail("notfound@techcorp.com"))
                .thenReturn(Optional.empty());

            assertThrows(
                EmployeeNotFoundException.class,
                () -> photoService.loadPhoto("notfound@techcorp.com")
            );
        }

        @Test
        @DisplayName("Should throw exception when employee has no photo")
        void shouldThrowExceptionWhenEmployeeHasNoPhoto() {
            when(employeeService.getEmployeeByEmail("john@techcorp.com"))
                .thenReturn(Optional.of(testEmployee));

            com.techcorp.model.exception.FileNotFoundException exception = assertThrows(
                com.techcorp.model.exception.FileNotFoundException.class,
                () -> photoService.loadPhoto("john@techcorp.com")
            );

            assertTrue(exception.getMessage().contains("has no photo"));
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            assertThrows(
                IllegalArgumentException.class,
                () -> photoService.loadPhoto(null)
            );
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            assertThrows(
                IllegalArgumentException.class,
                () -> photoService.loadPhoto("")
            );
        }
    }

    @Nested
    @DisplayName("Delete Photo Tests")
    class DeletePhotoTests {

        @Test
        @DisplayName("Should delete photo successfully")
        void shouldDeletePhotoSuccessfully() {
            testEmployee.setPhotoFileName("john@techcorp.com.jpg");

            when(employeeService.getEmployeeByEmail("john@techcorp.com"))
                .thenReturn(Optional.of(testEmployee));

            assertDoesNotThrow(() -> photoService.deletePhoto("john@techcorp.com"));
            assertNull(testEmployee.getPhotoFileName());
        }

        @Test
        @DisplayName("Should handle deletion when no photo exists")
        void shouldHandleDeletionWhenNoPhotoExists() {
            when(employeeService.getEmployeeByEmail("john@techcorp.com"))
                .thenReturn(Optional.of(testEmployee));

            assertDoesNotThrow(() -> photoService.deletePhoto("john@techcorp.com"));
        }

        @Test
        @DisplayName("Should throw exception when employee not found")
        void shouldThrowExceptionWhenEmployeeNotFound() {
            when(employeeService.getEmployeeByEmail("notfound@techcorp.com"))
                .thenReturn(Optional.empty());

            assertThrows(
                EmployeeNotFoundException.class,
                () -> photoService.deletePhoto("notfound@techcorp.com")
            );
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            assertThrows(
                IllegalArgumentException.class,
                () -> photoService.deletePhoto(null)
            );
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            assertThrows(
                IllegalArgumentException.class,
                () -> photoService.deletePhoto("")
            );
        }
    }

    @Nested
    @DisplayName("Content Type Tests")
    class ContentTypeTests {

        @Test
        @DisplayName("Should return correct content type for JPG")
        void shouldReturnCorrectContentTypeForJpg() {
            String contentType = photoService.getContentType("photo.jpg");
            assertEquals("image/jpeg", contentType);
        }

        @Test
        @DisplayName("Should return correct content type for JPEG")
        void shouldReturnCorrectContentTypeForJpeg() {
            String contentType = photoService.getContentType("photo.jpeg");
            assertEquals("image/jpeg", contentType);
        }

        @Test
        @DisplayName("Should return correct content type for PNG")
        void shouldReturnCorrectContentTypeForPng() {
            String contentType = photoService.getContentType("photo.png");
            assertEquals("image/png", contentType);
        }

        @Test
        @DisplayName("Should return default content type for unknown extension")
        void shouldReturnDefaultContentTypeForUnknownExtension() {
            String contentType = photoService.getContentType("document.pdf");
            assertEquals("application/octet-stream", contentType);
        }
    }
}

