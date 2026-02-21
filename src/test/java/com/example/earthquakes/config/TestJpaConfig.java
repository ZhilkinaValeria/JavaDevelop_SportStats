package com.example.earthquakes.config;

import com.example.earthquakes.model.Earthquake;
import com.example.earthquakes.service.EarthquakesJpaService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.*;

@TestConfiguration
@Profile("test-jpa")
public class TestJpaConfig {

    @Bean("earthquakesJpaService")
    public EarthquakesJpaService testEarthquakesJpaService() {
        return new EarthquakesJpaService(null) {
            private final Map<String, Earthquake> storage = new HashMap<>();
            
            {
                Earthquake e1 = new Earthquake();
                e1.setId("test-1");
                e1.setMagnitude(5.5);
                e1.setPlace("Test Location 1");
                
                Earthquake e2 = new Earthquake();
                e2.setId("test-2");
                e2.setMagnitude(6.5);
                e2.setPlace("Test Location 2");
                
                storage.put("test-1", e1);
                storage.put("test-2", e2);
            }
            
            @Override
            public List<Earthquake> getAll() {
                return new ArrayList<>(storage.values());
            }
            
            @Override
            public Earthquake getById(String id) {
                return storage.get(id);
            }
            
            @Override
            public Double avgMagnitude() {
                return 6.0;
            }
            
            @Override
            public Earthquake create(Earthquake earthquake) {
                storage.put(earthquake.getId(), earthquake);
                return earthquake;
            }
            
            @Override
            public Earthquake update(Earthquake earthquake) {  // ИСПРАВЛЕНО: возвращаем Earthquake
                storage.put(earthquake.getId(), earthquake);
                return earthquake;  // ИСПРАВЛЕНО: возвращаем объект
            }
            
            @Override
            public void delete(String id) {
                storage.remove(id);
            }
        };
    }
}