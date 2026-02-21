package com.example.earthquakes.controller;

import com.example.earthquakes.model.Earthquake;
import com.example.earthquakes.service.EarthquakesService;
import com.example.earthquakes.service.EarthquakesJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    
    @Autowired
    public EarthquakeController(
            @Qualifier("earthquakesService") EarthquakesService earthquakesService,
            @Qualifier("earthquakesJpaService") EarthquakesJpaService earthquakesJpaService) {
        this.earthquakesService = earthquakesService;
        this.earthquakesJpaService = earthquakesJpaService;
    }
    
    private EarthquakesService getService() {
        return earthquakesService != null ? earthquakesService : earthquakesJpaService;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Earthquake> getAll() {
        return getService().getAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Earthquake getById(@PathVariable String id) {
        return getService().getById(id);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")  // Только ADMIN
    public ResponseEntity<Earthquake> create(@RequestBody Earthquake earthquake) {
        Earthquake created = getService().create(earthquake);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // Только ADMIN
    public Earthquake update(@PathVariable String id, @RequestBody Earthquake earthquake) {
        earthquake.setId(id);
        return getService().update(earthquake);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // Только ADMIN
    public ResponseEntity<Void> delete(@PathVariable String id) {
        getService().delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/stats/avg-magnitude")
    @PreAuthorize("permitAll()")  // Доступно всем
    public Double getAvgMagnitude() {
        return getService().avgMagnitude();
    }
}