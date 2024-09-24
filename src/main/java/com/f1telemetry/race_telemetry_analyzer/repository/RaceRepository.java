package com.f1telemetry.race_telemetry_analyzer.repository;

import com.f1telemetry.race_telemetry_analyzer.model.Race;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RaceRepository extends MongoRepository<Race, String> {

}
