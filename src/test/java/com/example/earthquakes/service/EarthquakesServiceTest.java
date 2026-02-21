package com.example.earthquakes.service;

import com.example.earthquakes.config.TestConfig;
import com.example.earthquakes.model.Earthquake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.MethodName.class)  // Опционально: фиксированный порядок
class EarthquakesServiceTest {

    @Autowired
    private EarthquakesService earthquakesService;
    
    @Autowired
    private TestConfig.TestEarthquakeRepository testRepository;
    
    @BeforeEach
    void setUp() {
        // Сбрасываем репозиторий в исходное состояние перед каждым тестом
        // Удаляем все элементы, кроме базовых test-1 и test-2
        List<Earthquake> all = earthquakesService.getAll();
        for (Earthquake e : all) {
            if (!"test-1".equals(e.getId()) && !"test-2".equals(e.getId())) {
                earthquakesService.delete(e.getId());
            }
        }
        
        // Убеждаемся, что test-1 и test-2 существуют
        try {
            earthquakesService.getById("test-1");
        } catch (Exception e) {
            // Если test-1 не существует, создаем его
            Earthquake e1 = new Earthquake();
            e1.setId("test-1");
            e1.setMagnitude(5.5);
            e1.setPlace("Test Location 1");
            earthquakesService.create(e1);
        }
        
        try {
            earthquakesService.getById("test-2");
        } catch (Exception e) {
            // Если test-2 не существует, создаем его
            Earthquake e2 = new Earthquake();
            e2.setId("test-2");
            e2.setMagnitude(6.5);
            e2.setPlace("Test Location 2");
            earthquakesService.create(e2);
        }
        
        System.out.println("\n=== @BeforeEach - Reset repository to initial state ===");
        List<Earthquake> afterReset = earthquakesService.getAll();
        System.out.println("Repository now contains " + afterReset.size() + " earthquakes:");
        afterReset.forEach(e -> 
            System.out.println("  - ID: '" + e.getId() + "', Magnitude: " + e.getMagnitude()));
    }

    @Test
    void getAll_ShouldReturnTestEarthquakes() {
        System.out.println("\n=== Running getAll_ShouldReturnTestEarthquakes ===");
        
        List<Earthquake> result = earthquakesService.getAll();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        
        boolean hasEarthquake1 = result.stream()
            .anyMatch(e -> "test-1".equals(e.getId()) && 5.5 == e.getMagnitude());
        boolean hasEarthquake2 = result.stream()
            .anyMatch(e -> "test-2".equals(e.getId()) && 6.5 == e.getMagnitude());
        
        assertTrue(hasEarthquake1, "Должен быть элемент с ID test-1 и magnitude 5.5");
        assertTrue(hasEarthquake2, "Должен быть элемент с ID test-2 и magnitude 6.5");
    }

    @Test
    void getById_WhenExists_ShouldReturnEarthquake() {
        System.out.println("\n=== Running getById_WhenExists_ShouldReturnEarthquake ===");
        
        Earthquake result = earthquakesService.getById("test-1");
        
        assertNotNull(result);
        assertEquals("test-1", result.getId());
        assertEquals(5.5, result.getMagnitude());
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        System.out.println("\n=== Running getById_WhenNotExists_ShouldThrowException ===");
        
        assertThrows(org.springframework.web.server.ResponseStatusException.class, 
            () -> earthquakesService.getById("non-existent"));
    }

    @Test
    void avgMagnitude_ShouldCalculateAverage() {
        System.out.println("\n=== Running avgMagnitude_ShouldCalculateAverage ===");
        
        Double result = earthquakesService.avgMagnitude();
        
        assertEquals(6.0, result, 0.001);
    }

    @Test
    void create_ShouldAddNewEarthquake() {
        System.out.println("\n=== Running create_ShouldAddNewEarthquake ===");
        
        Earthquake newEarthquake = new Earthquake();
        newEarthquake.setId("test-3");
        newEarthquake.setMagnitude(4.5);
        newEarthquake.setPlace("Test Location 3");
        
        Earthquake created = earthquakesService.create(newEarthquake);
        
        assertNotNull(created);
        assertEquals("test-3", created.getId());
        
        Earthquake retrieved = earthquakesService.getById("test-3");
        assertNotNull(retrieved);
        assertEquals(4.5, retrieved.getMagnitude());
        
        List<Earthquake> all = earthquakesService.getAll();
        assertEquals(3, all.size());
    }

    @Test
    void delete_ShouldRemoveEarthquake() {

        
        earthquakesService.delete("test-1");
        
        List<Earthquake> all = earthquakesService.getAll();
        assertEquals(1, all.size());
        
        Earthquake remaining = all.get(0);
        assertEquals("test-2", remaining.getId());
        assertEquals(6.5, remaining.getMagnitude());
        
        assertThrows(org.springframework.web.server.ResponseStatusException.class, 
            () -> earthquakesService.getById("test-1"));
    }
}