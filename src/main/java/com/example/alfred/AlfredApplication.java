package com.example.alfred; // this should be your root package
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlfredApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlfredApplication.class, args);  // Starts the Spring Boot application
    }
}
