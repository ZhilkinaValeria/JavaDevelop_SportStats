package com.example.earthquakes.service;

import com.example.earthquakes.config.TestConfig;
import com.example.earthquakes.model.Earthquake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class EarthquakesServiceTest {

    @Autowired
    private EarthquakesService earthquakesService;

    @Test
    void getAll_ShouldReturnTestEarthquakes() {
        // Act
        List<Earthquake> result = earthquakesService.getAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        Earthquake first = result.get(0);
        assertEquals("test-1", first.getId());
        assertEquals(5.5, first.getMagnitude());
    }

    @Test
    void getById_WhenExists_ShouldReturnEarthquake() {
        // Act
        Earthquake result = earthquakesService.getById("test-1");
        
        // Assert
        assertNotNull(result);
        assertEquals("test-1", result.getId());
        assertEquals(5.5, result.getMagnitude());
    }

    @Test
    void avgMagnitude_ShouldCalculateAverage() {
        // Act
        Double result = earthquakesService.avgMagnitude();
        
        // Assert
        assertEquals(6.0, result, 0.001);
    }

    @Test
    void create_ShouldAddNewEarthquake() {
        // Arrange
        Earthquake newEarthquake = new Earthquake();
        newEarthquake.setId("test-3");
        newEarthquake.setMagnitude(4.5);
        newEarthquake.setPlace("Test Location 3");
        
        // Act
        Earthquake created = earthquakesService.create(newEarthquake);
        
        // Assert
        assertNotNull(created);
        assertEquals("test-3", created.getId());
        
        // Проверяем, что объект действительно сохранился
        Earthquake retrieved = earthquakesService.getById("test-3");
        assertNotNull(retrieved);
        assertEquals(4.5, retrieved.getMagnitude());
    }

    @Test
    void delete_ShouldRemoveEarthquake() {
        // Act
        earthquakesService.delete("test-1");
        
        // Assert
        List<Earthquake> all = earthquakesService.getAll();
        assertEquals(1, all.size());
        assertEquals("test-2", all.get(0).getId());
    }
}