package com.f1telemetry.race_telemetry_analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a driver in the racing context.
 *
 * <p>This class is mapped to the "drivers" collection in MongoDB using Spring Data's {@code @Document} annotation.
 * Each driver has a unique {@code fullName} that serves as the identifier and contains other related data such as
 * the driver's team, country code, driver number, and broadcast name.
 */
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

