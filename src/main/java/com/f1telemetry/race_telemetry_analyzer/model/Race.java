package com.f1telemetry.race_telemetry_analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

