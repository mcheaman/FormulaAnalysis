package com.f1telemetry.race_telemetry_analyzer.controller.OpenF1API;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.service.OpenF1API.DriverAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * REST controller responsible for handling requests to fetch and import driver data from the OpenF1 API.
 *
 * <p>This controller exposes an endpoint for importing drivers from the OpenF1 API. The driver data is
 * fetched by calling the {@link DriverAPIService} and returned in the response. If an error occurs during
 * the fetching process, the controller returns an appropriate HTTP status code with an error message.
 */
@RestController
@RequestMapping("/api/openf1/drivers")
public class DriverAPIController {

    private static final Logger logger = LoggerFactory.getLogger(DriverAPIController.class);

    @Autowired
    private DriverAPIService driverAPIService;

    /**
     * Imports driver data from the OpenF1 API and returns the imported drivers in the response.
     *
     * <p>This method fetches the list of drivers from the OpenF1 API using the {@link DriverAPIService}.
     * If the operation is successful, it returns the list of drivers with an HTTP status of 200 OK.
     * If an error occurs during the fetching process (e.g., network issue or interruption), it logs the
     * error and returns a 500 Internal Server Error response.
     *
     * @return A {@link ResponseEntity} containing the list of drivers if successful, or an error response
     *         with an HTTP 500 status if an exception is encountered.
     */
    @GetMapping("/import-drivers")
    public ResponseEntity<List<Driver>> importDriversFromOpenF1() {
        try {
            List<Driver> drivers = driverAPIService.fetchAllDriversFromOpenF1();
            return ResponseEntity.ok(drivers);
        } catch (IOException | InterruptedException e) {
            logger.error("Error occurred while fetching drivers from OpenF1 API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

