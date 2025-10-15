package com.techcorp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.techcorp.exception.ApiException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class ApiService {
    private HttpClient client;

    public ApiService(HttpClient client) {
        this.client = client;
    }

    public List<Employee> fetchEmployeesFromApi(String apiUrl) throws ApiException {
        List<Employee> employees = new ArrayList<>();

        try {
            HttpResponse<String> response = getResponse(apiUrl);

            if (response.statusCode() != 200) {
                throw new ApiException("HTTP error: " + response.statusCode());
            }

            JsonArray jsonArray = JsonParser.parseString(response.body())
                                            .getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();

                Employee employee = EmployeeMapper.jsonObjectToEmployee(jsonObject);

                employees.add(employee);
            }

        } catch (com.google.gson.JsonSyntaxException e) {
            throw new ApiException("Failed to parse JSON response: " + e.getMessage());
        } catch (java.io.IOException e) {
            throw new ApiException("Network error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException("Unexpected error: " + e.getMessage());
        }

        return employees;
    }

    private HttpResponse<String> getResponse(String apiUrl) throws IOException, InterruptedException {
        HttpRequest request = buildGetRequest(apiUrl);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest buildGetRequest(String apiUrl) {
        return HttpRequest.newBuilder()
                         .uri(URI.create(apiUrl))
                         .GET()
                         .build();
    }
}

