package com.techcorp.exception;

import com.techcorp.model.dto.ErrorResponse;
import com.techcorp.model.exception.DuplicateEmailException;
import com.techcorp.model.exception.EmployeeNotFoundException;
import com.techcorp.model.exception.FileNotFoundException;
import com.techcorp.model.exception.FileStorageException;
import com.techcorp.model.exception.InvalidDataException;
import com.techcorp.model.exception.InvalidFileException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/api/test");
        webRequest = new ServletWebRequest(servletRequest);
    }

    @Test
    void handleEmployeeNotFoundException_ShouldReturn404() {
        EmployeeNotFoundException exception = new EmployeeNotFoundException("Employee not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleEmployeeNotFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Employee not found", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleDuplicateEmailException_ShouldReturn409() {
        DuplicateEmailException exception = new DuplicateEmailException("Email already exists");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateEmailException(exception, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Email already exists", response.getBody().getMessage());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleInvalidDataException_ShouldReturn400() {
        InvalidDataException exception = new InvalidDataException("Invalid data");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidDataException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid data", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturn400() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid argument", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleFileNotFoundException_ShouldReturn404() {
        FileNotFoundException exception = new FileNotFoundException("File not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleFileNotFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("File not found", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleFileStorageException_ShouldReturn500() {
        FileStorageException exception = new FileStorageException("Failed to store file");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleFileStorageException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed to store file", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleInvalidFileException_ShouldReturn400() {
        InvalidFileException exception = new InvalidFileException("Invalid file format");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidFileException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid file format", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleMaxUploadSizeExceededException_ShouldReturn413() {
        MaxUploadSizeExceededException exception = new MaxUploadSizeExceededException(10485760);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMaxUploadSizeExceededException(exception, webRequest);

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("File is too large"));
        assertTrue(response.getBody().getMessage().contains("10485760"));
        assertEquals(413, response.getBody().getStatus());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleGenericException_ShouldReturn500() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("An unexpected error occurred"));
        assertTrue(response.getBody().getMessage().contains("Unexpected error"));
        assertEquals(500, response.getBody().getStatus());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void allHandlers_ShouldSetTimestamp() {
        EmployeeNotFoundException exception = new EmployeeNotFoundException("Test");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleEmployeeNotFoundException(exception, webRequest);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void allHandlers_ShouldIncludeRequestPath() {
        MockHttpServletRequest customRequest = new MockHttpServletRequest();
        customRequest.setRequestURI("/custom/path");
        WebRequest customWebRequest = new ServletWebRequest(customRequest);
        
        InvalidFileException exception = new InvalidFileException("Test error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidFileException(exception, customWebRequest);

        assertNotNull(response.getBody());
        assertEquals("/custom/path", response.getBody().getPath());
    }
}

