package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    private final RaceAPIService raceAPIService;
    private final DriverAPIService driverAPIService;
    private final LatestSessionService latestSessionService;

    public ImportService(RaceAPIService raceAPIService, DriverAPIService driverAPIService, LatestSessionService latestSessionService) {
        this.raceAPIService = raceAPIService;
        this.driverAPIService = driverAPIService;
        this.latestSessionService = latestSessionService;
    }



    // Method to import races and then drivers
    public void importData() throws IOException, InterruptedException {
        if (latestSessionService.isNewSessionAvailable()) {
            logger.info("New session found, beginning import.");
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
        } else {
            logger.info("No new sessions found, data is up to date");
        }
    }
}
