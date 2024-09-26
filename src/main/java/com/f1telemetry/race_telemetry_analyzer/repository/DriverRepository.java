package com.f1telemetry.race_telemetry_analyzer.repository;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {
    // Custom query methods will be added here when needed, e.g., findByName, findByTeam, etc.
    Optional<Driver> findByFullName(String name);
}
