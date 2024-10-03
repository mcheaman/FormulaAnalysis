package com.f1telemetry.race_telemetry_analyzer.repository;

import com.f1telemetry.race_telemetry_analyzer.model.Position;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing {@link Position} data in MongoDB.
 *
 * <p>This repository extends {@link MongoRepository} to provide CRUD operations for the {@link Position} entity.
 * It includes a custom method to find a position by session key and driver number.
 */
@Repository
public interface PositionRepository extends MongoRepository<Position, String> {
    /**
     * Finds the position of a driver for a specific session.
     *
     * @param sessionKey the session key identifying the race session
     * @param driverNumber the number of the driver
     * @return the {@link Position} entity matching the session key and driver number
     */
    Position findBySessionKeyAndDriverNumber(Integer sessionKey, Integer driverNumber);
}
