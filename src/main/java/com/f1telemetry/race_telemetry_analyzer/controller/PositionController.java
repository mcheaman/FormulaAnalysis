package com.f1telemetry.race_telemetry_analyzer.controller;

import com.f1telemetry.race_telemetry_analyzer.model.Position;
import com.f1telemetry.race_telemetry_analyzer.service.PositionService;
import com.f1telemetry.race_telemetry_analyzer.service.OpenF1API.PositionAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * REST controller for managing position data.
 *
 * <p>This controller provides endpoints to retrieve position data. It interacts with
 * the {@link PositionService} for accessing position data from the local database and the {@link PositionAPIService}
 * for importing position data from the OpenF1 API.
 */
@RestController
@RequestMapping("/api/positions")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @Autowired
    private PositionAPIService positionAPIService;

    /**
     * Retrieves all positions from the system.
     *
     * <p>This method returns a list of all positions currently stored in the database.
     *
     * @return a list of {@link Position} entities
     */
    @GetMapping
    public List<Position> getAllPositions() {
        return positionService.getAllPositions();
    }

    /**
     * Retrieves a position for a specific session and driver.
     *
     * <p>This method fetches the position based on the session key and driver number, returning the position
     * if found, or a 404 Not Found response if no position exists for the specified session and driver.
     *
     * @param sessionKey the session key identifying the race session
     * @param driverNumber the driver number to fetch the position for
     * @return a {@link ResponseEntity} containing the position if found, or a 404 Not Found status if not
     */
    @GetMapping("/session/{sessionKey}/driver/{driverNumber}")
    public ResponseEntity<Position> getPositionBySessionAndDriver(@PathVariable Integer sessionKey, @PathVariable Integer driverNumber) {
        Position position = positionService.getPositionBySessionAndDriver(sessionKey, driverNumber);
        return position != null ? ResponseEntity.ok(position) : ResponseEntity.notFound().build();
    }

}
