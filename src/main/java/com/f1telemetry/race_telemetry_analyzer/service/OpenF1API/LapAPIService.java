package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.model.Lap;
import com.f1telemetry.race_telemetry_analyzer.model.Race;
import com.f1telemetry.race_telemetry_analyzer.service.LapService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class LapAPIService {

    private final LapService lapService;

    private static final Logger logger = LoggerFactory.getLogger(LapAPIService.class);

    private static final String LAP_API_URL_TEMPLATE = "https://api.openf1.org/v1/laps?session_key=%d&driver_number=%d";

    public LapAPIService(LapService lapService) {
        this.lapService = lapService;
    }

    // Method to import laps from OpenF1 API for a specific session and driver
    public List<Lap> fetchLapsBySessionAndDriverFromOpenF1(Integer sessionKey, Integer driverNumber) throws IOException, InterruptedException, ExecutionException {
        HttpClient client = HttpClient.newHttpClient();

        String lapApiUrl = String.format(LAP_API_URL_TEMPLATE, sessionKey, driverNumber);

        // Create the HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(lapApiUrl))
                .header("Accept", "application/json")
                .build();

        // Send the request asynchronously and handle the response
        CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        // Process the response
        List<Lap> lapsToAdd = new ArrayList<>();
        futureResponse.thenAccept(response -> {
            if (response.statusCode() == 200) {
                String jsonResponse = response.body();

                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(jsonResponse);

                    for (JsonNode lapNode : rootNode) {
                        Lap lap = new Lap(
                                sessionKey,
                                driverNumber,
                                lapNode.get("lap_number").asInt(),
                                // Use null checks for remaining fields
                                lapNode.hasNonNull("lap_duration") ? lapNode.get("lap_duration").floatValue() : 0.0f,
                                lapNode.hasNonNull("duration_sector_1") ? lapNode.get("duration_sector_1").floatValue() : 0.0f,
                                lapNode.hasNonNull("duration_sector_2") ? lapNode.get("duration_sector_2").floatValue() : 0.0f,
                                lapNode.hasNonNull("duration_sector_3") ? lapNode.get("duration_sector_3").floatValue() : 0.0f,
                                Objects.equals(lapNode.get("is_pit_out_lap").asText(), "true"),
                                lapNode.hasNonNull("st_speed") ? lapNode.get("st_speed").asInt() : 0
                        );
                        lapsToAdd.add(lap);
                    }
                } catch (IOException e) {
                    logger.error("Error parsing lap json for driver {} session {} error code: {}", driverNumber, sessionKey, e.getMessage());
                }
            } else {
                logger.error("Failed to fetch laps for driver {} session {}. Response code: {} ", driverNumber, sessionKey, response.statusCode());
            }
        }).get();  // Wait for the CompletableFuture to complete


        logger.debug("{} laps to be added for driver {} on session {}", lapsToAdd.size(), driverNumber, sessionKey);
        return lapsToAdd;
    }

    // Fetches laps for all races and drivers
    public List<Lap> fetchLapsFromOpenF1(List<Race> races, List<Driver> drivers) throws IOException, ExecutionException, InterruptedException {
        List<Lap> allLaps = new ArrayList<>(); // This will store all laps across all races
        List<CompletableFuture<Void>> futures = new ArrayList<>();  // To keep track of asynchronous persistence

        // Iterate over every race
        for (Race race : races) {
            Integer sessionKey = race.getSessionKey();  // Get the session key from the race
            List<Lap> raceLaps = new ArrayList<>();  // Create a new list for each race

            // For each race, iterate over every driver
            for (Driver driver : drivers) {
                Integer driverNumber = driver.getDriverNumber();  // Get the driver number from the driver

                // Fetch laps for the current race and driver
                List<Lap> lapsForThisRaceAndDriver = fetchLapsBySessionAndDriverFromOpenF1(sessionKey, driverNumber);

                // Add fetched laps to the overall list of all laps
                raceLaps.addAll(lapsForThisRaceAndDriver);
                allLaps.addAll(lapsForThisRaceAndDriver);
            }

            logger.info("{} laps fetched from {} {} {}", raceLaps.size(), race.getCircuitName(), race.getYear(), race.getSessionName());

            // Make a copy of raceLaps before passing to the asynchronous task
            List<Lap> lapsToPersist = new ArrayList<>(raceLaps);

            // Persist the laps asynchronously and store the future in the list
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                lapService.addLaps(lapsToPersist);  // Persist the laps from the current race to MongoDB
            });
            futures.add(future);
        }

        logger.info("{} laps from {} races to be added to MongoDB", allLaps.size(), races.size());

        // Wait for all persistence operations to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return allLaps;
    }


}
