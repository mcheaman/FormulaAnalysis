package com.f1telemetry.race_telemetry_analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents the latest session in the system.
 *
 * <p>This class is mapped to the "latest_session" collection in MongoDB using Spring Data's {@code @Document} annotation.
 * It holds details about the latest race session, such as the session key, end date, and name.
 * There is only one document in this collection with a fixed ID.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "latest_session") // MongoDB collection for latest session
public class LatestSession {

    @Id
    private String id = "latest_session_id";  // Single document for latest session
    private Integer sessionKey;
    private String sessionEndDate;
    private String sessionName;
}
