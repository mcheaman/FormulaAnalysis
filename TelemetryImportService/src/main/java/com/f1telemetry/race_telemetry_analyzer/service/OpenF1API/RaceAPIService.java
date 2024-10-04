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

/**
 * Service responsible for fetching and upserting race data from the OpenF1 API.
 *
 * <p>This service interacts with the OpenF1 API to retrieve race session data after the latest session
 * stored in the database. The fetched races are upserted (inserted or updated) into the database using
 * the {@link RaceService}, ensuring that only new or updated races are added to the database.
 *
 * <p>Asynchronous HTTP requests are used to fetch the data from the API without blocking the main thread.
 * Once the data is retrieved, it is processed, parsed, and stored in the database. The method returns
 * a list of races that were imported (either newly added or updated) during this operation.
 */
@Service
public class RaceAPIService {

    @Autowired
    private RaceService raceService;

    @Autowired
    LatestSessionService latestSessionService;


    private static final Logger logger = LoggerFactory.getLogger(RaceAPIService.class);

    /**
     * Fetches race data from the OpenF1 API, upserts it into the database, and returns the races that were imported.
     *
     * <p>This method retrieves the latest session end date from the database and uses it to query the OpenF1 API
     * for races that have occurred after that date. The returned race data is parsed, and any new or updated races
     * are upserted into the database.
     *
     * <p>Asynchronous HTTP requests are used to avoid blocking the main thread during API interaction. The method
     * waits for the API response and race processing to complete before returning the list of imported races.
     *
     * @return A list of races that were newly added or updated (upserted) in the database.
     * @throws IOException If an I/O error occurs during the HTTP request or while parsing the JSON response.
     * @throws InterruptedException If the thread is interrupted while waiting for the API response.
     * @throws ExecutionException If an error occurs during the execution of the asynchronous task.
     */
    public List<Race> fetchRacesFromOpenF1() throws IOException, InterruptedException, ExecutionException {
        String latestSessionEndDate = latestSessionService.getLatestSessionFromDB()
                .map(LatestSession::getSessionEndDate)
                .orElse("2024-01-01");  // Fallback to an early date if no session exists in the DB

        String encodedDateStart = URLEncoder.encode("date_start>" + latestSessionEndDate, StandardCharsets.UTF_8);
        String racesApiUrl = "https://api.openf1.org/v1/sessions?session_type=Race&" + encodedDateStart;

        HttpClient client = HttpClient.newHttpClient();

        // Create the HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(racesApiUrl))
                .header("Accept", "application/json")
                .build();
        // List to hold races that need to be upserted and eventually returned
        List<Race> racesToUpsert = new ArrayList<>();

        // Send the request asynchronously and collect CompletableFutures
        CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        CompletableFuture<Void> processFuture = futureResponse.thenAccept(response -> {
            if (response.statusCode() == 200) {
                String jsonResponse = response.body();

                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(jsonResponse);

                    // Loop through each race node in the array
                    for (JsonNode raceNode : rootNode) {
                        Race raceToUpsert = new Race(
                                raceNode.get("session_key").asInt(),
                                raceNode.get("year").asInt(),
                                raceNode.get("session_name").asText(),
                                raceNode.get("country_name").asText(),
                                raceNode.get("circuit_short_name").asText()
                        );

                        // Add race to the list for upsert
                        racesToUpsert.add(raceToUpsert);
                    }

                    // Perform batch upsert (insert or update)
                    if (!racesToUpsert.isEmpty()) {
                        raceService.addRaces(racesToUpsert);
                        logger.info("Upserted {} races successfully.", racesToUpsert.size());
                    } else {
                        logger.info("No new races to upsert.");
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

        // Return the races from the database (if needed)
        return racesToUpsert;
    }

}
