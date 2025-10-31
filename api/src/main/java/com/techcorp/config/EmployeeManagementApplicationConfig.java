package com.techcorp.config;

import java.util.Scanner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeeManagementApplicationConfig {

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
}
