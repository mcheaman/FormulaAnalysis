package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    private final RaceAPIService raceAPIService;
    private final DriverAPIService driverAPIService;

    public ImportService(RaceAPIService raceAPIService, DriverAPIService driverAPIService) {
        this.raceAPIService = raceAPIService;
        this.driverAPIService = driverAPIService;
    }

    // Method to import races and then drivers
    public void importData() throws IOException, InterruptedException {
        try {
            // Import races
            logger.info("Starting to import races from OpenF1API...");
            raceAPIService.fetchRacesFromOpenF1();
            logger.info("Races imported successfully.");

            // Import drivers
            logger.info("Starting to import drivers from OpenF1API...");
            driverAPIService.fetchDriversFromOpenF1();
            logger.info("Drivers imported successfully.");
        } catch (Exception e) {
            logger.error("Error occurred during race and driver import: ", e);
        }
    }
}
