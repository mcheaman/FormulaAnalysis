package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.model.Race;
import com.f1telemetry.race_telemetry_analyzer.service.DriverService;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

@Service
public class DriverAPIService {

    @Autowired
    private DriverService driverService;

    @Autowired
    private RaceService raceService;

    private static final String DRIVER_API_BASE_URL = "https://api.openf1.org/v1/drivers?session_key=";
    private static final Logger logger = LoggerFactory.getLogger(DriverAPIService.class);

    private static final int INITIAL_DELAY_MS = 1000;  // Initial delay for exponential backoff
    private static final int MAX_RETRIES = 5;  // Max number of retries for rate-limited requests
    private final HttpClient client = HttpClient.newHttpClient();

    // Helper method to validate driver information
    private boolean isValidDriver(Driver driver) {
        return driver.getTeam() != null && !driver.getTeam().equals("null") &&
                driver.getCountryCode() != null && !driver.getCountryCode().equals("null") &&
                driver.getDriverNumber() > 0;
    }

    // Public method to fetch drivers from OpenF1 for multiple races
    public List<Driver> fetchDriversFromOpenF1(List<Race> races) throws IOException, InterruptedException {
        List<Integer> sessionKeys = races.stream()
                .map(Race::getSessionKey)
                .toList();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<Driver> driversToUpsert = new ArrayList<>();
        Semaphore semaphore = new Semaphore(3);  // Limit to 3 concurrent requests

        for (Integer sessionKey : sessionKeys) {
            semaphore.acquire();  // Acquire permit
            String sessionURL = DRIVER_API_BASE_URL + sessionKey;

            // Create and send the request, with retry and backoff handling
            CompletableFuture<Void> future = sendRequest(sessionURL, sessionKey, driversToUpsert, semaphore,0, INITIAL_DELAY_MS);
            futures.add(future);
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Perform batch upsert if new drivers are fetched
        if (!driversToUpsert.isEmpty()) {
            driverService.addDrivers(driversToUpsert);
        }

        return driverService.getAllDrivers();
    }

    // Wrapper method to send a request through sendRequestWithRetry
    private CompletableFuture<Void> sendRequest(String sessionURL, Integer sessionKey, List<Driver> driversToUpsert, Semaphore semaphore, int retryCount, int delayMs) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(sessionURL))
                .header("Accept", "application/json")
                .build();
        return sendRequestWithRetry(request, sessionURL, sessionKey, driversToUpsert, retryCount, delayMs)
                .exceptionally(ex -> {
                    logger.error("Request for session {} failed.", sessionKey, ex);
                    return null;
                })
                .whenComplete((result, ex) -> semaphore.release());  // Release the semaphore after the request is complete
    }

    // Method to send a request and process the successful output, or handle a rate limit error
    private CompletableFuture<Void> sendRequestWithRetry(HttpRequest request, String sessionURL, Integer sessionKey, List<Driver> driversToUpsert, int retryCount, int delayMs) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        // If the request was successful, process the drivers
                        processDriversResponse(response.body(), driversToUpsert);
                    } else if (response.statusCode() == 429) {
                        // If we hit a rate limit (429), handle it by retrying with exponential backoff
                        handleRateLimit(response, request, sessionURL, sessionKey, driversToUpsert, retryCount, delayMs);
                    } else {
                        logger.error("Failed to fetch drivers for session {}: response code {}", sessionKey, response.statusCode());
                    }
                });
    }

    // Method to process the API response for drivers
    private void processDriversResponse(String jsonResponse, List<Driver> driversToUpsert) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            for (JsonNode driverNode : rootNode) {
                String driverName = driverNode.get("full_name").asText();
                Driver driver = new Driver(
                        driverName,
                        driverNode.get("broadcast_name").asText(null),
                        driverNode.get("team_name").asText(null),
                        driverNode.get("country_code").asText(null),
                        driverNode.get("driver_number").asInt(0),
                        driverNode.get("headshot_url").asText(null)
                );

                if (isValidDriver(driver)) {
                    synchronized (driversToUpsert) {
                        driversToUpsert.add(driver);
                    }
                } else {
                    logger.debug("Skipping invalid driver: {}", driver.toString());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to process driver data.", e);
        }
    }

    // Method to handle rate limiting (HTTP 429) with exponential backoff
    private void handleRateLimit(HttpResponse<String> response, HttpRequest request, String sessionURL, Integer sessionKey, List<Driver> driversToUpsert, int retryCount, int delayMs) {
        Optional<String> retryAfter = response.headers().firstValue("Retry-After");
        int retryDelay = retryAfter.map(Integer::parseInt).orElse(delayMs);  // Default to provided delay if Retry-After is missing

        logger.warn("Rate limit exceeded for session {}. Retrying after {} ms", sessionKey, retryDelay);

        try {
            Thread.sleep(retryDelay);  // Wait for the specified duration before retrying
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (retryCount < MAX_RETRIES) {
            // Retry the request with an incremented retry count
            sendRequest(sessionURL, sessionKey, driversToUpsert, new Semaphore(1), retryCount + 1, retryDelay * 2);
        } else {
            logger.error("Max retries reached for session {}. Skipping.", sessionKey);
        }
    }


    // Convenience method to fetch all races and drivers
    public List<Driver> fetchAllDriversFromOpenF1() throws IOException, InterruptedException {
        return fetchDriversFromOpenF1(raceService.getAllRaces());
    }
}
