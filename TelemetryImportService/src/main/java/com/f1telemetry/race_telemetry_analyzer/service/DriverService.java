package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing driver data.
 *
 * <p>This service is responsible for performing CRUD operations on the {@link Driver} entity using the {@link DriverRepository}.
 */
@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    /**
     * Retrieves all drivers from the MongoDB database.
     *
     * @return a list of {@link Driver} entities
     */
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    /**
     * Retrieves a driver by their unique identifier.
     *
     * @param id the unique identifier of the driver
     * @return an {@link Optional} containing the driver if found, or empty if not
     */
    public Optional<Driver> getDriverById(String id) {
        return driverRepository.findById(id);
    }

    /**
     * Adds a new driver to the database.
     *
     * @param driver the {@link Driver} entity to save
     * @return the saved {@link Driver} entity
     */
    public Driver addDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    /**
     * Adds a list of drivers to the database.
     *
     * @param driversToAdd a list of {@link Driver} entities to save
     * @return a list of saved {@link Driver} entities
     */
    public List<Driver> addDrivers(List<Driver> driversToAdd) {
        return driverRepository.saveAll(driversToAdd);
    }

    /**
     * Deletes a driver from the database by their unique identifier.
     *
     * @param id the unique identifier of the driver
     */
    public void deleteDriver(String id) {
        driverRepository.deleteById(id);
    }

    /**
     * Retrieves a driver by their full name.
     *
     * @param driverName the full name of the driver
     * @return an {@link Optional} containing the driver if found, or empty if not
     */
    public Optional<Driver> getDriverByName(String driverName) {return driverRepository.findByFullName(driverName);}

}
