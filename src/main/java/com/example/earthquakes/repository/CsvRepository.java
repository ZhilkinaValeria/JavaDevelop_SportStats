package com.example.earthquakes.repository;

import com.example.earthquakes.model.Earthquake;
import com.example.earthquakes.util.CsvParser;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository("CsvRepository")
@Profile("csv")
public class CsvRepository implements CommonRepository<Earthquake> {
    
    private final Map<String, Earthquake> storage = new ConcurrentHashMap<>();
    private final CsvParser csvParser;
    
    public CsvRepository(CsvParser csvParser) {
        this.csvParser = csvParser;
    }
    
    @PostConstruct
    public void init() {
        List<Earthquake> earthquakes = csvParser.parseCsv("earthquakes.csv");
        earthquakes.forEach(e -> storage.put(e.getId(), e));
    }
    
    @Override
    public Earthquake save(Earthquake domain) {
        if (domain == null) {
            throw new IllegalArgumentException("Earthquake cannot be null");
        }
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