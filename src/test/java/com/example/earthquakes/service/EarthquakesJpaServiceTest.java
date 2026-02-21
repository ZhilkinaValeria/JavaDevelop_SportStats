package com.example.earthquakes.service;

import com.example.earthquakes.model.Earthquake;
import com.example.earthquakes.repository.EarthquakeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EarthquakesJpaServiceTest {
    
    @Mock
    private EarthquakeJpaRepository repository;
    
    @InjectMocks
    private EarthquakesJpaService service;
    
    private Earthquake earthquake1;
    private Earthquake earthquake2;
    
    @BeforeEach
    void setUp() {
        earthquake1 = new Earthquake();
        earthquake1.setId("1");
        earthquake1.setMagnitude(5.5);
        earthquake1.setPlace("Test Location 1");
        
        earthquake2 = new Earthquake();
        earthquake2.setId("2");
        earthquake2.setMagnitude(6.5);
        earthquake2.setPlace("Test Location 2");
    }
    
    @Test
    void getAll_ShouldReturnAllEarthquakes() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(earthquake1, earthquake2));
        
        // Act
        List<Earthquake> result = service.getAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        verify(repository, times(1)).findAll();
    }
    
    @Test
    void getById_WhenExists_ShouldReturnEarthquake() {
        // Arrange
        when(repository.findById("1")).thenReturn(Optional.of(earthquake1));
        
        // Act
        Earthquake result = service.getById("1");
        
        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals(5.5, result.getMagnitude());
        verify(repository, times(1)).findById("1");
    }
    
    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(repository.findById("999")).thenReturn(Optional.empty());
        
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.getById("999")
        );
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Earthquake not found", exception.getReason());
        verify(repository, times(1)).findById("999");
    }
    
    @Test
    void create_WhenNewId_ShouldSaveAndReturn() {
        // Arrange
        when(repository.existsById("3")).thenReturn(false);
        when(repository.save(any(Earthquake.class))).thenAnswer(i -> i.getArgument(0));
        
        Earthquake newEarthquake = new Earthquake();
        newEarthquake.setId("3");
        newEarthquake.setMagnitude(4.5);
        
        // Act
        Earthquake result = service.create(newEarthquake);
        
        // Assert
        assertNotNull(result);
        assertEquals("3", result.getId());
        verify(repository, times(1)).existsById("3");
        verify(repository, times(1)).save(newEarthquake);
    }
    
    @Test
    void create_WhenExistingId_ShouldThrowException() {
        // Arrange
        when(repository.existsById("1")).thenReturn(true);
        
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.create(earthquake1)
        );
        
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Earthquake already exists", exception.getReason());
        verify(repository, never()).save(any());
    }
    
    @Test
    void delete_WhenExists_ShouldDelete() {
        // Arrange
        when(repository.existsById("1")).thenReturn(true);
        doNothing().when(repository).deleteById("1");
        
        // Act
        service.delete("1");
        
        // Assert
        verify(repository, times(1)).existsById("1");
        verify(repository, times(1)).deleteById("1");
    }
    
    @Test
    void delete_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(repository.existsById("999")).thenReturn(false);
        
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.delete("999")
        );
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Earthquake not found", exception.getReason());
        verify(repository, never()).deleteById(any());
    }
    
    @Test
    void avgMagnitude_ShouldCalculateAverage() {
        // Arrange
        when(repository.findAverageMagnitude()).thenReturn(6.0);
        
        // Act
        Double result = service.avgMagnitude();
        
        // Assert
        assertEquals(6.0, result);
        verify(repository, times(1)).findAverageMagnitude();
    }
    
    @Test
    void avgMagnitude_WhenNoData_ShouldReturnZero() {
        // Arrange
        when(repository.findAverageMagnitude()).thenReturn(null);
        
        // Act
        Double result = service.avgMagnitude();
        
        // Assert
        assertEquals(0.0, result);
    }
}