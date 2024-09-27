package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;

import com.f1telemetry.race_telemetry_analyzer.model.LatestSession;
import com.f1telemetry.race_telemetry_analyzer.repository.LatestSessionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class LatestSessionService {

    @Autowired
    private LatestSessionRepository latestSessionRepository;

    private static final Logger logger = LoggerFactory.getLogger(LatestSessionService.class);

    private static final String LATEST_SESSION_API_URL = "https://api.openf1.org/v1/sessions?session_key=latest";

    // Fetch the latest session from OpenF1 API
    public JsonNode fetchLatestSessionFromOpenF1() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LATEST_SESSION_API_URL))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response.body());  // Return JSON node of latest session
        } else {
            throw new IOException("Failed to fetch the latest session. Response code: " + response.statusCode());
        }
    }

    // Get the latest session saved in the MongoDB database
    public Optional<LatestSession> getLatestSessionFromDB() {
        return latestSessionRepository.findById("latest_session_id");
    }

    // Check if new data is available by comparing OpenF1 latest session with the latest imported session
    public boolean isNewSessionAvailable() throws IOException, InterruptedException {
        // Fetch the latest session from OpenF1 API
        JsonNode latestSessionFromAPI = fetchLatestSessionFromOpenF1().get(0);
        Integer latestSessionKeyFromAPI = latestSessionFromAPI.get("session_key").asInt();

        // Get the latest session saved in the database
        Optional<LatestSession> latestSessionFromDB = getLatestSessionFromDB();

        // Compare the latest session keys
        if (latestSessionFromDB.isPresent()) {
            Integer latestSessionKeyFromDB = latestSessionFromDB.get().getSessionKey();

            // If the session keys are different, new data is available
            if (!latestSessionKeyFromAPI.equals(latestSessionKeyFromDB)) {
                return true;  // New session available
            }
        } else {
            return true;
        }

        return false;  // No new session
    }

    // Save or update the latest session in the MongoDB database
    public void saveLatestSession(LatestSession latestSession) {
        latestSessionRepository.save(latestSession);
    }
    // Method to update the database with the latest session after import
    public void updateLatestSession() throws IOException, InterruptedException {
        JsonNode latestSessionFromAPI = fetchLatestSessionFromOpenF1().get(0);
        // Parse the end date string into a ZonedDateTime (to handle the time zone)
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(latestSessionFromAPI.get("date_end").asText());
        // Convert to LocalDate and format to the desired pattern (yyyy-MM-dd)
        String formattedDate = zonedDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LatestSession newLatestSession = new LatestSession(
                "latest_session_id",  // Static ID to ensure single document
                latestSessionFromAPI.get("session_key").asInt(),
                formattedDate,
                latestSessionFromAPI.get("session_name").asText()
        );
        saveLatestSession(newLatestSession);  // Save or update in the database
    }
}
