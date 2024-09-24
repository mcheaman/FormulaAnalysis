package com.f1telemetry.race_telemetry_analyzer.service.OpenF1API;

import com.f1telemetry.race_telemetry_analyzer.model.Race;
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
import java.util.List;
import java.util.Optional;

@Service
public class RaceAPIService {

    @Autowired
    private RaceService raceService;

    private static final String RACE_API_URL = "https://api.openf1.org/v1/sessions?session_type=Race"; // session_type = race to filter out practice and qualifying
    private static final Logger logger = LoggerFactory.getLogger(RaceAPIService.class);
    public List<Race> fetchRacesFromOpenF1() throws IOException, InterruptedException {
        // Create the HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Create the HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RACE_API_URL))
                .header("Accept", "application/json")
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String jsonResponse = response.body();

            // Parse the JSON response into a JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Loop through each race node in the array
            for (JsonNode raceNode : rootNode) {
                String raceSessionKey = raceNode.get("session_key").asText();
                Optional<Race> existingRace = raceService.getRaceBySessionKey(raceSessionKey);  // Check if race exists by session_key
                Race importedRaceInfo = new Race(
                        raceSessionKey,
                        raceNode.get("year").asInt(),
                        raceNode.get("session_name").asText(),
                        raceNode.get("country_name").asText(),
                        raceNode.get("circuit_short_name").asText()
                );

                if (existingRace.isEmpty()) {
                    // Add the new race
                    logger.info("Adding race: {}", raceSessionKey);
                    raceService.addRace(importedRaceInfo);
                }
            }
        } else {
            throw new IOException("Failed to fetch races from OpenF1 API. Response code: " + response.statusCode());
        }

        return raceService.getAllRaces();  // Return the races stored in the database
    }
}
