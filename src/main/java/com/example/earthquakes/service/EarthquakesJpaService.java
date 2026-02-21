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
public class EarthquakesJpaService extends EarthquakesService {
    
    private final EarthquakeJpaRepository jpaRepository;
    
    public EarthquakesJpaService(EarthquakeJpaRepository jpaRepository) {
        super(null, null);
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public List<Earthquake> getAll() {
        return jpaRepository.findAll();
    }
    
    @Override
    public Earthquake getById(String id) {
        return jpaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Earthquake not found"));
    }
    
    @Override
    public Earthquake create(Earthquake earthquake) {
        if (jpaRepository.existsById(earthquake.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Earthquake already exists");
        }
        return jpaRepository.save(earthquake);
    }
    
    @Override
    public Earthquake update(Earthquake earthquake) {
        if (!jpaRepository.existsById(earthquake.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Earthquake not found");
        }
        return jpaRepository.save(earthquake);
    }
    
    @Override
    public void delete(String id) {
        if (!jpaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Earthquake not found");
        }
        jpaRepository.deleteById(id);
    }
    
    @Override
    public Double avgMagnitude() {
        Double avg = jpaRepository.findAverageMagnitude();
        return avg != null ? avg : 0.0;
    }
}