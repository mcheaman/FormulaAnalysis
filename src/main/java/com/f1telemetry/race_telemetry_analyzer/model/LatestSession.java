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
@Document(collection = "latest_session") // MongoDB collection for latest session
public class LatestSession {

    @Id
    private String id = "latest_session_id";  // Single document for latest session
    private Integer sessionKey;
    private String sessionName;
}
