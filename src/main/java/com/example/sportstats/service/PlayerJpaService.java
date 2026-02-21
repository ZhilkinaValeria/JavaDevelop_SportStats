package com.example.sportstats.service;

import com.example.sportstats.model.Player;
import com.example.sportstats.repository.PlayerJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Profile("jpa")
public class PlayerJpaService {
    
    private final PlayerJpaRepository jpaRepository;
    
    public PlayerJpaService(PlayerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    // Базовые CRUD операции
    public List<Player> getAll() {
        return jpaRepository.findAll();
    }
    
    public Player getById(String id) {
        return jpaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
    }
    
    public Player create(Player player) {
        if (jpaRepository.existsById(player.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Player already exists");
        }
        return jpaRepository.save(player);
    }
    
    public Player update(Player player) {
        if (!jpaRepository.existsById(player.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
        return jpaRepository.save(player);
    }
    
    public void delete(String id) {
        if (!jpaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
        jpaRepository.deleteById(id);
    }
    
    // Специализированные методы для работы с игроками
    
    public List<Player> getPlayersByTeam(String team) {
        return jpaRepository.findByTeam(team);
    }
    
    public List<Player> getPlayersByPosition(String position) {
        return jpaRepository.findByPosition(position);
    }
    
    public List<Player> getPlayersByAgeRange(Double minAge, Double maxAge) {
        return jpaRepository.findByAgeBetween(minAge, maxAge);
    }
    
    public List<Player> getPlayersByMinHeight(Integer minHeight) {
        return jpaRepository.findByHeightInchesGreaterThan(minHeight);
    }
    
    public List<Player> getPlayersByMinWeight(Integer minWeight) {
        return jpaRepository.findByWeightLbsGreaterThan(minWeight);
    }
    
    public List<Player> searchPlayersByName(String namePattern) {
        return jpaRepository.findByNameContaining(namePattern);
    }
    
    public List<Player> getPlayersByTeamAndPosition(String team, String position) {
        return jpaRepository.findByTeamAndPosition(team, position);
    }
    
    // Статистические методы
    
    public Double getAverageAge() {
        Double avg = jpaRepository.findAverageAge();
        return avg != null ? avg : 0.0;
    }
    
    public Double getAverageHeight() {
        Double avg = jpaRepository.findAverageHeightInches();
        return avg != null ? avg : 0.0;
    }
    
    public Double getAverageWeight() {
        Double avg = jpaRepository.findAverageWeightLbs();
        return avg != null ? avg : 0.0;
    }
    
    public Map<String, Long> getPlayersCountByTeam() {
        List<Object[]> results = jpaRepository.countPlayersByTeam();
        Map<String, Long> counts = new HashMap<>();
        for (Object[] result : results) {
            counts.put((String) result[0], (Long) result[1]);
        }
        return counts;
    }
    
    public Map<String, Long> getPlayersCountByPosition() {
        List<Object[]> results = jpaRepository.countPlayersByPosition();
        Map<String, Long> counts = new HashMap<>();
        for (Object[] result : results) {
            counts.put((String) result[0], (Long) result[1]);
        }
        return counts;
    }
    
    public Map<String, Integer> getHeightStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("max", jpaRepository.findMaxHeight());
        stats.put("min", jpaRepository.findMinHeight());
        return stats;
    }
    
    public Map<String, Integer> getWeightStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("max", jpaRepository.findMaxWeight());
        stats.put("min", jpaRepository.findMinWeight());
        return stats;
    }
    
    public List<Player> getYoungestPlayers() {
        return jpaRepository.findYoungestPlayers();
    }
    
    public List<Player> getOldestPlayers() {
        return jpaRepository.findOldestPlayers();
    }
    
    public Map<String, Object> getTeamStatistics(String teamCode) {  // Добавьте параметр
        List<Player> teamPlayers = getPlayersByTeam(teamCode);
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("team", teamCode);
        stats.put("totalPlayers", teamPlayers.size());
        stats.put("averageAge", teamPlayers.stream()
                .filter(p -> p.getAge() != null)
                .mapToDouble(Player::getAge)
                .average()
                .orElse(0.0));
        stats.put("averageHeight", teamPlayers.stream()
                .filter(p -> p.getHeightInches() != null)
                .mapToInt(Player::getHeightInches)
                .average()
                .orElse(0.0));
        stats.put("averageWeight", teamPlayers.stream()
                .filter(p -> p.getWeightLbs() != null)
                .mapToInt(Player::getWeightLbs)
                .average()
                .orElse(0.0));
        
        return stats;
    }
    
    public List<Player> getPlayersWithHighBmi(Double bmiThreshold) {
        return jpaRepository.findPlayersWithBmiGreaterThan(bmiThreshold);
    }
    
    public List<Player> getTop10Tallest() {
        return jpaRepository.findTop10ByOrderByHeightInchesDesc();
    }
    
    public List<Player> getTop10Heaviest() {
        return jpaRepository.findTop10ByOrderByWeightLbsDesc();
    }
    
    // Комплексная статистика по всем игрокам
    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalPlayers", jpaRepository.count());
        stats.put("averageAge", getAverageAge());
        stats.put("averageHeightInches", getAverageHeight());
        stats.put("averageWeightLbs", getAverageWeight());
        stats.put("heightStats", getHeightStats());
        stats.put("weightStats", getWeightStats());
        stats.put("playersByTeam", getPlayersCountByTeam());
        stats.put("playersByPosition", getPlayersCountByPosition());
        
        return stats;
    }
}