package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Lap;
import com.f1telemetry.race_telemetry_analyzer.repository.LapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing lap data.
 *
 * <p>This service is responsible for performing CRUD operations on the {@link Lap} entity using the {@link LapRepository}.
 */
@Service
public class LapService {

    @Autowired
    private LapRepository lapRepository;

    /**
     * Retrieves all laps from the MongoDB database.
     *
     * @return a list of {@link Lap} entities
     */
    public List<Lap> getAllLaps() {
        return lapRepository.findAll();
    }

    /**
     * Retrieves laps for a specific session and driver.
     *
     * @param sessionKey the session key identifying the race session
     * @param driverNumber the driver's number
     * @return a list of {@link Lap} entities for the specified session and driver
     */
    public List<Lap> getLapsBySessionAndDriver(Integer sessionKey, Integer driverNumber) {
        return lapRepository.findBySessionKeyAndDriverNumber(sessionKey, driverNumber);
    }

    /**
     * Adds a list of laps to the database.
     *
     * @param laps a list of {@link Lap} entities to save
     */
    public void addLaps(List<Lap> laps) {
        lapRepository.saveAll(laps);
    }
}
