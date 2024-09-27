package com.f1telemetry.race_telemetry_analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
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

    @Override
    public String toString() {
        return "Driver{" +
                "fullName='" + fullName + '\'' +
                ", broadcastName='" + broadcastName + '\'' +
                ", team='" + team + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", driverNumber=" + driverNumber +
                ", headshotUrl='" + headshotUrl + '\'' +
                '}';
    }
}

