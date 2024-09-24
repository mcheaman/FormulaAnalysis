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

@RestController
@RequestMapping("/api/openf1/drivers")
public class DriverAPIController {

    private static final Logger logger = LoggerFactory.getLogger(DriverAPIController.class);

    @Autowired
    private DriverAPIService driverAPIService;

    @GetMapping("/import-drivers")
    public ResponseEntity<List<Driver>> importDriversFromOpenF1() {
        try {
            List<Driver> drivers = driverAPIService.fetchDriversFromOpenF1();
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

