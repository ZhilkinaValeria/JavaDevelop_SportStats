package com.example.sportstats.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "players")
public class Player {
    
    @Id
    private String id; 
    
    private String name;
    private String team;      
    private String position;
    private Integer heightInches;
    private Integer weightLbs;
    private Double age;
    

    @Transient
    private Double heightMeters;
    
    @Transient
    private Double weightKg;
    
    @Transient
    private Double bmi; // Индекс массы тела
    
    // Конструкторы
    public Player() {}
    
    public Player(String name, String team, String position, Integer heightInches, 
                  Integer weightLbs, Double age) {
        this.id = generateId(name, team);
        this.name = name;
        this.team = team;
        this.position = position;
        this.heightInches = heightInches;
        this.weightLbs = weightLbs;
        this.age = age;
    }
    
    private String generateId(String name, String team) {
        return team + "_" + name.replaceAll("[^a-zA-Z0-9]", "_");
    }
    
    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name;
        if (team != null) {
            this.id = generateId(name, team);
        }
    }
    
    public String getTeam() { return team; }
    public void setTeam(String team) { 
        this.team = team;
        if (name != null) {
            this.id = generateId(name, team);
        }
    }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public Integer getHeightInches() { return heightInches; }
    public void setHeightInches(Integer heightInches) { 
        this.heightInches = heightInches;
        updateDerivedFields();
    }
    
    public Integer getWeightLbs() { return weightLbs; }
    public void setWeightLbs(Integer weightLbs) { 
        this.weightLbs = weightLbs;
        updateDerivedFields();
    }
    
    public Double getAge() { return age; }
    public void setAge(Double age) { this.age = age; }
    
    // Вычисляемые поля
    public Double getHeightMeters() {
        if (heightInches != null) {
            return heightInches * 0.0254; // 1 дюйм = 0.0254 метра
        }
        return null;
    }
    
    public Double getWeightKg() {
        if (weightLbs != null) {
            return weightLbs * 0.453592; // 1 фунт = 0.453592 кг
        }
        return null;
    }
    
    public Double getBmi() {
        if (heightInches != null && weightLbs != null && heightInches > 0) {
            // BMI = (weightLbs * 703) / (heightInches^2)
            return (weightLbs * 703.0) / (heightInches * heightInches);
        }
        return null;
    }
    
    private void updateDerivedFields() {
        // Триггер для обновления производных полей
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}