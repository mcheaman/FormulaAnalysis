package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Position;
import com.f1telemetry.race_telemetry_analyzer.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public Position getPositionBySessionAndDriver(Integer sessionKey, Integer driverNumber) {
        return positionRepository.findBySessionKeyAndDriverNumber(sessionKey, driverNumber);
    }

    public void addPositions(List<Position> positions) {
        positionRepository.saveAll(positions);
    }
}
