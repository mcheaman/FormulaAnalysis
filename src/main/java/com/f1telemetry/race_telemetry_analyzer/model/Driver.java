package com.f1telemetry.race_telemetry_analyzer.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "drivers") // This tells Spring that this class maps to the "drivers" collection in MongoDB
public class Driver {

    @Id
    private String id;
    private String name;
    private String team;


    // Constructors
    public Driver() {}

    public Driver(String name, String team) {
        this.name = name;
        this.team = team;
    }
}

