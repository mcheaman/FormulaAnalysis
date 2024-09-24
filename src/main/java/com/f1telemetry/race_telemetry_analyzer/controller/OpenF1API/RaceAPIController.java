package com.f1telemetry.race_telemetry_analyzer.controller.OpenF1API;

import com.f1telemetry.race_telemetry_analyzer.model.Race;
import com.f1telemetry.race_telemetry_analyzer.service.OpenF1API.RaceAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/openf1/races")
public class RaceAPIController {

    private static final Logger logger = LoggerFactory.getLogger(RaceAPIController.class);

    @Autowired
    private RaceAPIService raceApiService;

    @GetMapping("/import-races")
    public ResponseEntity<List<Race>> importRacesFromOpenF1() {
        try {
            List<Race> races = raceApiService.fetchRacesFromOpenF1();
            return ResponseEntity.ok(races);
        } catch (IOException | InterruptedException e) {
            logger.error("Error occurred while fetching races from OpenF1 API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

