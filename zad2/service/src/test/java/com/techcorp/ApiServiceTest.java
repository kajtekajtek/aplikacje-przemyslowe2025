package com.techcorp;

import com.techcorp.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiServiceTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private ApiService apiService;

    private static final String MOCK_API_URL = "https://jsonplaceholder.typicode.com/users";

    @BeforeEach
    public void setUp() {
        apiService = new ApiService(mockHttpClient);
    }

    @Test
    public void testFetchEmployeesFromApi_Success() throws Exception {
        // Given: Valid JSON response with multiple employees
        String validJsonResponse = """
            [
                {
                    "name": "John Doe",
                    "email": "john.doe@example.com",
                    "company": {
                        "name": "TechCorp"
                    }
                },
                {
                    "name": "Jane Smith",
                    "email": "jane.smith@example.com",
                    "company": {
                        "name": "Innovate Ltd"
                    }
                }
            ]
            """;

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(validJsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When
        List<Employee> employees = apiService.fetchEmployeesFromApi(MOCK_API_URL);

        // Then
        assertNotNull(employees);
        assertEquals(2, employees.size());

        Employee employee1 = employees.get(0);
        assertEquals("John", employee1.getFirstName());
        assertEquals("Doe", employee1.getLastName());
        assertEquals("john.doe@example.com", employee1.getEmailAddress());
        assertEquals("TechCorp", employee1.getCompanyName());
        assertEquals(Role.ENGINEER, employee1.getRole());
        assertEquals(Role.ENGINEER.getBaseSalary(), employee1.getSalary());

        Employee employee2 = employees.get(1);
        assertEquals("Jane", employee2.getFirstName());
        assertEquals("Smith", employee2.getLastName());
        assertEquals("jane.smith@example.com", employee2.getEmailAddress());
        assertEquals("Innovate Ltd", employee2.getCompanyName());
        assertEquals(Role.ENGINEER, employee2.getRole());

        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    public void testFetchEmployeesFromApi_SingleNamePerson() throws Exception {
        // Given: Employee with single name (no last name)
        String jsonResponse = """
            [
                {
                    "name": "Madonna",
                    "email": "madonna@example.com",
                    "company": {
                        "name": "StarCorp"
                    }
                }
            ]
            """;

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When
        List<Employee> employees = apiService.fetchEmployeesFromApi(MOCK_API_URL);

        // Then
        assertEquals(1, employees.size());
        Employee employee = employees.get(0);
        assertEquals("Madonna", employee.getFirstName());
        assertEquals("", employee.getLastName());
    }

    @Test
    public void testFetchEmployeesFromApi_EmptyArray() throws Exception {
        // Given: Empty JSON array
        String emptyJsonResponse = "[]";

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(emptyJsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When
        List<Employee> employees = apiService.fetchEmployeesFromApi(MOCK_API_URL);

        // Then
        assertNotNull(employees);
        assertEquals(0, employees.size());
    }

    @Test
    public void testFetchEmployeesFromApi_HttpError404() throws Exception {
        // Given: HTTP 404 error
        when(mockHttpResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.fetchEmployeesFromApi(MOCK_API_URL);
        });

        assertTrue(exception.getMessage().contains("HTTP error: 404"));
    }

    @Test
    public void testFetchEmployeesFromApi_HttpError500() throws Exception {
        // Given: HTTP 500 error
        when(mockHttpResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.fetchEmployeesFromApi(MOCK_API_URL);
        });

        assertTrue(exception.getMessage().contains("HTTP error: 500"));
    }

    @Test
    public void testFetchEmployeesFromApi_InvalidJson() throws Exception {
        // Given: Invalid JSON response
        String invalidJsonResponse = "{ this is not valid json }";

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(invalidJsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.fetchEmployeesFromApi(MOCK_API_URL);
        });

        assertTrue(exception.getMessage().contains("Failed to parse JSON response"));
    }

    @Test
    public void testFetchEmployeesFromApi_NetworkError() throws Exception {
        // Given: Network IOException
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new IOException("Network unavailable"));

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.fetchEmployeesFromApi(MOCK_API_URL);
        });

        assertTrue(exception.getMessage().contains("Network error"));
        assertTrue(exception.getMessage().contains("Network unavailable"));
    }

    @Test
    public void testFetchEmployeesFromApi_InterruptedException() throws Exception {
        // Given: InterruptedException
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new InterruptedException("Request interrupted"));

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.fetchEmployeesFromApi(MOCK_API_URL);
        });

        assertTrue(exception.getMessage().contains("Request interrupted"));
        assertTrue(Thread.interrupted()); // Verify interrupt flag was restored
    }

    @Test
    public void testFetchEmployeesFromApi_JsonNotArray() throws Exception {
        // Given: JSON response is object, not array
        String jsonObject = """
            {
                "name": "John Doe",
                "email": "john@example.com"
            }
            """;

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonObject);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.fetchEmployeesFromApi(MOCK_API_URL);
        });

        assertTrue(exception.getMessage().contains("Unexpected error") || 
                   exception.getMessage().contains("Failed to parse JSON response"));
    }

    @Test
    public void testFetchEmployeesFromApi_MissingCompanyField() throws Exception {
        // Given: Employee without company field (should cause error)
        String jsonResponse = """
            [
                {
                    "name": "John Doe",
                    "email": "john.doe@example.com"
                }
            ]
            """;

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.fetchEmployeesFromApi(MOCK_API_URL);
        });

        assertTrue(exception.getMessage().contains("Unexpected error"));
    }

    @Test
    public void testFetchEmployeesFromApi_MissingNameField() throws Exception {
        // Given: Employee without name field
        String jsonResponse = """
            [
                {
                    "email": "john.doe@example.com",
                    "company": {
                        "name": "TechCorp"
                    }
                }
            ]
            """;

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.fetchEmployeesFromApi(MOCK_API_URL);
        });

        assertTrue(exception.getMessage().contains("Unexpected error"));
    }

    @Test
    public void testFetchEmployeesFromApi_MissingEmailField() throws Exception {
        // Given: Employee without email field
        String jsonResponse = """
            [
                {
                    "name": "John Doe",
                    "company": {
                        "name": "TechCorp"
                    }
                }
            ]
            """;

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.fetchEmployeesFromApi(MOCK_API_URL);
        });

        assertTrue(exception.getMessage().contains("Unexpected error"));
    }

    @Test
    public void testFetchEmployeesFromApi_MultipleNamesWithSpaces() throws Exception {
        // Given: Employee with multiple names
        String jsonResponse = """
            [
                {
                    "name": "John Paul Smith Jr.",
                    "email": "john.smith@example.com",
                    "company": {
                        "name": "TechCorp"
                    }
                }
            ]
            """;

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When
        List<Employee> employees = apiService.fetchEmployeesFromApi(MOCK_API_URL);

        // Then
        assertEquals(1, employees.size());
        Employee employee = employees.get(0);
        assertEquals("John", employee.getFirstName());
        assertEquals("Paul Smith Jr.", employee.getLastName());
    }

    @Test
    public void testFetchEmployeesFromApi_WithExtraWhitespace() throws Exception {
        // Given: Names with extra whitespace
        String jsonResponse = """
            [
                {
                    "name": "  John   Doe  ",
                    "email": "john.doe@example.com",
                    "company": {
                        "name": "TechCorp"
                    }
                }
            ]
            """;

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When
        List<Employee> employees = apiService.fetchEmployeesFromApi(MOCK_API_URL);

        // Then
        assertEquals(1, employees.size());
        Employee employee = employees.get(0);
        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
    }

    @Test
    public void testFetchEmployeesFromApi_VerifiesCorrectUrl() throws Exception {
        // Given
        String customUrl = "https://custom-api.example.com/employees";
        String jsonResponse = "[]";

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When
        apiService.fetchEmployeesFromApi(customUrl);

        // Then
        verify(mockHttpClient).send(
            argThat(request -> request.uri().toString().equals(customUrl)),
            eq(HttpResponse.BodyHandlers.ofString())
        );
    }

    @Test
    public void testFetchEmployeesFromApi_AssignsEngineerRoleToAll() throws Exception {
        // Given: Multiple employees
        String jsonResponse = """
            [
                {
                    "name": "John Doe",
                    "email": "john@example.com",
                    "company": {"name": "CompanyA"}
                },
                {
                    "name": "Jane Smith",
                    "email": "jane@example.com",
                    "company": {"name": "CompanyB"}
                }
            ]
            """;

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);

        // When
        List<Employee> employees = apiService.fetchEmployeesFromApi(MOCK_API_URL);

        // Then
        assertEquals(2, employees.size());
        employees.forEach(employee -> {
            assertEquals(Role.ENGINEER, employee.getRole());
            assertEquals(Role.ENGINEER.getBaseSalary(), employee.getSalary());
        });
    }
}

