package com.f1telemetry.race_telemetry_analyzer.config;

import com.f1telemetry.race_telemetry_analyzer.service.OpenF1API.ImportService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that triggers data import on application startup.
 * This class is marked as a {@code @Configuration} class and defines a
 * {@code @Bean} method that returns an {@code ApplicationRunner}. The
 * {@code ApplicationRunner} calls the ImportService importData() method
 * when the application starts, initiating the data import process.
 */
@Configuration
public class StartupImportConfig {

    private final ImportService importService;

    public StartupImportConfig(ImportService importService) {
        this.importService = importService;
    }

    @Bean
    public ApplicationRunner runOnStartup() {
        return args -> {
            importService.importData();
        };
    }
}
