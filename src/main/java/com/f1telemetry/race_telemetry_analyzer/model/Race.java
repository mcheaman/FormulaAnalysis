package com.f1telemetry.race_telemetry_analyzer.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "races") // This tells Spring that this class maps to the "races" collection in MongoDB
public class Race {

    @Id
    private String session_key;
    private Integer year;
    private String session_name;
    private String country_name;
    private String circuit_name;


    // Constructors
    public Race() {}

    public Race(String circuit_name, Integer year) {
        this.circuit_name = circuit_name;
        this.year = year;
    }

    public Race(String session_key, Integer year, String session_name, String country_name, String circuit_name) {
        this.session_key = session_key;
        this.year = year;
        this.session_name = session_name;
        this.country_name = country_name;
        this.circuit_name = circuit_name;
    }
}

