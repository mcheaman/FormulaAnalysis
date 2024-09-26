package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
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

    private boolean isValidDriver(Driver driver) {
        return driver.getTeam() != null && !driver.getTeam().equals("null") &&
                driver.getCountryCode() != null && !driver.getCountryCode().equals("null") &&
                driver.getDriverNumber() > 0 &&
                driver.getHeadshotUrl() != null && !driver.getHeadshotUrl().equals("null");
    }

    public List<Driver> fetchDriversFromOpenF1() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        List<String> sessions = raceService.getAllSessionKeys();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<Driver> driversToUpsert = new ArrayList<>();
        Semaphore semaphore = new Semaphore(5);  // Limit to 5 concurrent requests

        for (String session_key : sessions) {
            semaphore.acquire();  // Acquire a permit before sending the request
            String sessionURL = DRIVER_API_BASE_URL + session_key;

            // Create the HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(sessionURL))
                    .header("Accept", "application/json")
                    .build();

            // Send the request asynchronously
            CompletableFuture<Void> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        try {
                            if (response.statusCode() == 200) {
                                String jsonResponse = response.body();
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                                for (JsonNode driverNode : rootNode) {
                                    String driverName = driverNode.get("full_name").asText();
                                    Driver importedDriverInfo = new Driver(
                                            driverName,
                                            driverNode.get("broadcast_name").asText(null),
                                            driverNode.get("team_name").asText(null),
                                            driverNode.get("country_code").asText(null),
                                            driverNode.get("driver_number").asInt(0),
                                            driverNode.get("headshot_url").asText(null)
                                    );
                                    if (isValidDriver(importedDriverInfo)) {
                                        synchronized (driversToUpsert) {
                                            driversToUpsert.add(importedDriverInfo);
                                        }
                                    } else {
                                        logger.warn("Skipping invalid driver: {}", driverName);
                                    }
                                }
                            } else if (response.statusCode() == 429) {
                                handleRateLimit(response, sessionURL);  // Handle 429 error properly
                            } else {
                                logger.error("Failed to fetch drivers for session: {}. Response code: {}", session_key, response.statusCode());
                            }
                        } catch (IOException | InterruptedException e) {
                            logger.error("Failed to parse driver data for session: " + session_key, e);
                        }
                    })
                    .exceptionally(ex -> {
                        logger.error("Request for session " + session_key + " failed.", ex);
                        return null;
                    })
                    .whenComplete((result, ex) -> semaphore.release());  // Release the permit when done

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        if(!driversToUpsert.isEmpty()){
            driverService.addDrivers(driversToUpsert);  // Perform batch upsert
        }
        return driverService.getAllDrivers();  // Return the drivers stored in the database
    }

    private void handleRateLimit(HttpResponse<String> response, String sessionURL) throws InterruptedException {
        Optional<String> retryAfter = response.headers().firstValue("Retry-After");
        int retryDelay = retryAfter.map(Integer::parseInt).orElse(5000);  // Default retry delay 5 seconds if header not present

        logger.warn("Rate limit exceeded. Retrying request for {} after {} ms", sessionURL, retryDelay);
        Thread.sleep(retryDelay);  // Wait for the specified duration
    }



}


