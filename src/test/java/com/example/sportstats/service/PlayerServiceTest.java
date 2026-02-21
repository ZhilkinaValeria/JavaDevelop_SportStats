package com.example.sportstats.service;

import com.example.sportstats.config.TestConfig;
import com.example.sportstats.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;
    
    @Autowired
    private TestConfig.TestPlayerRepository testRepository;
    
    @BeforeEach
    void setUp() {
        // Сбрасываем репозиторий в исходное состояние перед каждым тестом
        List<Player> all = playerService.getAll();
        for (Player p : all) {
            if (!"test-1".equals(p.getId()) && !"test-2".equals(p.getId()) && !"test-3".equals(p.getId())) {
                playerService.delete(p.getId());
            }
        }
        
        // Убеждаемся, что test-1, test-2 и test-3 существуют
        ensurePlayerExists("test-1", "Test Player 1", "BAL", "Catcher", 74, 180, 22.99);
        ensurePlayerExists("test-2", "Test Player 2", "NYY", "Starting Pitcher", 75, 210, 28.5);
        ensurePlayerExists("test-3", "Test Player 3", "BOS", "Outfielder", 72, 190, 25.3);
        
        System.out.println("\n=== @BeforeEach - Reset repository to initial state ===");
        List<Player> afterReset = playerService.getAll();
        System.out.println("Repository now contains " + afterReset.size() + " players:");
        afterReset.forEach(p -> 
            System.out.println("  - ID: '" + p.getId() + "', Name: " + p.getName() + 
                              ", Team: " + p.getTeam() + ", Age: " + p.getAge()));
    }
    
    private void ensurePlayerExists(String id, String name, String team, String position, 
                                    Integer height, Integer weight, Double age) {
        try {
            playerService.getById(id);
        } catch (ResponseStatusException e) {
            // Если игрок не существует, создаем его
            Player player = new Player();
            player.setId(id);
            player.setName(name);
            player.setTeam(team);
            player.setPosition(position);
            player.setHeightInches(height);
            player.setWeightLbs(weight);
            player.setAge(age);
            playerService.create(player);
        }
    }

    @Test
    void getAll_ShouldReturnAllTestPlayers() {
        System.out.println("\n=== Running getAll_ShouldReturnAllTestPlayers ===");
        
        List<Player> result = playerService.getAll();
        
        assertNotNull(result);
        assertEquals(3, result.size());
        
        boolean hasPlayer1 = result.stream()
            .anyMatch(p -> "test-1".equals(p.getId()) && 
                           "Test Player 1".equals(p.getName()) && 
                           "BAL".equals(p.getTeam()) &&
                           22.99 == p.getAge());
        boolean hasPlayer2 = result.stream()
            .anyMatch(p -> "test-2".equals(p.getId()) && 
                           "Test Player 2".equals(p.getName()) && 
                           "NYY".equals(p.getTeam()) &&
                           28.5 == p.getAge());
        boolean hasPlayer3 = result.stream()
            .anyMatch(p -> "test-3".equals(p.getId()) && 
                           "Test Player 3".equals(p.getName()) && 
                           "BOS".equals(p.getTeam()) &&
                           25.3 == p.getAge());
        
        assertTrue(hasPlayer1, "Должен быть элемент с ID test-1");
        assertTrue(hasPlayer2, "Должен быть элемент с ID test-2");
        assertTrue(hasPlayer3, "Должен быть элемент с ID test-3");
    }

    @Test
    void getById_WhenExists_ShouldReturnPlayer() {
        System.out.println("\n=== Running getById_WhenExists_ShouldReturnPlayer ===");
        
        Player result = playerService.getById("test-1");
        
        assertNotNull(result);
        assertEquals("test-1", result.getId());
        assertEquals("Test Player 1", result.getName());
        assertEquals("BAL", result.getTeam());
        assertEquals("Catcher", result.getPosition());
        assertEquals(74, result.getHeightInches());
        assertEquals(180, result.getWeightLbs());
        assertEquals(22.99, result.getAge());
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        System.out.println("\n=== Running getById_WhenNotExists_ShouldThrowException ===");
        
        assertThrows(ResponseStatusException.class, 
            () -> playerService.getById("non-existent"));
    }

    @Test
    void getAverageAge_ShouldCalculateAverage() {
        System.out.println("\n=== Running getAverageAge_ShouldCalculateAverage ===");
        
        Double result = playerService.getAverageAge();
        
        // (22.99 + 28.5 + 25.3) / 3 = 25.59666...
        assertEquals(25.6, result, 0.1);
    }

    @Test
    void getAverageHeight_ShouldCalculateAverage() {
        System.out.println("\n=== Running getAverageHeight_ShouldCalculateAverage ===");
        
        Double result = playerService.getAverageHeight();
        
        // (74 + 75 + 72) / 3 = 73.666...
        assertEquals(73.67, result, 0.1);
    }

    @Test
    void getAverageWeight_ShouldCalculateAverage() {
        System.out.println("\n=== Running getAverageWeight_ShouldCalculateAverage ===");
        
        Double result = playerService.getAverageWeight();
        
        // (180 + 210 + 190) / 3 = 193.333...
        assertEquals(193.33, result, 0.1);
    }

    @Test
    void create_ShouldAddNewPlayer() {
        System.out.println("\n=== Running create_ShouldAddNewPlayer ===");
        
        Player newPlayer = new Player();
        newPlayer.setId("test-4");
        newPlayer.setName("New Test Player");
        newPlayer.setTeam("CHC");
        newPlayer.setPosition("First Baseman");
        newPlayer.setHeightInches(76);
        newPlayer.setWeightLbs(220);
        newPlayer.setAge(27.5);
        
        Player created = playerService.create(newPlayer);
        
        assertNotNull(created);
        assertEquals("test-4", created.getId());
        assertEquals("New Test Player", created.getName());
        assertEquals("CHC", created.getTeam());
        assertEquals("First Baseman", created.getPosition());
        assertEquals(76, created.getHeightInches());
        assertEquals(220, created.getWeightLbs());
        assertEquals(27.5, created.getAge());
        
        Player retrieved = playerService.getById("test-4");
        assertNotNull(retrieved);
        assertEquals(27.5, retrieved.getAge());
        
        List<Player> all = playerService.getAll();
        assertEquals(4, all.size());
    }

    @Test
    void delete_ShouldRemovePlayer() {
        System.out.println("\n=== Running delete_ShouldRemovePlayer ===");
        
        playerService.delete("test-1");
        
        List<Player> all = playerService.getAll();
        assertEquals(2, all.size());
        
        assertThrows(ResponseStatusException.class, 
            () -> playerService.getById("test-1"));
    }

    @Test
    void getPlayersByTeam_ShouldReturnTeamPlayers() {
        System.out.println("\n=== Running getPlayersByTeam_ShouldReturnTeamPlayers ===");
        
        List<Player> balPlayers = playerService.getPlayersByTeam("BAL");
        
        assertNotNull(balPlayers);
        assertEquals(1, balPlayers.size());
        assertEquals("test-1", balPlayers.get(0).getId());
        assertEquals("BAL", balPlayers.get(0).getTeam());
    }

    @Test
    void getPlayersByPosition_ShouldReturnPositionPlayers() {
        System.out.println("\n=== Running getPlayersByPosition_ShouldReturnPositionPlayers ===");
        
        List<Player> catchers = playerService.getPlayersByPosition("Catcher");
        
        assertNotNull(catchers);
        assertEquals(1, catchers.size());
        assertEquals("test-1", catchers.get(0).getId());
        assertEquals("Catcher", catchers.get(0).getPosition());
    }

    @Test
    void getPlayersByAgeRange_ShouldReturnFilteredPlayers() {
        System.out.println("\n=== Running getPlayersByAgeRange_ShouldReturnFilteredPlayers ===");
        
        List<Player> result = playerService.getPlayersByAgeRange(23.0, 27.0);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-3", result.get(0).getId()); // 25.3
    }

    @Test
    void getPlayersByMinHeight_ShouldReturnTallPlayers() {
        System.out.println("\n=== Running getPlayersByMinHeight_ShouldReturnTallPlayers ===");
        
        List<Player> result = playerService.getPlayersByMinHeight(73);
        
        assertNotNull(result);
        assertEquals(2, result.size()); // test-1 (74) и test-2 (75)
        assertTrue(result.stream().anyMatch(p -> "test-1".equals(p.getId())));
        assertTrue(result.stream().anyMatch(p -> "test-2".equals(p.getId())));
    }

    @Test
    void getPlayersByMinWeight_ShouldReturnHeavierPlayers() {
        System.out.println("\n=== Running getPlayersByMinWeight_ShouldReturnHeavierPlayers ===");
        
        List<Player> result = playerService.getPlayersByMinWeight(200);
        
        assertNotNull(result);
        assertEquals(1, result.size()); // только test-2 (210)
        assertEquals("test-2", result.get(0).getId());
    }

    @Test
    void searchPlayersByName_ShouldReturnMatchingPlayers() {
        System.out.println("\n=== Running searchPlayersByName_ShouldReturnMatchingPlayers ===");
        
        List<Player> result = playerService.searchPlayersByName("Player");
        
        assertNotNull(result);
        assertEquals(3, result.size()); // все три содержат "Player"
    }

    @Test
    void getHeightStats_ShouldReturnMinAndMax() {
        System.out.println("\n=== Running getHeightStats_ShouldReturnMinAndMax ===");
        
        var result = playerService.getHeightStats();
        
        assertNotNull(result);
        assertEquals(75, result.get("max"));
        assertEquals(72, result.get("min"));
    }

    @Test
    void getWeightStats_ShouldReturnMinAndMax() {
        System.out.println("\n=== Running getWeightStats_ShouldReturnMinAndMax ===");
        
        var result = playerService.getWeightStats();
        
        assertNotNull(result);
        assertEquals(210, result.get("max"));
        assertEquals(180, result.get("min"));
    }

    @Test
    void getYoungestPlayers_ShouldReturnYoungest() {
        System.out.println("\n=== Running getYoungestPlayers_ShouldReturnYoungest ===");
        
        List<Player> result = playerService.getYoungestPlayers();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-1", result.get(0).getId()); // 22.99 - самый молодой
    }

    @Test
    void getOldestPlayers_ShouldReturnOldest() {
        System.out.println("\n=== Running getOldestPlayers_ShouldReturnOldest ===");
        
        List<Player> result = playerService.getOldestPlayers();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-2", result.get(0).getId()); // 28.5 - самый возрастной
    }

    @Test
    void getTop10Tallest_ShouldReturnTallestFirst() {
        System.out.println("\n=== Running getTop10Tallest_ShouldReturnTallestFirst ===");
        
        List<Player> result = playerService.getTop10Tallest();
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("test-2", result.get(0).getId()); // 75 - самый высокий
        assertEquals("test-1", result.get(1).getId()); // 74
        assertEquals("test-3", result.get(2).getId()); // 72
    }

    @Test
    void getTop10Heaviest_ShouldReturnHeaviestFirst() {
        System.out.println("\n=== Running getTop10Heaviest_ShouldReturnHeaviestFirst ===");
        
        List<Player> result = playerService.getTop10Heaviest();
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("test-2", result.get(0).getId()); // 210 - самый тяжелый
        assertEquals("test-3", result.get(1).getId()); // 190
        assertEquals("test-1", result.get(2).getId()); // 180
    }

    @Test
    void getTeamStatistics_ShouldReturnTeamStats() {
        System.out.println("\n=== Running getTeamStatistics_ShouldReturnTeamStats ===");
        
        var result = playerService.getTeamStatistics("BAL");
        
        assertNotNull(result);
        assertEquals(1, result.get("totalPlayers"));
        assertEquals(22.99, (double) result.get("averageAge"), 0.01);
        assertEquals(74.0, (double) result.get("averageHeight"), 0.01);
        assertEquals(180.0, (double) result.get("averageWeight"), 0.01);
    }

    @Test
    void getPlayersCountByTeam_ShouldReturnCorrectCounts() {
        System.out.println("\n=== Running getPlayersCountByTeam_ShouldReturnCorrectCounts ===");
        
        var result = playerService.getPlayersCountByTeam();
        
        assertNotNull(result);
        assertEquals(1L, result.get("BAL"));
        assertEquals(1L, result.get("NYY"));
        assertEquals(1L, result.get("BOS"));
    }

    @Test
    void getOverallStatistics_ShouldReturnCompleteStats() {
        System.out.println("\n=== Running getOverallStatistics_ShouldReturnCompleteStats ===");
        
        var result = playerService.getOverallStatistics();
        
        assertNotNull(result);
        assertEquals(3, result.get("totalPlayers"));
        assertEquals(25.6, (double) result.get("averageAge"), 0.1);
        assertEquals(73.67, (double) result.get("averageHeightInches"), 0.1);
        assertEquals(193.33, (double) result.get("averageWeightLbs"), 0.1);
        
        @SuppressWarnings("unchecked")
        var heightStats = (java.util.Map<String, Integer>) result.get("heightStats");
        assertEquals(75, heightStats.get("max"));
        assertEquals(72, heightStats.get("min"));
        
        @SuppressWarnings("unchecked")
        var weightStats = (java.util.Map<String, Integer>) result.get("weightStats");
        assertEquals(210, weightStats.get("max"));
        assertEquals(180, weightStats.get("min"));
    }
}