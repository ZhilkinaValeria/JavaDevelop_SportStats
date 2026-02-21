package com.example.earthquakes.repository;

import com.example.earthquakes.model.Earthquake;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("jpa")
public interface EarthquakeJpaRepository extends JpaRepository<Earthquake, String> {
    
    List<Earthquake> findByMagnitudeGreaterThan(Double magnitude);
    
    List<Earthquake> findByPlaceContaining(String place);
    
    @Query("SELECT AVG(e.magnitude) FROM Earthquake e")
    Double findAverageMagnitude();
}