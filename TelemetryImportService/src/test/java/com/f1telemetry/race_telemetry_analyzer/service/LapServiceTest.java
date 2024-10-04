package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Lap;
import com.f1telemetry.race_telemetry_analyzer.repository.LapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LapServiceTest {

    @Mock
    private LapRepository lapRepository;

    @InjectMocks
    private LapService lapService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void getAllLaps_ShouldReturnAllLaps() {
        // Arrange
        List<Lap> expectedLaps = Arrays.asList(
                new Lap(1, 33, 1, 90.5f, 30.5f, 30.0f, 30.0f,false,  320),
                new Lap(1, 33, 2, 91.2f, 30.4f, 30.2f, 30.6f, false, 318)
        );
        when(lapRepository.findAll()).thenReturn(expectedLaps);

        // Act
        List<Lap> actualLaps = lapService.getAllLaps();

        // Assert
        assertEquals(expectedLaps.size(), actualLaps.size());
        assertEquals(expectedLaps, actualLaps);
        verify(lapRepository, times(1)).findAll();
    }

    @Test
    void getLapsBySessionAndDriver_ShouldReturnLapsForGivenSessionAndDriver() {
        // Arrange
        Integer sessionKey = 1;
        Integer driverNumber = 33;
        List<Lap> expectedLaps = Arrays.asList(
                new Lap(sessionKey, driverNumber, 1, 90.5f, 30.5f, 30.0f, 30.0f, false,  320),
                new Lap(sessionKey, driverNumber, 2, 91.2f, 30.4f, 30.2f, 30.6f,false,  318)
        );
        when(lapRepository.findBySessionKeyAndDriverNumber(sessionKey, driverNumber)).thenReturn(expectedLaps);

        // Act
        List<Lap> actualLaps = lapService.getLapsBySessionAndDriver(sessionKey, driverNumber);

        // Assert
        assertEquals(expectedLaps.size(), actualLaps.size());
        assertEquals(expectedLaps, actualLaps);
        verify(lapRepository, times(1)).findBySessionKeyAndDriverNumber(sessionKey, driverNumber);
    }

    @Test
    void addLaps_ShouldSaveLapsToDatabase() {
        // Arrange
        List<Lap> lapsToSave = Arrays.asList(
                new Lap(1, 33, 1, 90.5f, 30.5f, 30.0f, 30.0f,false,  320),
                new Lap(1, 33, 2, 91.2f, 30.4f, 30.2f, 30.6f,false,  318)
        );

        // Act
        lapService.addLaps(lapsToSave);

        // Assert
        verify(lapRepository, times(1)).saveAll(lapsToSave);
    }
}
