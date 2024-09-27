package com.f1telemetry.race_telemetry_analyzer.controller;

import com.f1telemetry.race_telemetry_analyzer.model.Lap;
import com.f1telemetry.race_telemetry_analyzer.service.LapService;
import com.f1telemetry.race_telemetry_analyzer.service.OpenF1API.LapAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/laps")
public class LapController {

    @Autowired
    private LapService lapService;

    @Autowired
    private LapAPIService lapAPIService;

    // Endpoint to get all laps
    @GetMapping
    public List<Lap> getAllLaps() {
        return lapService.getAllLaps();
    }

    // Endpoint to get laps by driver number and session key
    @GetMapping("/session/{sessionKey}/driver/{driverNumber}")
    public List<Lap> getLapsBySessionAndDriver(@PathVariable Integer sessionKey, @PathVariable Integer driverNumber) {
        return lapService.getLapsBySessionAndDriver(sessionKey, driverNumber);
    }

    // Endpoint to import laps from OpenF1 for a specific session and driver
    @PostMapping("/import/session/{sessionKey}/driver/{driverNumber}")
    public List<Lap> fetchLapsFromOpenF1(@PathVariable Integer sessionKey, @PathVariable Integer driverNumber) throws IOException, InterruptedException, ExecutionException {
        return lapAPIService.fetchLapsBySessionAndDriverFromOpenF1(sessionKey, driverNumber);
    }
}
