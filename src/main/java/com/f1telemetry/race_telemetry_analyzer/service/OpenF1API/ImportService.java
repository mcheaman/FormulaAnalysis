package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;


import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.model.Race;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    private final RaceAPIService raceAPIService;
    private final DriverAPIService driverAPIService;
    private final LapAPIService lapAPIService;
    private final LatestSessionService latestSessionService;
    private final PositionAPIService positionAPIService;

    public ImportService(RaceAPIService raceAPIService, DriverAPIService driverAPIService, LapAPIService lapAPIService, LatestSessionService latestSessionService, PositionAPIService positionAPIService) {
        this.raceAPIService = raceAPIService;
        this.driverAPIService = driverAPIService;
        this.lapAPIService = lapAPIService;
        this.latestSessionService = latestSessionService;
        this.positionAPIService = positionAPIService;
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
                logger.info("Laps import completed.");

                // Import positions
                logger.info("Starting to import positions from OpenF1API...");
                positionAPIService.fetchPositionsFromOpenF1(races, drivers);
                logger.info("Positions import completed.");

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
