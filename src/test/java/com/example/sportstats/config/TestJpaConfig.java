package com.example.sportstats.config;

import com.example.sportstats.model.Player;
import com.example.sportstats.service.PlayerJpaService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.*;

@TestConfiguration
@Profile("test-jpa")
public class TestJpaConfig {

    @Bean("playerJpaService")
    public PlayerJpaService testPlayerJpaService() {
        return new PlayerJpaService(null) {
            private final Map<String, Player> storage = new HashMap<>();
            
            {
                // Тестовый игрок 1 - BAL, Catcher
                Player p1 = new Player();
                p1.setId("test-1");
                p1.setName("Test Catcher");
                p1.setTeam("BAL");
                p1.setPosition("Catcher");
                p1.setHeightInches(74);
                p1.setWeightLbs(180);
                p1.setAge(22.99);
                
                // Тестовый игрок 2 - NYY, Pitcher
                Player p2 = new Player();
                p2.setId("test-2");
                p2.setName("Test Pitcher");
                p2.setTeam("NYY");
                p2.setPosition("Starting Pitcher");
                p2.setHeightInches(75);
                p2.setWeightLbs(210);
                p2.setAge(28.5);
                
                // Тестовый игрок 3 - BOS, Outfielder
                Player p3 = new Player();
                p3.setId("test-3");
                p3.setName("Test Outfielder");
                p3.setTeam("BOS");
                p3.setPosition("Outfielder");
                p3.setHeightInches(72);
                p3.setWeightLbs(190);
                p3.setAge(25.3);
                
                // Тестовый игрок 4 - BAL, First Baseman (для проверки фильтрации по команде)
                Player p4 = new Player();
                p4.setId("test-4");
                p4.setName("Test First Baseman");
                p4.setTeam("BAL");
                p4.setPosition("First Baseman");
                p4.setHeightInches(76);
                p4.setWeightLbs(220);
                p4.setAge(30.1);
                
                storage.put("test-1", p1);
                storage.put("test-2", p2);
                storage.put("test-3", p3);
                storage.put("test-4", p4);
            }
            
            @Override
            public List<Player> getAll() {
                return new ArrayList<>(storage.values());
            }
            
            @Override
            public Player getById(String id) {
                return storage.get(id);
            }
            
            @Override
            public Double getAverageAge() {
                return storage.values().stream()
                        .mapToDouble(Player::getAge)
                        .average()
                        .orElse(0.0);
            }
            
            @Override
            public Double getAverageHeight() {
                return storage.values().stream()
                        .filter(p -> p.getHeightInches() != null)
                        .mapToInt(Player::getHeightInches)
                        .average()
                        .orElse(0.0);
            }
            
            @Override
            public Double getAverageWeight() {
                return storage.values().stream()
                        .filter(p -> p.getWeightLbs() != null)
                        .mapToInt(Player::getWeightLbs)
                        .average()
                        .orElse(0.0);
            }
            
            @Override
            public List<Player> getPlayersByTeam(String team) {
                return storage.values().stream()
                        .filter(p -> team.equals(p.getTeam()))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            }
            
            @Override
            public List<Player> getPlayersByPosition(String position) {
                return storage.values().stream()
                        .filter(p -> position.equals(p.getPosition()))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            }
            
            @Override
            public Player create(Player player) {
                storage.put(player.getId(), player);
                return player;
            }
            
            @Override
            public Player update(Player player) {
                storage.put(player.getId(), player);
                return player;
            }
            
            @Override
            public void delete(String id) {
                storage.remove(id);
            }
            
            @Override
            public Map<String, Long> getPlayersCountByTeam() {
                Map<String, Long> counts = new HashMap<>();
                storage.values().forEach(p -> 
                    counts.merge(p.getTeam(), 1L, Long::sum)
                );
                return counts;
            }
            
            @Override
            public List<Player> getTop10Tallest() {
                return storage.values().stream()
                        .sorted(Comparator.comparing(Player::getHeightInches, Comparator.nullsLast(Comparator.reverseOrder())))
                        .limit(10)
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            }
            
            @Override
            public List<Player> getPlayersWithHighBmi(Double bmiThreshold) {
                return storage.values().stream()
                        .filter(p -> p.getBmi() != null && p.getBmi() > bmiThreshold)
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            }
            
            @Override
            public Map<String, Object> getTeamStatistics(String teamCode) {
                List<Player> teamPlayers = getPlayersByTeam(teamCode);
                Map<String, Object> stats = new HashMap<>();
                
                stats.put("team", teamCode);
                stats.put("totalPlayers", teamPlayers.size());
                stats.put("averageAge", teamPlayers.stream()
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
        };
    }
}