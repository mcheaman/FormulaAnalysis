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

@RestController
@RequestMapping("/api/races")
public class RaceController {


    private final RaceService raceService;
    private static final Logger logger = LoggerFactory.getLogger(RaceController.class);

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping
    public List<Race> getAllRaces() {
        return raceService.getAllRaces();
    }

    @GetMapping("/{session_key}")
    public ResponseEntity<Race> getRaceBySessionKey(@PathVariable String session_key) {
        Optional<Race> race = raceService.getRaceBySessionKey(session_key);
        return race.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Race> addRace(@Validated @RequestBody Race race) {
        logger.info("Adding a new race: {}", race.getSessionKey());
        Race savedRace = raceService.addRace(race);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRace);
    }

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
