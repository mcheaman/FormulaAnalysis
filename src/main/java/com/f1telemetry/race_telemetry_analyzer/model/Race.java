package com.f1telemetry.race_telemetry_analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a race session.
 *
 * <p>This class is mapped to the "races" collection in MongoDB using Spring Data's {@code @Document} annotation.
 * Each race is identified by a session key and contains other details like the year, session name, country, and circuit name.
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "races") // This tells Spring that this class maps to the "races" collection in MongoDB
public class Race {

    @Id
    private Integer sessionKey;
    private Integer year;
    private String sessionName;
    private String countryName;
    private String circuitName;

}

