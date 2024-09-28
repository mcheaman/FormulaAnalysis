package com.f1telemetry.race_telemetry_analyzer.controller;

import com.f1telemetry.race_telemetry_analyzer.model.Position;
import com.f1telemetry.race_telemetry_analyzer.service.PositionService;
import com.f1telemetry.race_telemetry_analyzer.service.OpenF1API.PositionAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @Autowired
    private PositionAPIService positionAPIService;

    @GetMapping
    public List<Position> getAllPositions() {
        return positionService.getAllPositions();
    }

    @GetMapping("/session/{sessionKey}/driver/{driverNumber}")
    public ResponseEntity<Position> getPositionBySessionAndDriver(@PathVariable Integer sessionKey, @PathVariable Integer driverNumber) {
        Position position = positionService.getPositionBySessionAndDriver(sessionKey, driverNumber);
        return position != null ? ResponseEntity.ok(position) : ResponseEntity.notFound().build();
    }

    @PostMapping("/import")
    public ResponseEntity<List<Position>> importPositions() throws IOException, ExecutionException, InterruptedException {
        List<Position> importedPositions = positionAPIService.fetchAllPositionsFromOpenF1();
        return ResponseEntity.ok(importedPositions);
    }
}
