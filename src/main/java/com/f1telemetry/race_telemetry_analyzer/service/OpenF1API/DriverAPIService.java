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

/**
 * Service responsible for fetching and managing driver data from the OpenF1 API.
 *
 * <p>This service handles asynchronous requests to the OpenF1 API to fetch driver data for a list of race sessions.
 * It supports rate-limited requests using exponential backoff and retries and performs batch upsert of drivers into the database.
 */
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

    /**
     * Validates driver information before processing it.
     *
     * @param driver the driver to validate
     * @return {@code true} if the driver is valid, {@code false} otherwise
     */
    private boolean isValidDriver(Driver driver) {
        return driver.getTeam() != null && !driver.getTeam().equals("null") &&
                driver.getCountryCode() != null && !driver.getCountryCode().equals("null") &&
                driver.getDriverNumber() > 0;
    }

    /**
     * Fetches drivers from the OpenF1 API for multiple race sessions.
     *
     * @param races a list of races to fetch drivers for
     * @return a list of drivers imported from OpenF1
     * @throws IOException if there is an I/O error during the fetch
     * @throws InterruptedException if the thread is interrupted during the fetch
     */
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

    /**
     * Sends an API request with retry support.
     *
     * @param sessionURL the session URL for the request
     * @param sessionKey the session key to identify the request
     * @param driversToUpsert the list to collect drivers for upserting
     * @param semaphore semaphore to limit concurrency
     * @param retryCount the current retry count
     * @param delayMs the delay for retrying the request
     * @return a {@link CompletableFuture} that completes when the request is done
     */
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

    /**
     * Sends a request and retries if necessary due to rate limiting (HTTP 429).
     *
     * @param request the request to send
     * @param sessionURL the session URL
     * @param sessionKey the session key
     * @param driversToUpsert the list of drivers to upsert
     * @param retryCount the current retry count
     * @param delayMs the delay between retries
     * @return a {@link CompletableFuture} that completes when the request is done
     */
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

    /**
     * Processes the API response and extracts valid driver data.
     *
     * @param jsonResponse the JSON response from the API
     * @param driversToUpsert the list to collect drivers for upserting
     */
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

    /**
     * Handles rate-limiting errors by retrying the request with an exponential backoff.
     *
     * @param response the HTTP response
     * @param request the request to retry
     * @param sessionURL the session URL
     * @param sessionKey the session key
     * @param driversToUpsert the list of drivers to upsert
     * @param retryCount the current retry count
     * @param delayMs the delay between retries
     */
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


    /**
     * Fetches drivers for all races from OpenF1 API.
     *
     * @return a list of drivers from OpenF1
     * @throws IOException if there is an I/O error
     * @throws InterruptedException if the thread is interrupted
     */
    public List<Driver> fetchAllDriversFromOpenF1() throws IOException, InterruptedException {
        return fetchDriversFromOpenF1(raceService.getAllRaces());
    }
}
