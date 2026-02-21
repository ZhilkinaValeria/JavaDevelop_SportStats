package com.example.earthquakes.service;

import com.example.earthquakes.model.Earthquake;
import com.example.earthquakes.repository.EarthquakeJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Profile("jpa")
public class EarthquakesJpaService {  // Не наследуем EarthquakesService
    
    private final EarthquakeJpaRepository jpaRepository;
    
    public EarthquakesJpaService(EarthquakeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    public List<Earthquake> getAll() {
        return jpaRepository.findAll();
    }
    
    public Earthquake getById(String id) {
        return jpaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Earthquake not found"));
    }
    
    public Earthquake create(Earthquake earthquake) {
        if (jpaRepository.existsById(earthquake.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Earthquake already exists");
        }
        return jpaRepository.save(earthquake);
    }
    
    public Earthquake update(Earthquake earthquake) {
        if (!jpaRepository.existsById(earthquake.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Earthquake not found");
        }
        return jpaRepository.save(earthquake);
    }
    
    public void delete(String id) {
        if (!jpaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Earthquake not found");
        }
        jpaRepository.deleteById(id);
    }
    
    public Double avgMagnitude() {
        Double avg = jpaRepository.findAverageMagnitude();
        return avg != null ? avg : 0.0;
    }
}