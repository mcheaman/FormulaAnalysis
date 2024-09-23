package com.f1telemetry.race_telemetry_analyzer.controller;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.service.DriverService;
import com.f1telemetry.race_telemetry_analyzer.service.OpenF1ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {


    private final DriverService driverService;
    private static final Logger logger = LoggerFactory.getLogger(OpenF1ApiController.class);

    public DriverController(DriverService driverService, OpenF1ApiService openF1ApiService) {
        this.driverService = driverService;
    }

    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable String id) {
        Optional<Driver> driver = driverService.getDriverById(id);
        return driver.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Driver> addDriver(@Validated @RequestBody Driver driver) {
        logger.info("Adding a new driver: {}", driver.getName());
        Driver savedDriver = driverService.addDriver(driver);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable String id) {
        logger.info("Deleting driver with id: {}", id);
        Optional<Driver> driver = driverService.getDriverById(id);
        if (driver.isEmpty()) {
            logger.warn("Driver with id {} not found", id);
            return ResponseEntity.notFound().build();
        }

        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}
