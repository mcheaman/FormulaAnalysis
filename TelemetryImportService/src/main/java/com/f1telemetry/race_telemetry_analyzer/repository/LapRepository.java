package com.f1telemetry.race_telemetry_analyzer.repository;

import com.f1telemetry.race_telemetry_analyzer.model.Lap;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing and managing {@link Lap} data in MongoDB.
 *
 * <p>This repository extends {@link MongoRepository} to provide CRUD operations for the {@link Lap} entity.
 * It includes a custom method to find laps by session key and driver number.
 */
@Repository
public interface LapRepository extends MongoRepository<Lap, String> {
    /**
     * Finds all laps for a given session and driver.
     *
     * @param sessionKey the session key identifying the race session
     * @param driverNumber the number of the driver
     * @return a list of {@link Lap} entities matching the session key and driver number
     */
    List<Lap> findBySessionKeyAndDriverNumber(Integer sessionKey, Integer driverNumber);
}
