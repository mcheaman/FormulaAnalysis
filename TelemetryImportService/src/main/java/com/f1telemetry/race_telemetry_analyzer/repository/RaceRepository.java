package com.f1telemetry.race_telemetry_analyzer.repository;

import com.f1telemetry.race_telemetry_analyzer.model.Race;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing {@link Race} data in MongoDB.
 *
 * <p>This repository extends {@link MongoRepository} to provide CRUD operations for the {@link Race} entity.
 * It can be extended with custom query methods as needed.
 */
@Repository
public interface RaceRepository extends MongoRepository<Race, String> {
}
