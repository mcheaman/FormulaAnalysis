package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Race;
import com.f1telemetry.race_telemetry_analyzer.repository.RaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RaceService {

    @Autowired
    private RaceRepository raceRepository;

    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    public Optional<Race> getRaceBySessionKey(String id) {
        return raceRepository.findById(id);
    }

    public Race addRace(Race race) {
        return raceRepository.save(race);
    }

    public void deleteRace(String session_key) {
        raceRepository.deleteById(session_key);
    }



}
