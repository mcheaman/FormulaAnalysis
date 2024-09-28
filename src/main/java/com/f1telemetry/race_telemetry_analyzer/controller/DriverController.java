package com.f1telemetry.race_telemetry_analyzer.controller;

import com.f1telemetry.race_telemetry_analyzer.controller.OpenF1API.DriverAPIController;
import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.service.DriverService;
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
    private static final Logger logger = LoggerFactory.getLogger(DriverAPIController.class);

    public DriverController(DriverService driverService) {
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
        Driver savedDriver = driverService.addDriver(driver);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);
    }

    @PutMapping("/{name}")
    public ResponseEntity<Driver> updateDriver(@PathVariable String name, @RequestBody Driver updatedDriverInfo) {
        try {
            Driver updatedDriver = driverService.addDriver(updatedDriverInfo);
            return ResponseEntity.ok(updatedDriver);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
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
