package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Race;
import com.f1telemetry.race_telemetry_analyzer.repository.RaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RaceService {

    @Autowired
    private RaceRepository raceRepository;

    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    // Method to return a list of session keys from all races
    public List<String> getAllSessionKeys() {
        // Retrieve all races and map the session_key from each race to a list
        return raceRepository.findAll().stream()
                .map(Race::getSession_key)  // Extract the session_key from each Race object
                .collect(Collectors.toList()); // Collect the session_key values into a List
    }

    public Optional<Race> getRaceBySessionKey(String id) {
        return raceRepository.findById(id);
    }

    public Race addRace(Race race) {
        return raceRepository.save(race);
    }

    public List<Race> addRaces(List<Race> racesToAdd) {
        return raceRepository.saveAll(racesToAdd);
    }

    public void deleteRace(String session_key) {
        raceRepository.deleteById(session_key);
    }



}
