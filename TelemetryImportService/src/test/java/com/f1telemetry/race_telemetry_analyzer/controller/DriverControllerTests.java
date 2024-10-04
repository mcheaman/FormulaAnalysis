package com.f1telemetry.race_telemetry_analyzer.controller;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DriverControllerTests {

    @Mock
    private DriverService driverService;

    @InjectMocks
    private DriverController driverController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void testGetAllDrivers() {
        // Arrange
        Driver driver1 = new Driver("Lewis Hamilton", "Mercedes");
        Driver driver2 = new Driver("Max Verstappen", "Red Bull");
        List<Driver> driverList = Arrays.asList(driver1, driver2);

        when(driverService.getAllDrivers()).thenReturn(driverList);

        // Act
        List<Driver> result = driverController.getAllDrivers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Lewis Hamilton", result.getFirst().getFullName());
        verify(driverService, times(1)).getAllDrivers();
    }

    @Test
    void testGetDriverById() {
        // Arrange
        Driver driver = new Driver("Lewis Hamilton", "Mercedes");
        when(driverService.getDriverById("123")).thenReturn(Optional.of(driver));

        // Act
        ResponseEntity<Driver> response = driverController.getDriverById("123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(driver, response.getBody());
        verify(driverService, times(1)).getDriverById("123");
    }

    @Test
    void testGetDriverById_NotFound() {
        // Arrange
        when(driverService.getDriverById("999")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Driver> response = driverController.getDriverById("999");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(driverService, times(1)).getDriverById("999");
    }

    @Test
    void testAddDriver() {
        // Arrange
        Driver newDriver = new Driver("Lewis Hamilton", "Mercedes");
        when(driverService.addDriver(any(Driver.class))).thenReturn(newDriver);

        // Act
        ResponseEntity<Driver> result = driverController.addDriver(newDriver);

        // Assert
        assertEquals("Lando Norris", Objects.requireNonNull(result.getBody()).getFullName());
        verify(driverService, times(1)).addDriver(newDriver);
    }

    @Test
    void testDeleteDriver() {
        // Act
        ResponseEntity<Void> response = driverController.deleteDriver("123");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(driverService, times(1)).deleteDriver("123");
    }




}
