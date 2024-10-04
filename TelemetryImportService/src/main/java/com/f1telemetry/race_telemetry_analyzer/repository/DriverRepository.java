package com.f1telemetry.race_telemetry_analyzer.repository;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link Driver} data in MongoDB.
 *
 * <p>This repository extends {@link MongoRepository} to provide CRUD operations for the {@link Driver} entity.
 * It also includes a custom method to find a driver by their full name.
 */
@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {
    /**
     * Finds a driver by their full name.
     *
     * @param name the full name of the driver
     * @return an {@link Optional} containing the {@link Driver} if found, or empty if not found
     */
    Optional<Driver> findByFullName(String name);
}
