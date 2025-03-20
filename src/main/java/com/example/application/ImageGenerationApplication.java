package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
public class ImageGenerationApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(ImageGenerationApplication.class, args);
    }

}
