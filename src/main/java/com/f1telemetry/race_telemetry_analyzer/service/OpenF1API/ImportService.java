package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;


import com.f1telemetry.race_telemetry_analyzer.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Service for importing race telemetry data from the OpenF1 API.
 *
 * <p>This service is responsible for orchestrating the import of races, drivers, laps, and positions,
 * and updating the latest session information in the system.
 */
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


    /**
     * Import race telemetry from the OpenF1 API if a new session is available.
     * Check if new race telemetry is available using {@code latestSessionService}.
     * If so, import new races, drivers, laps, and positions.
     * Finally, updates the latest session to avoid duplicate imports.
     * Triggered on application startup from config/StartupImportConfig
     *
     * @throws IOException If an I/O error occurs during data fetching.
     * @throws InterruptedException If the thread is interrupted during data fetching.
     */
    public void importData() throws IOException, InterruptedException {
        if (latestSessionService.isNewSessionAvailable()) {
            logger.info("New session found, beginning import.");
            try {
                // Import races
                logger.info("Beginning race import from OpenF1API...");
                List<Race> races = raceAPIService.fetchRacesFromOpenF1();
                logger.info("{} races imported.", races.size());

                // Import drivers
                logger.info("Beginning driver import from OpenF1API...");
                List<Driver> drivers = driverAPIService.fetchDriversFromOpenF1(races);
                logger.info("{} drivers imported.", drivers.size());

                // Import laps
                logger.info("Beginning lap import from OpenF1API...");
                List<Lap> laps = lapAPIService.fetchLapsFromOpenF1(races, drivers);
                logger.info("{} laps imported.", laps.size());

                // Import positions
                logger.info("Beginning position import from OpenF1API...");
                List<Position> positions = positionAPIService.fetchPositionsFromOpenF1(races, drivers);
                logger.info("{} positions imported.", positions.size());

                // Update latest session
                LatestSession latestSession = latestSessionService.updateLatestSession();
                logger.info("Latest session updated to {}", latestSession.getSessionName());
            } catch (Exception e) {
                logger.error("Error occurred during OpenF1 import: ", e);
            }
        } else {
            logger.info("No new sessions found, data is up to date");
        }
        logger.info("OpenF1 import completed");
    }
}
