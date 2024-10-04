package com.f1telemetry.race_telemetry_analyzer.controller;

import com.f1telemetry.race_telemetry_analyzer.model.Lap;
import com.f1telemetry.race_telemetry_analyzer.service.LapService;
import com.f1telemetry.race_telemetry_analyzer.service.OpenF1API.LapAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * REST controller for managing lap data.
 *
 * <p>This controller provides endpoints to retrieve lap data. It interacts with
 * the {@link LapService} for retrieving lap data from the local database and the {@link LapAPIService}
 * for fetching lap data from the OpenF1 API.
 */
@RestController
@RequestMapping("/api/laps")
public class LapController {

    @Autowired
    private LapService lapService;

    @Autowired
    private LapAPIService lapAPIService;

    /**
     * Retrieves all lap data from the database.
     *
     * <p>This method returns a list of all laps currently stored in the system.
     *
     * @return a list of {@link Lap} entities
     */
    @GetMapping
    public List<Lap> getAllLaps() {
        return lapService.getAllLaps();
    }

    /**
     * Retrieves lap data for a specific session and driver.
     *
     * <p>This method retrieves laps based on the session key and driver number, returning all laps
     * for the specified session and driver.
     *
     * @param sessionKey the session key identifying the race session
     * @param driverNumber the driver number to filter lap data by
     * @return a list of {@link Lap} entities for the specified session and driver
     */
    @GetMapping("/session/{sessionKey}/driver/{driverNumber}")
    public List<Lap> getLapsBySessionAndDriver(@PathVariable Integer sessionKey, @PathVariable Integer driverNumber) {
        return lapService.getLapsBySessionAndDriver(sessionKey, driverNumber);
    }

}
