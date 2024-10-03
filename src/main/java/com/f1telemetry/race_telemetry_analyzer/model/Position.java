package com.f1telemetry.race_telemetry_analyzer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents the position of a driver in a race session.
 *
 * <p>This class is mapped to the "position" collection in MongoDB using Spring Data's {@code @Document} annotation.
 * Each position has a composite key formed from the session key and driver number, along with the driver's position in the race.
 */
@Setter
@Getter
@NoArgsConstructor
@Document(collection = "position") // This tells Spring that this class maps to the "laps" collection in MongoDB
public class Position {

    @Id
    private String id; // Composite key in string format (sessionKey_driverNumber)

    private Integer sessionKey;
    private Integer driverNumber;
    private Integer position;


    public Position(Integer sessionKey, Integer driverNumber, Integer position) {
        this.id = sessionKey + "_" + driverNumber; // Create composite key
        this.sessionKey = sessionKey;
        this.driverNumber = driverNumber;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id='" + id + '\'' +
                ", sessionKey=" + sessionKey +
                ", driverNumber=" + driverNumber +
                ", position=" + position +
                '}';
    }
}
