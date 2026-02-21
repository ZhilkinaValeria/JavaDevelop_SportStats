package com.example.earthquakes.config;

import com.example.earthquakes.model.Earthquake;
import com.example.earthquakes.repository.CommonRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public CommonRepository<Earthquake> testEarthquakeRepository() {
        return new TestEarthquakeRepository();
    }

    public static class TestEarthquakeRepository implements CommonRepository<Earthquake> {
        
        private final Map<String, Earthquake> storage = new HashMap<>();
        
        public TestEarthquakeRepository() {
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
        public Earthquake save(Earthquake domain) {
            storage.put(domain.getId(), domain);
            return domain;
        }

        @Override
        public Iterable<Earthquake> save(Collection<Earthquake> domains) {
            domains.forEach(d -> storage.put(d.getId(), d));
            return domains;
        }

        @Override
        public void delete(String id) {
            storage.remove(id);
        }

        @Override
        public void delete(Earthquake domain) {
            storage.remove(domain.getId());
        }

        @Override
        public Earthquake findById(String id) {
            return storage.get(id);
        }

        @Override
        public Iterable<Earthquake> findAll() {
            return storage.values();
        }

        @Override
        public boolean exists(String id) {
            return storage.containsKey(id);
        }

        @Override
        public long count() {
            return storage.size();
        }
    }
}