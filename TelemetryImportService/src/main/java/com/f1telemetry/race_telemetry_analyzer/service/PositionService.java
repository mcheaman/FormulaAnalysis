package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Position;
import com.f1telemetry.race_telemetry_analyzer.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing position data.
 *
 * <p>This service is responsible for performing CRUD operations on the {@link Position} entity using the {@link PositionRepository}.
 */
@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    /**
     * Retrieves all positions from the MongoDB database.
     *
     * @return a list of {@link Position} entities
     */
    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    /**
     * Retrieves the position of a driver for a specific session.
     *
     * @param sessionKey the session key identifying the race session
     * @param driverNumber the driver's number
     * @return the {@link Position} entity for the specified session and driver, or {@code null} if not found
     */
    public Position getPositionBySessionAndDriver(Integer sessionKey, Integer driverNumber) {
        return positionRepository.findBySessionKeyAndDriverNumber(sessionKey, driverNumber);
    }

    /**
     * Adds a list of positions to the database.
     *
     * @param positions a list of {@link Position} entities to save
     */
    public void addPositions(List<Position> positions) {
        positionRepository.saveAll(positions);
    }
}
