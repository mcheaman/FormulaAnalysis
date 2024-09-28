package com.f1telemetry.race_telemetry_analyzer.service;

import com.f1telemetry.race_telemetry_analyzer.model.Position;
import com.f1telemetry.race_telemetry_analyzer.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private PositionService positionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void getAllPositions_ShouldReturnAllPositions() {
        // Arrange
        List<Position> expectedPositions = Arrays.asList(
                new Position(1, 33, 1),
                new Position(1, 44, 2)
        );
        when(positionRepository.findAll()).thenReturn(expectedPositions);

        // Act
        List<Position> actualPositions = positionService.getAllPositions();

        // Assert
        assertEquals(expectedPositions.size(), actualPositions.size());
        assertEquals(expectedPositions, actualPositions);
        verify(positionRepository, times(1)).findAll();
    }

    @Test
    void getPositionBySessionAndDriver_ShouldReturnCorrectPosition() {
        // Arrange
        Integer sessionKey = 1;
        Integer driverNumber = 33;
        Position expectedPosition = new Position(sessionKey, driverNumber, 1);
        when(positionRepository.findBySessionKeyAndDriverNumber(sessionKey, driverNumber)).thenReturn(expectedPosition);

        // Act
        Position actualPosition = positionService.getPositionBySessionAndDriver(sessionKey, driverNumber);

        // Assert
        assertEquals(expectedPosition, actualPosition);
        verify(positionRepository, times(1)).findBySessionKeyAndDriverNumber(sessionKey, driverNumber);
    }

    @Test
    void addPositions_ShouldSaveAllPositionsToRepository() {
        // Arrange
        List<Position> positionsToSave = Arrays.asList(
                new Position(1, 33, 1),
                new Position(1, 44, 2)
        );

        // Act
        positionService.addPositions(positionsToSave);

        // Assert
        verify(positionRepository, times(1)).saveAll(positionsToSave);
    }
}
