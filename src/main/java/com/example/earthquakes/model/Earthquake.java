package com.example.earthquakes.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "earthquakes")
public class Earthquake {
    
    @Id
    private String id;
    
    private LocalDateTime time;
    private Double latitude;
    private Double longitude;
    private Double depth;
    private Double magnitude;
    private String place;
    
    @Column(name = "magnitude_type")
    private String magType;
    
    // Конструкторы
    public Earthquake() {}
    
    public Earthquake(String id, LocalDateTime time, Double latitude, Double longitude, 
                      Double depth, Double magnitude, String place, String magType) {
        this.id = id;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth = depth;
        this.magnitude = magnitude;
        this.place = place;
        this.magType = magType;
    }
    
    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public Double getDepth() { return depth; }
    public void setDepth(Double depth) { this.depth = depth; }
    
    public Double getMagnitude() { return magnitude; }
    public void setMagnitude(Double magnitude) { this.magnitude = magnitude; }
    
    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }
    
    public String getMagType() { return magType; }
    public void setMagType(String magType) { this.magType = magType; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Earthquake that = (Earthquake) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}