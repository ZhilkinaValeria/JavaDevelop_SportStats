package com.example.earthquakes.controller;

import com.example.earthquakes.model.Earthquake;
import com.example.earthquakes.service.EarthquakesService;
import com.example.earthquakes.service.EarthquakesJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/earthquakes")
public class EarthquakeController {
    
    private final EarthquakesService earthquakesService;
    private final EarthquakesJpaService earthquakesJpaService;
    private final boolean isJpaMode;
    
    @Autowired
    public EarthquakeController(
            @Autowired(required = false) EarthquakesService earthquakesService,
            @Autowired(required = false) EarthquakesJpaService earthquakesJpaService) {
        this.earthquakesService = earthquakesService;
        this.earthquakesJpaService = earthquakesJpaService;
        this.isJpaMode = earthquakesJpaService != null;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Earthquake> getAll() {
        if (isJpaMode) {
            return earthquakesJpaService.getAll();
        }
        return earthquakesService.getAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Earthquake getById(@PathVariable String id) {
        if (isJpaMode) {
            return earthquakesJpaService.getById(id);
        }
        return earthquakesService.getById(id);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Earthquake> create(@RequestBody Earthquake earthquake) {
        Earthquake created;
        if (isJpaMode) {
            created = earthquakesJpaService.create(earthquake);
        } else {
            created = earthquakesService.create(earthquake);
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Earthquake update(@PathVariable String id, @RequestBody Earthquake earthquake) {
        earthquake.setId(id);
        if (isJpaMode) {
            return earthquakesJpaService.update(earthquake);
        }
        return earthquakesService.update(earthquake);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (isJpaMode) {
            earthquakesJpaService.delete(id);
        } else {
            earthquakesService.delete(id);
        }
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/stats/avg-magnitude")
    @PreAuthorize("permitAll()")
    public Double getAvgMagnitude() {
        if (isJpaMode) {
            return earthquakesJpaService.avgMagnitude();
        }
        return earthquakesService.avgMagnitude();
    }
}