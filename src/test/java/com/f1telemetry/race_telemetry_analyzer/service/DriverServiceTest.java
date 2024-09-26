package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Driver;
import com.f1telemetry.race_telemetry_analyzer.repository.DriverRepository;
import com.f1telemetry.race_telemetry_analyzer.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case for getAllDrivers()
    @Test
    void getAllDrivers_ShouldReturnAllDrivers() {
        // Arrange
        List<Driver> mockDrivers = Arrays.asList(
                new Driver("Max Verstappen", "Red Bull Racing"),
                new Driver("Lewis Hamilton", "Mercedes")
        );
        when(driverRepository.findAll()).thenReturn(mockDrivers);

        // Act
        List<Driver> result = driverService.getAllDrivers();

        // Assert
        assertEquals(2, result.size());
        verify(driverRepository, times(1)).findAll();
    }

    // Test case for getDriverById()
    @Test
    void getDriverById_ShouldReturnDriverWhenIdExists() {
        // Arrange
        Driver mockDriver = new Driver("Max Verstappen", "Red Bull Racing");
        when(driverRepository.findById("Max Verstappen")).thenReturn(Optional.of(mockDriver));

        // Act
        Optional<Driver> result = driverService.getDriverById("Max Verstappen");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockDriver, result.get());
        verify(driverRepository, times(1)).findById("Max Verstappen");
    }

    @Test
    void getDriverById_ShouldReturnEmptyWhenIdDoesNotExist() {
        // Arrange
        when(driverRepository.findById("Max Verstappen")).thenReturn(Optional.empty());

        // Act
        Optional<Driver> result = driverService.getDriverById("Max Verstappen");

        // Assert
        assertFalse(result.isPresent());
        verify(driverRepository, times(1)).findById("Max Verstappen");
    }

    // Test case for addDriver()
    @Test
    void addDriver_ShouldSaveAndReturnNewDriver() {
        // Arrange
        Driver newDriver = new Driver("Max Verstappen", "Red Bull Racing");
        when(driverRepository.save(newDriver)).thenReturn(newDriver);

        // Act
        Driver result = driverService.addDriver(newDriver);

        // Assert
        assertNotNull(result);
        assertEquals(newDriver, result);
        verify(driverRepository, times(1)).save(newDriver);
    }

    // Test case for addDrivers()
    @Test
    void addDrivers_ShouldSaveAndReturnMultipleDrivers() {
        // Arrange
        List<Driver> newDrivers = Arrays.asList(
                new Driver("Max Verstappen", "Red Bull Racing"),
                new Driver("Lewis Hamilton", "Mercedes")
        );
        when(driverRepository.saveAll(newDrivers)).thenReturn(newDrivers);

        // Act
        List<Driver> result = driverService.addDrivers(newDrivers);

        // Assert
        assertEquals(2, result.size());
        verify(driverRepository, times(1)).saveAll(newDrivers);
    }

    // Test case for deleteDriver()
    @Test
    void deleteDriver_ShouldDeleteDriverById() {
        // Act
        driverService.deleteDriver("Max Verstappen");

        // Assert
        verify(driverRepository, times(1)).deleteById("Max Verstappen");
    }

    // Test case for getDriverByName()
    @Test
    void getDriverByName_ShouldReturnDriverWhenNameExists() {
        // Arrange
        Driver mockDriver = new Driver("Max Verstappen", "Red Bull Racing");
        when(driverRepository.findByFullName("Max Verstappen")).thenReturn(Optional.of(mockDriver));

        // Act
        Optional<Driver> result = driverService.getDriverByName("Max Verstappen");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockDriver, result.get());
        verify(driverRepository, times(1)).findByFullName("Max Verstappen");
    }

    @Test
    void getDriverByName_ShouldReturnEmptyWhenNameDoesNotExist() {
        // Arrange
        when(driverRepository.findByFullName("Max Verstappen")).thenReturn(Optional.empty());

        // Act
        Optional<Driver> result = driverService.getDriverByName("Max Verstappen");

        // Assert
        assertFalse(result.isPresent());
        verify(driverRepository, times(1)).findByFullName("Max Verstappen");
    }
}
