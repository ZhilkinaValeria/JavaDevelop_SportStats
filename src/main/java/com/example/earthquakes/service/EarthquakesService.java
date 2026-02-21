package com.example.earthquakes.service;

import com.example.earthquakes.model.Earthquake;
import com.example.earthquakes.repository.CommonRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Profile({"csv", "jdbc"})
public class EarthquakesService {
    
    protected CommonRepository<Earthquake> repository;
    
    public EarthquakesService(
            @Qualifier("CsvRepository") CommonRepository<Earthquake> csvRepo,
            @Qualifier("EarthquakeJdbcRepository") CommonRepository<Earthquake> jdbcRepo) {
        // Репозиторий будет выбран через профили
        this.repository = csvRepo; // По умолчанию, переопределяется через init
    }
    
    void init(CommonRepository<Earthquake> repository) {
        if (repository != null) {
            this.repository = repository;
        }
    }
    
    public List<Earthquake> getAll() {
        return (List<Earthquake>) repository.findAll();
    }
    
    public Earthquake getById(String id) {
        Earthquake earthquake = repository.findById(id);
        if (earthquake == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Earthquake not found");
        }
        return earthquake;
    }
    
    public Earthquake create(Earthquake earthquake) {
        if (earthquake.getId() == null || earthquake.getId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is required");
        }
        if (repository.exists(earthquake.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Earthquake already exists");
        }
        return repository.save(earthquake);
    }
    
    public Earthquake update(Earthquake earthquake) {
        if (!repository.exists(earthquake.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Earthquake not found");
        }
        return repository.save(earthquake);
    }
    
    public void delete(String id) {
        if (!repository.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Earthquake not found");
        }
        repository.delete(id);
    }
    
    public Double avgMagnitude() {
        List<Earthquake> earthquakes = (List<Earthquake>) repository.findAll();
        return earthquakes.stream()
                .mapToDouble(Earthquake::getMagnitude)
                .average()
                .orElse(0.0);
    }
}