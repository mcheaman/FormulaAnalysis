package com.f1telemetry.race_telemetry_analyzer.repository;

import com.f1telemetry.race_telemetry_analyzer.model.Position;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends MongoRepository<Position, String> {
    Position findBySessionKeyAndDriverNumber(Integer sessionKey, Integer driverNumber);
}
