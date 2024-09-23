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
    private String country_code;
    private Integer driver_number;
    private String headshot_url;


    // Constructors
    public Driver() {}

    public Driver(String name, String team) {
        this.name = name;
        this.team = team;
    }

    public Driver(String name, String team, String country_code, Integer driver_number, String headshot_url) {
        this.name = name;
        this.team = team;
        this.country_code = country_code;
        this.driver_number = driver_number;
        this.headshot_url = headshot_url;
    }
}

