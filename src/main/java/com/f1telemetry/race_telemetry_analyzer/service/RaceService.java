package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Race;
import com.f1telemetry.race_telemetry_analyzer.repository.RaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing race data.
 *
 * <p>This service is responsible for performing CRUD operations on the {@link Race} entity using the {@link RaceRepository}.
 */
@Service
public class RaceService {

    @Autowired
    private RaceRepository raceRepository;

    /**
     * Retrieves all races from the MongoDB database.
     *
     * @return a list of {@link Race} entities
     */
    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    /**
     * Retrieves a race by its session key.
     *
     * @param id the session key of the race
     * @return an {@link Optional} containing the race if found, or empty if not
     */
    public Optional<Race> getRaceBySessionKey(String id) {
        return raceRepository.findById(id);
    }

    /**
     * Adds a new race to the database.
     *
     * @param race the {@link Race} entity to save
     * @return the saved {@link Race} entity
     */
    public Race addRace(Race race) {
        return raceRepository.save(race);
    }

    /**
     * Adds a list of races to the database.
     *
     * @param racesToAdd a list of {@link Race} entities to save
     * @return a list of saved {@link Race} entities
     */
    public List<Race> addRaces(List<Race> racesToAdd) {
        return raceRepository.saveAll(racesToAdd);
    }

    /**
     * Deletes a race from the database by its session key.
     *
     * @param sessionKey the session key of the race
     */
    public void deleteRace(String sessionKey) {
        raceRepository.deleteById(sessionKey);
    }

    /**
     * Retrieves the session keys of all races.
     *
     * @return a list of session keys for all races
     */
    public List<Integer> getAllSessionKeys() {
        // Retrieve all races and map the session_key from each race to a list
        return raceRepository.findAll().stream()
                .map(Race::getSessionKey)  // Extract the session_key from each Race object
                .collect(Collectors.toList()); // Collect the session_key values into a List
    }
}
