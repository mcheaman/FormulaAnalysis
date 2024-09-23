package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class OpenF1ApiService {

    @Autowired
    private DriverService driverService;

    private static final String API_URL = "https://api.openf1.org/v1/drivers?session_key=latest"; // latest used to only access drivers from the latest session

    public List<Driver> fetchDriversFromOpenF1() throws IOException, InterruptedException {
        // Create the HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Create the HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Accept", "application/json")
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String jsonResponse = response.body();

            // Parse the JSON response into a JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Loop through each driver node in the array
            for (JsonNode driverNode : rootNode) {
                System.out.println(driverNode);
                String driverName = driverNode.get("full_name").asText();
                Optional<Driver> existingDriver = driverService.getDriverByName(driverName);  // Check if driver exists by name

                if (existingDriver.isEmpty()) {  // Only add if driver doesn't already exist
                    Driver driver = new Driver();
                    driver.setName(driverName);
                    driver.setTeam(driverNode.get("team_name").asText());  // Adjust based on actual fields


                    driverService.addDriver(driver);
                }
            }
        } else {
            throw new IOException("Failed to fetch drivers from OpenF1 API. Response code: " + response.statusCode());
        }

        return driverService.getAllDrivers();  // Return the drivers stored in the database
    }
}
