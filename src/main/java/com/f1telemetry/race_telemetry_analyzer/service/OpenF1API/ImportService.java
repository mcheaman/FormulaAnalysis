package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;




import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.model.Race;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    private final RaceAPIService raceAPIService;
    private final DriverAPIService driverAPIService;
    private final LapAPIService lapAPIService;
    private final LatestSessionService latestSessionService;

    public ImportService(RaceAPIService raceAPIService, DriverAPIService driverAPIService, LapAPIService lapAPIService, LatestSessionService latestSessionService) {
        this.raceAPIService = raceAPIService;
        this.driverAPIService = driverAPIService;
        this.lapAPIService = lapAPIService;
        this.latestSessionService = latestSessionService;
    }



    // Method to import races and then drivers
    public void importData() throws IOException, InterruptedException {
        if (latestSessionService.isNewSessionAvailable()) {
            logger.info("New session found, beginning import.");
            try {
                // Import races
                logger.info("Starting to import races from OpenF1API...");
                List<Race> races = raceAPIService.fetchRacesFromOpenF1();
                logger.info("{} Races imported successfully.", races.size());

                // Import drivers
                logger.info("Starting to import drivers from OpenF1API...");
                List<Driver> drivers = driverAPIService.fetchDriversFromOpenF1(races);
                logger.info("{} Drivers imported successfully.", drivers.size());

                // Import laps
                logger.info("Starting to import laps from OpenF1API...");
                lapAPIService.fetchLapsFromOpenF1(races, drivers);
                logger.info("Laps imported successfully.");

                // Update latest session
                latestSessionService.updateLatestSession();
                logger.info("Updating latest session record");
            } catch (Exception e) {
                logger.error("Error occurred during OpenF1 import: ", e);
            }
        } else {
            logger.info("No new sessions found, data is up to date");
        }
    }
}
