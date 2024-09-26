package com.f1telemetry.race_telemetry_analyzer.config;

import com.f1telemetry.race_telemetry_analyzer.service.OpenF1API.ImportService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
