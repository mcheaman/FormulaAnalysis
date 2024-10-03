package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;

import com.f1telemetry.race_telemetry_analyzer.model.Position;
import com.f1telemetry.race_telemetry_analyzer.model.Race;
import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.service.DriverService;
import com.f1telemetry.race_telemetry_analyzer.service.PositionService;
import com.f1telemetry.race_telemetry_analyzer.service.RaceService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for interacting with the OpenF1 API to fetch and manage position data.
 *
 * <p>This service is responsible for sending requests to the OpenF1 API to retrieve position data
 * for specific sessions and drivers, handling retries in case of rate limits.
 */
@Service
public class PositionAPIService {

    @Autowired
    private PositionService positionService;
    @Autowired
    private RaceService raceService;
    @Autowired
    private DriverService driverService;

    private static final String POSITION_API_BASE_URL = "https://api.openf1.org/v1/position?session_key=";
    private static final Logger logger = LoggerFactory.getLogger(PositionAPIService.class);

    private static final int MAX_RETRIES = 5;  // Max number of retries on 429 errors
    private static final int INITIAL_DELAY_MS = 1000;  // Initial delay (in milliseconds) for retry after 429 errors

    /**
     * Fetches positions from the OpenF1 API for multiple races and drivers.
     *
     * @param races a list of races to fetch positions for
     * @param drivers a list of drivers to fetch positions for
     * @return a list of positions imported from OpenF1
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    public List<Position> fetchPositionsFromOpenF1(List<Race> races, List<Driver> drivers) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        List<Position> allPositions = new ArrayList<>();  // This will store all positions

        // Iterate over every race
        for (Race race : races) {
            Integer sessionKey = race.getSessionKey();

            // For each race, iterate over every driver
            for (Driver driver : drivers) {
                Integer driverNumber = driver.getDriverNumber();
                String positionURL = POSITION_API_BASE_URL + sessionKey + "&driver_number=" + driverNumber;

                // Make synchronous API call
                Position position = fetchPositionForDriverAndSession(client, positionURL, sessionKey, driverNumber);
                if (position != null) {
                    allPositions.add(position);  // Add to list if position is valid
                }
            }
        }

        // Persist positions to MongoDB
        if (!allPositions.isEmpty()) {
            logger.debug("{} positions for {} drivers in {} races to be persisted to MongoDB", allPositions.size(), drivers.size(), races.size());
            positionService.addPositions(allPositions);  // Save positions to MongoDB
        }

        return allPositions;
    }

    /**
     * Fetches the position of a specific driver in a race session.
     *
     * @param client the HTTP client
     * @param positionURL the URL to fetch position data
     * @param sessionKey the session key of the race
     * @param driverNumber the number of the driver
     * @return the {@link Position} of the driver in the session, or {@code null} if not found
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    private Position fetchPositionForDriverAndSession(HttpClient client, String positionURL, Integer sessionKey, Integer driverNumber) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(positionURL))
                .header("Accept", "application/json")
                .build();

        // Call the API synchronously and handle 429 errors with retries
        int retryCount = 0;
        int delayMs = INITIAL_DELAY_MS;

        while (retryCount < MAX_RETRIES) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Process the successful response
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.body());
                // Check if rootNode is an array
                if (rootNode.isArray() && !rootNode.isEmpty()) {
                    // In order to get the finishing positions, get the last object in the array
                    JsonNode lastObject = rootNode.get(rootNode.size() - 1);
                    Integer position = lastObject.get("position").asInt();

                    // Create and return Position object
                    return new Position(sessionKey, driverNumber, position);
                } else {
                    logger.debug("No valid positions found for session {} and driver {}.", sessionKey, driverNumber);
                    return null;
                }
            } else if (response.statusCode() == 429) {
                // Handle rate limiting (429) by delaying and retrying
                Optional<String> retryAfter = response.headers().firstValue("Retry-After");
                int retryDelay = retryAfter.map(Integer::parseInt).orElse(delayMs);  // Use "Retry-After" header if available

                logger.warn("Rate limit exceeded for session {} and driver {}. Retrying after {} ms", sessionKey, driverNumber, retryDelay);
                Thread.sleep(retryDelay);  // Wait for the specified delay
                retryCount++;
                delayMs *= 2;  // Exponentially increase delay after each retry
            } else {
                logger.error("Failed to fetch position for session {} and driver {}. Response code: {}", sessionKey, driverNumber, response.statusCode());
                return null;  // Return null for failed requests (non-200, non-429)
            }
        }

        logger.error("Max retries reached for session {} and driver {}. Skipping.", sessionKey, driverNumber);
        return null;  // Return null if max retries are exceeded
    }

    /**
     * Fetches all positions for all races and drivers from the OpenF1 API.
     *
     * @return a list of positions imported from OpenF1
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    public List<Position> fetchAllPositionsFromOpenF1() throws IOException, InterruptedException {
        return fetchPositionsFromOpenF1(raceService.getAllRaces(), driverService.getAllDrivers());
    }

}
