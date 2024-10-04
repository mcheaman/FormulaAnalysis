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

/**
 * REST controller responsible for handling requests to fetch and import race data from the OpenF1 API.
 *
 * <p>This controller provides an endpoint for importing races from the OpenF1 API. The race data is fetched
 * via the {@link RaceAPIService} and returned in the HTTP response. If any error occurs during the fetching
 * process, the controller logs the error and returns an appropriate HTTP status with an error message.
 */
@RestController
@RequestMapping("/api/openf1/races")
public class RaceAPIController {

    private static final Logger logger = LoggerFactory.getLogger(RaceAPIController.class);

    @Autowired
    private RaceAPIService raceApiService;
    /**
     * Imports race data from the OpenF1 API and returns the imported races in the HTTP response.
     *
     * <p>This method calls {@link RaceAPIService#fetchRacesFromOpenF1()} to fetch race data from the OpenF1 API.
     * If the fetching is successful, the list of races is returned with an HTTP 200 OK status. If an error occurs,
     * such as an {@link IOException} or {@link InterruptedException}, the error is logged, and an HTTP 500 Internal
     * Server Error response is returned.
     *
     * @return A {@link ResponseEntity} containing the list of races if successful, or an HTTP 500 status
     *         with an error message if an exception is encountered.
     */
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

