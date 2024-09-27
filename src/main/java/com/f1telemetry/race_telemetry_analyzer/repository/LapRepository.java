package com.f1telemetry.race_telemetry_analyzer.repository;

import com.f1telemetry.race_telemetry_analyzer.model.Lap;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LapRepository extends MongoRepository<Lap, String> {

    List<Lap> findBySessionKeyAndDriverNumber(Integer sessionKey, Integer driverNumber);
}
