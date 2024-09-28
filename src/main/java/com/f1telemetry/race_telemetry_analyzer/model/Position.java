package com.f1telemetry.race_telemetry_analyzer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
