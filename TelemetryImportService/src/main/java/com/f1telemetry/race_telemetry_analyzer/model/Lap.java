package com.f1telemetry.race_telemetry_analyzer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a lap in a race.
 *
 * <p>This class is mapped to the "laps" collection in MongoDB using Spring Data's {@code @Document} annotation.
 * Each lap has an identifier composed of the session key, driver number, and lap number.
 * It includes information such as lap duration, sector times, speed trap speed, and whether it was a pit out lap.
 */
@Setter
@Getter
@NoArgsConstructor
@Document(collection = "laps") // This tells Spring that this class maps to the "laps" collection in MongoDB
public class Lap {

    @Id
    private String id; // Composite key in string format (sessionKey_driverNumber_lapNumber)

    private Integer sessionKey;
    private Integer driverNumber;
    private Integer lapNumber;
    private Float lapDuration;
    private Float sector1;
    private Float sector2;
    private Float sector3;
    private Integer speedTrapSpeed;
    private Boolean isPitOutLap;

    public Lap(Integer sessionKey, Integer driverNumber, Integer lapNumber, Float lapDuration, Float sector1, Float sector2, Float sector3,Boolean isPitOutLap, Integer speedTrapSpeed) {
        this.id = sessionKey + "_" + driverNumber + "_" + lapNumber; // Create composite key
        this.sessionKey = sessionKey;
        this.driverNumber = driverNumber;
        this.lapNumber = lapNumber;
        this.lapDuration = lapDuration;
        this.sector1 = sector1;
        this.sector2 = sector2;
        this.sector3 = sector3;
        this.isPitOutLap = isPitOutLap;
        this.speedTrapSpeed = speedTrapSpeed;
    }
}
