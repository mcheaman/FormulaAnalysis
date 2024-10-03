package com.f1telemetry.race_telemetry_analyzer.controller;

import com.f1telemetry.race_telemetry_analyzer.model.Race;
import com.f1telemetry.race_telemetry_analyzer.service.RaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing race data.
 *
 * <p>This controller provides endpoints for creating, retrieving, and deleting race data. It interacts
 * with the {@link RaceService} to perform CRUD (Create, Read, Update, Delete) operations on race entities.
 */
@RestController
@RequestMapping("/api/races")
public class RaceController {


    private final RaceService raceService;
    private static final Logger logger = LoggerFactory.getLogger(RaceController.class);

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    /**
     * Retrieves all races from the system.
     *
     * <p>This method returns a list of all races currently stored in the database.
     *
     * @return a list of {@link Race} entities
     */
    @GetMapping
    public List<Race> getAllRaces() {
        return raceService.getAllRaces();
    }

    /**
     * Retrieves a race by its session key.
     *
     * <p>This method fetches a race based on its session key. If the race is found, it returns the race data
     * with an HTTP 200 OK status. If the race is not found, the method returns a 404 Not Found status.
     *
     * @param session_key the session key of the race to retrieve
     * @return a {@link ResponseEntity} containing the race if found, or 404 Not Found if not
     */
    @GetMapping("/{session_key}")
    public ResponseEntity<Race> getRaceBySessionKey(@PathVariable String session_key) {
        Optional<Race> race = raceService.getRaceBySessionKey(session_key);
        return race.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Adds a new race to the system.
     *
     * <p>This method creates a new race entity and stores it in the database. The created race is returned
     * with an HTTP 201 Created status.
     *
     * @param race the race data to be added, provided in the request body
     * @return a {@link ResponseEntity} containing the newly created race
     */
    @PostMapping
    public ResponseEntity<Race> addRace(@Validated @RequestBody Race race) {
        logger.info("Adding a new race: {}", race.getSessionKey());
        Race savedRace = raceService.addRace(race);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRace);
    }

    /**
     * Deletes a race by its session key.
     *
     * <p>If the race is found, it is deleted from the system, and the method returns a 204 No Content status.
     * If the race is not found, the method returns a 404 Not Found status.
     *
     * @param session_key the session key of the race to delete
     * @return a {@link ResponseEntity} with a 204 status if the deletion was successful, or 404 if the race was not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRace(@PathVariable String session_key) {
        logger.info("Deleting race with session_key: {}", session_key);
        Optional<Race> race = raceService.getRaceBySessionKey(session_key);
        if (race.isEmpty()) {
            logger.warn("Race with session_key {} not found", session_key);
            return ResponseEntity.notFound().build();
        }

        raceService.deleteRace(session_key);
        return ResponseEntity.noContent().build();
    }
}
