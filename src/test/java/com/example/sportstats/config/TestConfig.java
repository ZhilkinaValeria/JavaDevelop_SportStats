package com.example.sportstats.config;

import com.example.sportstats.model.Player;
import com.example.sportstats.repository.CommonRepository;
import com.example.sportstats.service.PlayerService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public CommonRepository<Player> testPlayerRepository() {
        return new TestPlayerRepository();
    }
    
    @Bean("playerService")
    @Primary
    public PlayerService testPlayerService() {
        // Используем конструктор с одним параметром
        PlayerService service = new PlayerService(testPlayerRepository());
        return service;
    }

    public static class TestPlayerRepository implements CommonRepository<Player> {
        
        private final Map<String, Player> storage = new HashMap<>();
        
        public TestPlayerRepository() {
            // Создаем тестовых игроков из разных команд
            Player p1 = new Player();
            p1.setId("test-1");
            p1.setName("Test Player 1");
            p1.setTeam("BAL");
            p1.setPosition("Catcher");
            p1.setHeightInches(74);
            p1.setWeightLbs(180);
            p1.setAge(22.99);
            
            Player p2 = new Player();
            p2.setId("test-2");
            p2.setName("Test Player 2");
            p2.setTeam("NYY");
            p2.setPosition("Pitcher");
            p2.setHeightInches(75);
            p2.setWeightLbs(210);
            p2.setAge(28.5);
            
            Player p3 = new Player();
            p3.setId("test-3");
            p3.setName("Test Player 3");
            p3.setTeam("BOS");
            p3.setPosition("Outfielder");
            p3.setHeightInches(72);
            p3.setWeightLbs(190);
            p3.setAge(25.3);
            
            storage.put("test-1", p1);
            storage.put("test-2", p2);
            storage.put("test-3", p3);
        }
        
        public TestPlayerRepository(boolean withCustomData) {
            // Пустой конструктор для возможности создания без данных
        }

        @Override
        public Player save(Player domain) {
            storage.put(domain.getId(), domain);
            return domain;
        }

        @Override
        public Iterable<Player> save(Collection<Player> domains) {
            domains.forEach(d -> storage.put(d.getId(), d));
            return domains;
        }

        @Override
        public void delete(String id) {
            storage.remove(id);
        }

        @Override
        public void delete(Player domain) {
            storage.remove(domain.getId());
        }

        @Override
        public Player findById(String id) {
            return storage.get(id);
        }

        @Override
        public Iterable<Player> findAll() {
            return storage.values();
        }

        @Override
        public boolean exists(String id) {
            return storage.containsKey(id);
        }

        @Override
        public long count() {
            return storage.size();
        }
    }
}