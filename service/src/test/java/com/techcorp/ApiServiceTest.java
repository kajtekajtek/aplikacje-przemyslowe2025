package com.techcorp;

import com.techcorp.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiServiceTest {

    private final String API_URL = "http://example.com/api";

    private final String HTTP_ERROR_MESSAGE                = "HTTP error:";
    private final String MALFORMED_JSON_ERROR_MESSAGE      = "Failed to parse JSON response:";
    private final String NETWORK_ERROR_MESSAGE             = "Network error:";
    private final String REQUEST_INTERRUPTED_ERROR_MESSAGE = "Request interrupted:";

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private ApiService apiService;

    @BeforeEach
    void setUp() {
        apiService = new ApiService(httpClient);
    }

    @Test
    @DisplayName("200 OK: parses JSON array and maps to ENGINEER employees")
    void shouldReturn200WhenSuccessfullyFetchedEmployees() throws Exception {
        String json = "[\n" +
                "  {\"name\":\"John Doe\",\"email\":\"john@acme.com\",\"company\":{\"name\":\"Acme\"}},\n" +
                "  {\"name\":\"Jane Smith\",\"email\":\"jane@globex.com\",\"company\":{\"name\":\"Globex\"}}\n" +
                "]";

        when(httpClient.send(
            any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(json);

        List<Employee> employees = apiService.fetchEmployeesFromApi(API_URL);

        assertNotNull(employees);
        assertEquals(2, employees.size());
        assertEquals("John", employees.get(0).getFirstName());
        assertEquals("Doe", employees.get(0).getLastName());
        assertEquals("Acme", employees.get(0).getCompanyName());
        assertEquals(Role.ENGINEER, employees.get(0).getRole());
    }

    @Test
    @DisplayName("404 Not Found: throws ApiException with status code")
    void shouldReturn404AndThrowWhenNotFound() throws Exception {
        when(httpClient.send(
            any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(404);

        ApiException ex = assertThrows(ApiException.class, () ->
                apiService.fetchEmployeesFromApi(API_URL)
        );
        assertTrue(ex.getMessage().contains(HTTP_ERROR_MESSAGE));
        assertTrue(ex.getMessage().contains("404"));
    }

    @Test
    @DisplayName("500 Internal Server Error: throws ApiException with status code")
    void shouldReturn500AndThrowWhenInternalServerError() throws Exception {
        when(httpClient.send(
            any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);

        ApiException ex = assertThrows(ApiException.class, () ->
                apiService.fetchEmployeesFromApi(API_URL)
        );
        assertTrue(ex.getMessage().contains("HTTP error"));
        assertTrue(ex.getMessage().contains("500"));
    }

    @Test
    @DisplayName("Invalid JSON: throws ApiException with parse message")
    void shouldReturnParseErrorAndThrowWhenInvalidJson() throws Exception {
        when(httpClient.send(
            any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{");

        ApiException ex = assertThrows(ApiException.class, () ->
                apiService.fetchEmployeesFromApi(API_URL)
        );
        assertTrue(ex.getMessage().contains(MALFORMED_JSON_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("IOException: throws ApiException with network error")
    void shouldReturnNetworkErrorAndThrowWhenIOException() throws Exception {
        when(httpClient.send(
            any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenThrow(new java.io.IOException("boom"));

        ApiException ex = assertThrows(ApiException.class, () ->
                apiService.fetchEmployeesFromApi(API_URL)
        );
        assertTrue(ex.getMessage().contains(NETWORK_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("InterruptedException: sets interrupt flag and throws ApiException")
    void shouldReturnRequestInterruptedErrorAndThrowWhenInterruptedException() throws Exception {
        when(httpClient.send(
            any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenThrow(new InterruptedException("stop"));

        ApiException ex = assertThrows(ApiException.class, () ->
                apiService.fetchEmployeesFromApi(API_URL)
        );
        assertTrue(Thread.currentThread().isInterrupted());
        assertTrue(ex.getMessage().contains(REQUEST_INTERRUPTED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Name parsing: handles single and multi-word names")
    void shouldReturnEmployeesWithSingleAndMultiWordNames() throws Exception {
        String json = "[\n" +
                "  {\"name\":\"Single\",\"email\":\"s@x.com\",\"company\":{\"name\":\"X\"}},\n" +
                "  {\"name\":\"Multi Word Name\",\"email\":\"m@y.com\",\"company\":{\"name\":\"Y\"}}\n" +
                "]";

        when(httpClient.send(
            any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(json);

        List<Employee> employees = apiService.fetchEmployeesFromApi(API_URL);

        assertEquals("Single", employees.get(0).getFirstName());
        assertEquals("", employees.get(0).getLastName());

        assertEquals("Multi", employees.get(1).getFirstName());
        assertEquals("Word Name", employees.get(1).getLastName());
    }
}


