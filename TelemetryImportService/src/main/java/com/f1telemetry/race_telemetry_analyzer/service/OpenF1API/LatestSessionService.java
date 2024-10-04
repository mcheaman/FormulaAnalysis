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

/**
 * Service for managing the latest session information.
 *
 * <p>This service is responsible for fetching the latest session data from the OpenF1 API,
 * checking for new session availability, and saving or updating the latest session in the database.
 */
@Service
public class LatestSessionService {

    @Autowired
    private LatestSessionRepository latestSessionRepository;

    private static final Logger logger = LoggerFactory.getLogger(LatestSessionService.class);

    private static final String LATEST_SESSION_API_URL = "https://api.openf1.org/v1/sessions?session_key=latest";

    /**
     * Fetches the latest session available from the OpenF1 API.
     *
     * @return a {@link JsonNode} representing the latest session
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
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

    /**
     * Retrieves the latest session stored in the MongoDB database.
     *
     * @return an {@link Optional} containing the latest session if found, or empty if not found
     */
    public Optional<LatestSession> getLatestSessionFromDB() {
        return latestSessionRepository.findById("latest_session_id");
    }

    /**
     * Checks if a new session is available by comparing the latest session in the database
     * with the latest session from the OpenF1 API.
     *
     * @return {@code true} if a new session is available, {@code false} otherwise
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
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

    /**
     * Add or update the latest session in the MongoDB database.
     *
     * @param latestSession the latest session to save or update
     */
    public void saveLatestSession(LatestSession latestSession) {
        latestSessionRepository.save(latestSession);
    }

    /**
     * Updates the latest session data after importing new telemetry.
     *
     * @return the updated {@link LatestSession} entity
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    public LatestSession updateLatestSession() throws IOException, InterruptedException {
        JsonNode latestSessionFromAPI = fetchLatestSessionFromOpenF1().get(0);
        // Parse the end date string into a ZonedDateTime (to handle the time zone)
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(latestSessionFromAPI.get("date_end").asText());
        // Convert to LocalDate and format to the desired pattern (yyyy-MM-dd)
        String formattedDate = zonedDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // Format an easily readable name for the latest session. ex. "Singapore Race 2024"
        String formattedName =
                latestSessionFromAPI.get("circuit_short_name").asText() + " "
                        + latestSessionFromAPI.get("session_name").asText() + " "
                        + latestSessionFromAPI.get("year").asText();
        LatestSession newLatestSession = new LatestSession(
                "latest_session_id",  // Static ID to ensure single document
                latestSessionFromAPI.get("session_key").asInt(),
                formattedDate,
                formattedName
        );
        saveLatestSession(newLatestSession);  // Save or update in the database
        return newLatestSession;
    }
}
