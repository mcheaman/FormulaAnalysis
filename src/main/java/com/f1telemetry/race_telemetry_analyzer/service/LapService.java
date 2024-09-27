package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Lap;
import com.f1telemetry.race_telemetry_analyzer.repository.LapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LapService {

    @Autowired
    private LapRepository lapRepository;

    // Fetch all laps from MongoDB
    public List<Lap> getAllLaps() {
        return lapRepository.findAll();
    }

    // Fetch laps by session key and driver number
    public List<Lap> getLapsBySessionAndDriver(Integer sessionKey, Integer driverNumber) {
        return lapRepository.findBySessionKeyAndDriverNumber(sessionKey, driverNumber);
    }

    // Add a list of laps to the database
    public void addLaps(List<Lap> laps) {
        lapRepository.saveAll(laps);
    }
}
