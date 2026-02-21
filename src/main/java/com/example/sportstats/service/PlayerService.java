package com.example.sportstats.service;

import com.example.sportstats.model.Player;
import com.example.sportstats.repository.CommonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Profile({"csv", "jdbc"})
public class PlayerService {
    
    protected CommonRepository<Player> repository;
    
    @Autowired
    public PlayerService(CommonRepository<Player> repository) {
        this.repository = repository;
    }
    
    // ========== БАЗОВЫЕ CRUD ==========
    
    public List<Player> getAll() {
        List<Player> result = new ArrayList<>();
        repository.findAll().forEach(result::add);
        return result;
    }
    
    public Player getById(String id) {
        Player player = repository.findById(id);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
        return player;
    }
    
    public Player create(Player player) {
        if (player.getId() == null || player.getId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is required");
        }
        if (repository.exists(player.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Player already exists");
        }
        return repository.save(player);
    }
    
    public Player update(Player player) {
        if (!repository.exists(player.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
        return repository.save(player);
    }
    
    public void delete(String id) {
        if (!repository.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
        repository.delete(id);
    }
    
    // ========== ФИЛЬТРАЦИЯ ==========
    
    public List<Player> getPlayersByTeam(String team) {
        return getAll().stream()
                .filter(p -> team.equals(p.getTeam()))
                .collect(Collectors.toList());
    }
    
    public List<Player> getPlayersByPosition(String position) {
        return getAll().stream()
                .filter(p -> position.equals(p.getPosition()))
                .collect(Collectors.toList());
    }
    
    public List<Player> getPlayersByAgeRange(Double minAge, Double maxAge) {
        return getAll().stream()
                .filter(p -> p.getAge() != null)
                .filter(p -> p.getAge() >= minAge && p.getAge() <= maxAge)
                .collect(Collectors.toList());
    }
    
    // Новые методы фильтрации
    public List<Player> getPlayersByMinHeight(Integer minHeight) {
        return getAll().stream()
                .filter(p -> p.getHeightInches() != null && p.getHeightInches() >= minHeight)
                .collect(Collectors.toList());
    }
    
    public List<Player> getPlayersByMinWeight(Integer minWeight) {
        return getAll().stream()
                .filter(p -> p.getWeightLbs() != null && p.getWeightLbs() >= minWeight)
                .collect(Collectors.toList());
    }
    
    public List<Player> getPlayersByTeamAndPosition(String team, String position) {
        return getAll().stream()
                .filter(p -> team.equals(p.getTeam()) && position.equals(p.getPosition()))
                .collect(Collectors.toList());
    }
    
    public List<Player> searchPlayersByName(String name) {
        return getAll().stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    // ========== СТАТИСТИКА ==========
    
    public Double getAverageAge() {
        return getAll().stream()
                .filter(p -> p.getAge() != null)
                .mapToDouble(Player::getAge)
                .average()
                .orElse(0.0);
    }
    
    public Double getAverageHeight() {
        return getAll().stream()
                .filter(p -> p.getHeightInches() != null)
                .mapToInt(Player::getHeightInches)
                .average()
                .orElse(0.0);
    }
    
    public Double getAverageWeight() {
        return getAll().stream()
                .filter(p -> p.getWeightLbs() != null)
                .mapToInt(Player::getWeightLbs)
                .average()
                .orElse(0.0);
    }
    
    public Map<String, Long> getPlayersCountByTeam() {
        return getAll().stream()
                .collect(Collectors.groupingBy(Player::getTeam, Collectors.counting()));
    }
    
    public Map<String, Long> getPlayersCountByPosition() {
        return getAll().stream()
                .collect(Collectors.groupingBy(Player::getPosition, Collectors.counting()));
    }
    
    public Map<String, Integer> getHeightStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("max", getAll().stream()
                .filter(p -> p.getHeightInches() != null)
                .mapToInt(Player::getHeightInches)
                .max()
                .orElse(0));
        stats.put("min", getAll().stream()
                .filter(p -> p.getHeightInches() != null)
                .mapToInt(Player::getHeightInches)
                .min()
                .orElse(0));
        return stats;
    }
    
    public Map<String, Integer> getWeightStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("max", getAll().stream()
                .filter(p -> p.getWeightLbs() != null)
                .mapToInt(Player::getWeightLbs)
                .max()
                .orElse(0));
        stats.put("min", getAll().stream()
                .filter(p -> p.getWeightLbs() != null)
                .mapToInt(Player::getWeightLbs)
                .min()
                .orElse(0));
        return stats;
    }
    
    public List<Player> getYoungestPlayers() {
        Double minAge = getAll().stream()
                .filter(p -> p.getAge() != null)
                .mapToDouble(Player::getAge)
                .min()
                .orElse(0.0);
        
        return getAll().stream()
                .filter(p -> p.getAge() != null && p.getAge().equals(minAge))
                .collect(Collectors.toList());
    }
    
    public List<Player> getOldestPlayers() {
        Double maxAge = getAll().stream()
                .filter(p -> p.getAge() != null)
                .mapToDouble(Player::getAge)
                .max()
                .orElse(0.0);
        
        return getAll().stream()
                .filter(p -> p.getAge() != null && p.getAge().equals(maxAge))
                .collect(Collectors.toList());
    }
    
    public Map<String, Object> getTeamStatistics(String teamCode) {
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
        return getAll().stream()
                .filter(p -> p.getBmi() != null && p.getBmi() > bmiThreshold)
                .collect(Collectors.toList());
    }
    
    public List<Player> getTop10Tallest() {
        return getAll().stream()
                .filter(p -> p.getHeightInches() != null)
                .sorted(Comparator.comparing(Player::getHeightInches).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
    
    public List<Player> getTop10Heaviest() {
        return getAll().stream()
                .filter(p -> p.getWeightLbs() != null)
                .sorted(Comparator.comparing(Player::getWeightLbs).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
    
    public Map<String, Object> getOverallStatistics() {
        List<Player> allPlayers = getAll();
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalPlayers", allPlayers.size());
        stats.put("averageAge", getAverageAge());
        stats.put("averageHeight", getAverageHeight());
        stats.put("averageWeight", getAverageWeight());
        stats.put("playersByTeam", getPlayersCountByTeam());
        stats.put("playersByPosition", getPlayersCountByPosition());
        stats.put("heightStats", getHeightStats());
        stats.put("weightStats", getWeightStats());
        
        return stats;
    }
}