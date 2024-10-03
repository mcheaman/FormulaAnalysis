package com.f1telemetry.race_telemetry_analyzer.repository;

import com.f1telemetry.race_telemetry_analyzer.model.LatestSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing {@link LatestSession} data in MongoDB.
 *
 * <p>This repository extends {@link MongoRepository} to provide CRUD operations for the {@link LatestSession} entity.
 * It is primarily used to store and retrieve the latest session information.
 */
@Repository
public interface LatestSessionRepository extends MongoRepository<LatestSession, String> {
}
