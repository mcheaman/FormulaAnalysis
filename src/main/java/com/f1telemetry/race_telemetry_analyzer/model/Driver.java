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
    private String fullName;
    private String broadcastName;
    private String team;
    private String countryCode;
    private Integer driverNumber;
    private String headshotUrl;


    // Constructors
    public Driver() {}

    public Driver(String fullName, String team) {
        this.fullName = fullName;
        this.team = team;
    }

    public Driver(String fullName, String broadcastName, String team, String countryCode, Integer driverNumber, String headshotUrl) {
        this.fullName = fullName;
        this.broadcastName = broadcastName;
        this.team = team;
        this.countryCode = countryCode;
        this.driverNumber = driverNumber;
        this.headshotUrl = headshotUrl;
    }
}

