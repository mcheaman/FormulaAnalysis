package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Race;
import com.f1telemetry.race_telemetry_analyzer.repository.RaceRepository;
import com.f1telemetry.race_telemetry_analyzer.service.RaceService;
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

class RaceServiceTest {

    @Mock
    private RaceRepository raceRepository;

    @InjectMocks
    private RaceService raceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case for getAllRaces()
    @Test
    void getAllRaces_ShouldReturnAllRaces() {
        // Arrange
        List<Race> mockRaces = Arrays.asList(
                new Race(1, 2022, "Sprint", "Country A", "Circuit A"),
                new Race(2, 2022, "Race", "Country B", "Circuit B")
        );
        when(raceRepository.findAll()).thenReturn(mockRaces);

        // Act
        List<Race> result = raceService.getAllRaces();

        // Assert
        assertEquals(2, result.size());
        verify(raceRepository, times(1)).findAll();
    }

    // Test case for getAllSessionKeys()
    @Test
    void getAllSessionKeys_ShouldReturnAllSessionKeys() {
        // Arrange
        List<Race> mockRaces = Arrays.asList(
                new Race(1, 2022, "Sprint", "Country A", "Circuit A"),
                new Race(2, 2022, "Race", "Country B", "Circuit B")
        );
        when(raceRepository.findAll()).thenReturn(mockRaces);

        // Act
        List<Integer> result = raceService.getAllSessionKeys();

        // Assert
        assertEquals(Arrays.asList(1,2), result);
        verify(raceRepository, times(1)).findAll();
    }

    // Test case for getRaceBySessionKey()
    @Test
    void getRaceBySessionKey_ShouldReturnRaceWhenSessionKeyExists() {
        // Arrange
        Race mockRace = new Race(1, 2022, "Race", "Country A", "Circuit A");
        when(raceRepository.findById(String.valueOf(1))).thenReturn(Optional.of(mockRace));

        // Act
        Optional<Race> result = raceService.getRaceBySessionKey("session1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockRace, result.get());
        verify(raceRepository, times(1)).findById("session1");
    }

    @Test
    void getRaceBySessionKey_ShouldReturnEmptyWhenSessionKeyDoesNotExist() {
        // Arrange
        when(raceRepository.findById("1")).thenReturn(Optional.empty());

        // Act
        Optional<Race> result = raceService.getRaceBySessionKey("1");

        // Assert
        assertFalse(result.isPresent());
        verify(raceRepository, times(1)).findById("1");
    }

    // Test case for addRace()
    @Test
    void addRace_ShouldSaveAndReturnNewRace() {
        // Arrange
        Race newRace = new Race(3, 2022, "Race", "Country C", "Circuit C");
        when(raceRepository.save(newRace)).thenReturn(newRace);

        // Act
        Race result = raceService.addRace(newRace);

        // Assert
        assertNotNull(result);
        assertEquals(newRace, result);
        verify(raceRepository, times(1)).save(newRace);
    }

    // Test case for addRaces()
    @Test
    void addRaces_ShouldSaveAndReturnMultipleRaces() {
        // Arrange
        List<Race> newRaces = Arrays.asList(
                new Race(3, 2022, "Race", "Country C", "Circuit C"),
                new Race(4, 2022, "Sprint", "Country D", "Circuit D")
        );
        when(raceRepository.saveAll(newRaces)).thenReturn(newRaces);

        // Act
        List<Race> result = raceService.addRaces(newRaces);

        // Assert
        assertEquals(2, result.size());
        verify(raceRepository, times(1)).saveAll(newRaces);
    }

    // Test case for deleteRace()
    @Test
    void deleteRace_ShouldDeleteRaceBySessionKey() {
        // Act
        raceService.deleteRace("1");

        // Assert
        verify(raceRepository, times(1)).deleteById("session1");
    }
}
