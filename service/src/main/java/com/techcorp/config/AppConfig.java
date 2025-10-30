package com.techcorp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration
public class AppConfig {
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .create();
    }
}
