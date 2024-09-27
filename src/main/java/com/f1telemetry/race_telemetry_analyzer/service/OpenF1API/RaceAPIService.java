package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;

import com.f1telemetry.race_telemetry_analyzer.model.LatestSession;
import com.f1telemetry.race_telemetry_analyzer.model.Race;
import com.f1telemetry.race_telemetry_analyzer.service.RaceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Service
public class RaceAPIService {

    @Autowired
    private RaceService raceService;

    @Autowired
    LatestSessionService latestSessionService;


    private static final Logger logger = LoggerFactory.getLogger(RaceAPIService.class);


    public List<Race> fetchRacesFromOpenF1() throws IOException, InterruptedException, ExecutionException {
        String latestSessionEndDate = latestSessionService.getLatestSessionFromDB()
                .map(LatestSession::getSessionEndDate)  // Get the sessionEndDate
                .orElse("2024-01-01");  // Fallback to an early date if no session exists in the DB
        // Encode the date_start> parameter
        String encodedDateStart = URLEncoder.encode("date_start>" + latestSessionEndDate, StandardCharsets.UTF_8);

        String racesApiUrl = "https://api.openf1.org/v1/sessions?session_type=Race&" + encodedDateStart;
        HttpClient client = HttpClient.newHttpClient();


        // List to hold races that need to be upserted
        List<Race> racesToAdd = new ArrayList<>();

        // Create the HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(racesApiUrl))
                .header("Accept", "application/json")
                .build();

        // Send the request asynchronously and collect CompletableFutures
        CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        CompletableFuture<Void> processFuture = futureResponse.thenAccept(response -> {
            if (response.statusCode() == 200) {
                String jsonResponse = response.body();

                try {
                    // Parse the JSON response into a JsonNode
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(jsonResponse);

                    // Loop through each race node in the array
                    for (JsonNode raceNode : rootNode) {
                        Integer raceSessionKey = raceNode.get("session_key").asInt();
                        Optional<Race> existingRace = raceService.getRaceBySessionKey(String.valueOf(raceSessionKey));  // Check if race exists by session_key

                        Race importedRaceInfo = new Race(
                                raceSessionKey,
                                raceNode.get("year").asInt(),
                                raceNode.get("session_name").asText(),
                                raceNode.get("country_name").asText(),
                                raceNode.get("circuit_short_name").asText()
                        );

                        // If race does not exist, add to the list for batch insertion
                        if (existingRace.isEmpty()) {
                            logger.debug("Scheduling race for addition: {}", raceSessionKey);
                            synchronized (racesToAdd) {  // Synchronize access to shared list
                                racesToAdd.add(importedRaceInfo);
                            }
                        } else {
                            logger.debug("Race already exists: {}", raceSessionKey);
                        }
                    }
                } catch (IOException e) {
                    logger.error("Failed to parse race data", e);
                }
            } else {
                logger.error("Failed to fetch races from OpenF1 API. Response code: {}", response.statusCode());
            }
        });

        // Wait for the request and processing to be completed
        processFuture.get();

        // Perform batch upsert (both insert and update)
        if (!racesToAdd.isEmpty()) {
            raceService.addRaces(racesToAdd);  // Implement batch add in the service
        } else {
            logger.info("No new races to add.");
        }

        // Return the races from the database (or return racesToAdd for better performance)
        return raceService.getAllRaces();
    }

}
