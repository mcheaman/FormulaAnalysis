package com.f1telemetry.race_telemetry_analyzer.controller;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.service.OpenF1ApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OpenF1ApiControllerTests {

    @Mock
    private OpenF1ApiService openF1ApiService;

    @InjectMocks
    private OpenF1ApiController openF1ApiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases go here...
    @Test
    void testImportDriversSuccess() throws IOException, InterruptedException {
        // Arrange
        List<Driver> mockDrivers = new ArrayList<>();
        Driver driver1 = new Driver("Lewis Hamilton", "Mercedes");
        Driver driver2 = new Driver("Max Verstappen", "Red Bull");
        mockDrivers.add(driver1);
        mockDrivers.add(driver2);

        when(openF1ApiService.fetchDriversFromOpenF1()).thenReturn(mockDrivers);

        // Act
        ResponseEntity<List<Driver>> response = openF1ApiController.importDriversFromOpenF1();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(openF1ApiService, times(1)).fetchDriversFromOpenF1();
    }

    @Test
    void testImportDriversIOException() throws IOException, InterruptedException {
        // Arrange
        when(openF1ApiService.fetchDriversFromOpenF1()).thenThrow(new IOException("API failed"));

        // Act
        ResponseEntity<List<Driver>> response = openF1ApiController.importDriversFromOpenF1();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(openF1ApiService, times(1)).fetchDriversFromOpenF1();
    }

    @Test
    void testImportDriversInterruptedException() throws IOException, InterruptedException {
        // Arrange
        when(openF1ApiService.fetchDriversFromOpenF1()).thenThrow(new InterruptedException("Operation interrupted"));

        // Act
        ResponseEntity<List<Driver>> response = openF1ApiController.importDriversFromOpenF1();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(openF1ApiService, times(1)).fetchDriversFromOpenF1();
    }

}
