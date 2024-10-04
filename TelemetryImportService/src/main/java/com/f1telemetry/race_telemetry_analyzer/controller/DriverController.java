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

/**
 * REST controller for managing driver data in the system.
 *
 * <p>This controller provides endpoints for CRUD (Create, Read, Update, Delete) operations
 * on driver entities. It interacts with the {@link DriverService} to handle data persistence
 * and business logic related to drivers.
 */
@RestController
@RequestMapping("/api/drivers")
public class DriverController {


    private final DriverService driverService;
    private static final Logger logger = LoggerFactory.getLogger(DriverAPIController.class);

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    /**
     * Retrieves all drivers from the system.
     *
     * <p>This method returns a list of all drivers currently stored in the database.
     *
     * @return a list of {@link Driver} entities
     */
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    /**
     * Retrieves a driver by their unique identifier.
     *
     * <p>If the driver is found, their information is returned with an HTTP 200 OK status.
     * If the driver is not found, the method returns a 404 Not Found status.
     *
     * @param id the unique identifier of the driver
     * @return a {@link ResponseEntity} containing the driver if found, or 404 if not
     */
    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable String id) {
        Optional<Driver> driver = driverService.getDriverById(id);
        return driver.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Adds a new driver to the system.
     *
     * <p>This method creates a new driver and returns the saved entity with a 201 Created status.
     *
     * @param driver the driver to be added, provided in the request body
     * @return a {@link ResponseEntity} containing the newly created driver
     */
    @PostMapping
    public ResponseEntity<Driver> addDriver(@Validated @RequestBody Driver driver) {
        Driver savedDriver = driverService.addDriver(driver);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);
    }

    /**
     * Deletes a driver by their unique identifier.
     *
     * <p>If the driver is found, they are deleted from the system, and the method returns a 204 No Content status.
     * If the driver is not found, the method returns a 404 Not Found status.
     *
     * @param id the unique identifier of the driver to delete
     * @return a {@link ResponseEntity} with a 204 status if the deletion was successful, or 404 if the driver was not found
     */
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
