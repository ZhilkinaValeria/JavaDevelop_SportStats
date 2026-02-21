package com.example.sportstats.service;

import com.example.sportstats.model.Player;
import com.example.sportstats.repository.PlayerJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerJpaServiceTest {
    
    @Mock
    private PlayerJpaRepository repository;
    
    @InjectMocks
    private PlayerJpaService service;
    
    private Player player1;
    private Player player2;
    private Player player3;
    
    @BeforeEach
    void setUp() {
        // Игрок 1 - BAL, Catcher
        player1 = new Player();
        player1.setId("1");
        player1.setName("Test Player 1");
        player1.setTeam("BAL");
        player1.setPosition("Catcher");
        player1.setHeightInches(74);
        player1.setWeightLbs(180);
        player1.setAge(22.99);
        
        // Игрок 2 - NYY, Pitcher
        player2 = new Player();
        player2.setId("2");
        player2.setName("Test Player 2");
        player2.setTeam("NYY");
        player2.setPosition("Starting Pitcher");
        player2.setHeightInches(75);
        player2.setWeightLbs(210);
        player2.setAge(28.5);
        
        // Игрок 3 - BOS, Outfielder
        player3 = new Player();
        player3.setId("3");
        player3.setName("Test Player 3");
        player3.setTeam("BOS");
        player3.setPosition("Outfielder");
        player3.setHeightInches(72);
        player3.setWeightLbs(190);
        player3.setAge(25.3);
    }
    
    @Test
    void getAll_ShouldReturnAllPlayers() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(player1, player2, player3));
        
        // Act
        List<Player> result = service.getAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("2", result.get(1).getId());
        assertEquals("3", result.get(2).getId());
        verify(repository, times(1)).findAll();
    }
    
    @Test
    void getById_WhenExists_ShouldReturnPlayer() {
        // Arrange
        when(repository.findById("1")).thenReturn(Optional.of(player1));
        
        // Act
        Player result = service.getById("1");
        
        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Test Player 1", result.getName());
        assertEquals("BAL", result.getTeam());
        assertEquals("Catcher", result.getPosition());
        assertEquals(74, result.getHeightInches());
        assertEquals(180, result.getWeightLbs());
        assertEquals(22.99, result.getAge());
        verify(repository, times(1)).findById("1");
    }
    
    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(repository.findById("999")).thenReturn(Optional.empty());
        
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.getById("999")
        );
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Player not found", exception.getReason());
        verify(repository, times(1)).findById("999");
    }
    
    @Test
    void create_WhenNewId_ShouldSaveAndReturn() {
        // Arrange
        Player newPlayer = new Player();
        newPlayer.setId("4");
        newPlayer.setName("New Player");
        newPlayer.setTeam("CHC");
        newPlayer.setPosition("First Baseman");
        newPlayer.setHeightInches(76);
        newPlayer.setWeightLbs(220);
        newPlayer.setAge(27.5);
        
        when(repository.existsById("4")).thenReturn(false);
        when(repository.save(any(Player.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        Player result = service.create(newPlayer);
        
        // Assert
        assertNotNull(result);
        assertEquals("4", result.getId());
        assertEquals("New Player", result.getName());
        assertEquals("CHC", result.getTeam());
        assertEquals("First Baseman", result.getPosition());
        assertEquals(76, result.getHeightInches());
        assertEquals(220, result.getWeightLbs());
        assertEquals(27.5, result.getAge());
        verify(repository, times(1)).existsById("4");
        verify(repository, times(1)).save(newPlayer);
    }
    
    @Test
    void create_WhenExistingId_ShouldThrowException() {
        // Arrange
        when(repository.existsById("1")).thenReturn(true);
        
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.create(player1)
        );
        
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Player already exists", exception.getReason());
        verify(repository, never()).save(any());
    }
    
    @Test
    void update_WhenExists_ShouldUpdateAndReturn() {
        // Arrange
        Player updatedPlayer = new Player();
        updatedPlayer.setId("1");
        updatedPlayer.setName("Updated Player");
        updatedPlayer.setTeam("BAL");
        updatedPlayer.setPosition("Catcher");
        updatedPlayer.setHeightInches(75);
        updatedPlayer.setWeightLbs(185);
        updatedPlayer.setAge(23.5);
        
        when(repository.existsById("1")).thenReturn(true);
        when(repository.save(any(Player.class))).thenReturn(updatedPlayer);
        
        // Act
        Player result = service.update(updatedPlayer);
        
        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Updated Player", result.getName());
        assertEquals(75, result.getHeightInches());
        assertEquals(185, result.getWeightLbs());
        assertEquals(23.5, result.getAge());
        verify(repository, times(1)).existsById("1");
        verify(repository, times(1)).save(updatedPlayer);
    }
    
    @Test
    void update_WhenNotExists_ShouldThrowException() {
        // Arrange
        Player updatedPlayer = new Player();
        updatedPlayer.setId("999");
        updatedPlayer.setName("Updated Player");
        
        when(repository.existsById("999")).thenReturn(false);
        
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.update(updatedPlayer)
        );
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Player not found", exception.getReason());
        verify(repository, never()).save(any());
    }
    
    @Test
    void delete_WhenExists_ShouldDelete() {
        // Arrange
        when(repository.existsById("1")).thenReturn(true);
        doNothing().when(repository).deleteById("1");
        
        // Act
        service.delete("1");
        
        // Assert
        verify(repository, times(1)).existsById("1");
        verify(repository, times(1)).deleteById("1");
    }
    
    @Test
    void delete_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(repository.existsById("999")).thenReturn(false);
        
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.delete("999")
        );
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Player not found", exception.getReason());
        verify(repository, never()).deleteById(any());
    }
    
    @Test
    void getPlayersByTeam_ShouldReturnTeamPlayers() {
        // Arrange
        when(repository.findByTeam("BAL")).thenReturn(List.of(player1));
        
        // Act
        List<Player> result = service.getPlayersByTeam("BAL");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("BAL", result.get(0).getTeam());
        verify(repository, times(1)).findByTeam("BAL");
    }
    
    @Test
    void getPlayersByPosition_ShouldReturnPositionPlayers() {
        // Arrange
        when(repository.findByPosition("Catcher")).thenReturn(List.of(player1));
        
        // Act
        List<Player> result = service.getPlayersByPosition("Catcher");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("Catcher", result.get(0).getPosition());
        verify(repository, times(1)).findByPosition("Catcher");
    }
    
    @Test
    void getPlayersByAgeRange_ShouldReturnFilteredPlayers() {
        // Arrange
        when(repository.findByAgeBetween(20.0, 26.0)).thenReturn(List.of(player1, player3));
        
        // Act
        List<Player> result = service.getPlayersByAgeRange(20.0, 26.0);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("3", result.get(1).getId());
        verify(repository, times(1)).findByAgeBetween(20.0, 26.0);
    }
    
    @Test
    void getPlayersByMinHeight_ShouldReturnTallPlayers() {
        // Arrange
        when(repository.findByHeightInchesGreaterThan(73)).thenReturn(List.of(player1, player2));
        
        // Act
        List<Player> result = service.getPlayersByMinHeight(73);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("2", result.get(1).getId());
        verify(repository, times(1)).findByHeightInchesGreaterThan(73);
    }
    
    @Test
    void getPlayersByMinWeight_ShouldReturnHeavierPlayers() {
        // Arrange
        when(repository.findByWeightLbsGreaterThan(185)).thenReturn(List.of(player2));
        
        // Act
        List<Player> result = service.getPlayersByMinWeight(185);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getId());
        assertEquals(210, result.get(0).getWeightLbs());
        verify(repository, times(1)).findByWeightLbsGreaterThan(185);
    }
    
    @Test
    void searchPlayersByName_ShouldReturnMatchingPlayers() {
        // Arrange
        when(repository.findByNameContaining("Test")).thenReturn(List.of(player1, player2, player3));
        
        // Act
        List<Player> result = service.searchPlayersByName("Test");
        
        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(repository, times(1)).findByNameContaining("Test");
    }
    
    @Test
    void getAverageAge_ShouldCalculateAverage() {
        // Arrange
        when(repository.findAverageAge()).thenReturn(25.6);
        
        // Act
        Double result = service.getAverageAge();
        
        // Assert
        assertEquals(25.6, result);
        verify(repository, times(1)).findAverageAge();
    }
    
    @Test
    void getAverageAge_WhenNoData_ShouldReturnZero() {
        // Arrange
        when(repository.findAverageAge()).thenReturn(null);
        
        // Act
        Double result = service.getAverageAge();
        
        // Assert
        assertEquals(0.0, result);
    }
    
    @Test
    void getAverageHeight_ShouldCalculateAverage() {
        // Arrange
        when(repository.findAverageHeightInches()).thenReturn(73.5);
        
        // Act
        Double result = service.getAverageHeight();
        
        // Assert
        assertEquals(73.5, result);
        verify(repository, times(1)).findAverageHeightInches();
    }
    
    @Test
    void getAverageWeight_ShouldCalculateAverage() {
        // Arrange
        when(repository.findAverageWeightLbs()).thenReturn(193.3);
        
        // Act
        Double result = service.getAverageWeight();
        
        // Assert
        assertEquals(193.3, result);
        verify(repository, times(1)).findAverageWeightLbs();
    }
    
    @Test
    void getPlayersCountByTeam_ShouldReturnCounts() {
        // Arrange
        List<Object[]> mockResults = Arrays.asList(
            new Object[]{"BAL", 1L},
            new Object[]{"NYY", 1L},
            new Object[]{"BOS", 1L}
        );
        when(repository.countPlayersByTeam()).thenReturn(mockResults);
        
        // Act
        Map<String, Long> result = service.getPlayersCountByTeam();
        
        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1L, result.get("BAL"));
        assertEquals(1L, result.get("NYY"));
        assertEquals(1L, result.get("BOS"));
        verify(repository, times(1)).countPlayersByTeam();
    }
    
    @Test
    void getHeightStats_ShouldReturnMinAndMax() {
        // Arrange
        when(repository.findMaxHeight()).thenReturn(75);
        when(repository.findMinHeight()).thenReturn(72);
        
        // Act
        Map<String, Integer> result = service.getHeightStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(75, result.get("max"));
        assertEquals(72, result.get("min"));
        verify(repository, times(1)).findMaxHeight();
        verify(repository, times(1)).findMinHeight();
    }
    
    @Test
    void getWeightStats_ShouldReturnMinAndMax() {
        // Arrange
        when(repository.findMaxWeight()).thenReturn(210);
        when(repository.findMinWeight()).thenReturn(180);
        
        // Act
        Map<String, Integer> result = service.getWeightStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(210, result.get("max"));
        assertEquals(180, result.get("min"));
        verify(repository, times(1)).findMaxWeight();
        verify(repository, times(1)).findMinWeight();
    }
    
    @Test
    void getYoungestPlayers_ShouldReturnYoungest() {
        // Arrange
        when(repository.findYoungestPlayers()).thenReturn(List.of(player1));
        
        // Act
        List<Player> result = service.getYoungestPlayers();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals(22.99, result.get(0).getAge());
        verify(repository, times(1)).findYoungestPlayers();
    }
    
    @Test
    void getOldestPlayers_ShouldReturnOldest() {
        // Arrange
        when(repository.findOldestPlayers()).thenReturn(List.of(player2));
        
        // Act
        List<Player> result = service.getOldestPlayers();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getId());
        assertEquals(28.5, result.get(0).getAge());
        verify(repository, times(1)).findOldestPlayers();
    }
    
    @Test
    void getTeamStatistics_ShouldReturnTeamStats() {
        // Arrange
        Object[] stats = new Object[]{22.99, 74.0, 180.0, 1L};
        when(repository.getTeamStats("BAL")).thenReturn(stats);
        
        // Act
        Map<String, Object> result = service.getTeamStatistics("BAL");
        
        // Assert
        assertNotNull(result);
        assertEquals(22.99, result.get("averageAge"));
        assertEquals(74.0, result.get("averageHeightInches"));
        assertEquals(180.0, result.get("averageWeightLbs"));
        assertEquals(1L, result.get("totalPlayers"));
        verify(repository, times(1)).getTeamStats("BAL");
    }
    
    @Test
    void getTop10Tallest_ShouldReturnTallestPlayers() {
        // Arrange
        when(repository.findTop10ByOrderByHeightInchesDesc()).thenReturn(List.of(player2, player1, player3));
        
        // Act
        List<Player> result = service.getTop10Tallest();
        
        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("2", result.get(0).getId()); // Самый высокий
        assertEquals("1", result.get(1).getId());
        assertEquals("3", result.get(2).getId());
        verify(repository, times(1)).findTop10ByOrderByHeightInchesDesc();
    }
    
    @Test
    void getTop10Heaviest_ShouldReturnHeaviestPlayers() {
        // Arrange
        when(repository.findTop10ByOrderByWeightLbsDesc()).thenReturn(List.of(player2, player3, player1));
        
        // Act
        List<Player> result = service.getTop10Heaviest();
        
        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("2", result.get(0).getId()); // Самый тяжелый
        assertEquals("3", result.get(1).getId());
        assertEquals("1", result.get(2).getId());
        verify(repository, times(1)).findTop10ByOrderByWeightLbsDesc();
    }
    
    @Test
    void getOverallStatistics_ShouldReturnCompleteStats() {
        // Arrange
        when(repository.count()).thenReturn(3L);
        when(repository.findAverageAge()).thenReturn(25.6);
        when(repository.findAverageHeightInches()).thenReturn(73.7);
        when(repository.findAverageWeightLbs()).thenReturn(193.3);
        when(repository.findMaxHeight()).thenReturn(75);
        when(repository.findMinHeight()).thenReturn(72);
        when(repository.findMaxWeight()).thenReturn(210);
        when(repository.findMinWeight()).thenReturn(180);
        
        List<Object[]> teamCounts = Arrays.asList(
            new Object[]{"BAL", 1L},
            new Object[]{"NYY", 1L},
            new Object[]{"BOS", 1L}
        );
        when(repository.countPlayersByTeam()).thenReturn(teamCounts);
        
        List<Object[]> positionCounts = Arrays.asList(
            new Object[]{"Catcher", 1L},
            new Object[]{"Starting Pitcher", 1L},
            new Object[]{"Outfielder", 1L}
        );
        when(repository.countPlayersByPosition()).thenReturn(positionCounts);
        
        // Act
        Map<String, Object> result = service.getOverallStatistics();
        
        // Assert
        assertNotNull(result);
        assertEquals(3L, result.get("totalPlayers"));
        assertEquals(25.6, result.get("averageAge"));
        assertEquals(73.7, result.get("averageHeightInches"));
        assertEquals(193.3, result.get("averageWeightLbs"));
        
        Map<String, Integer> heightStats = (Map<String, Integer>) result.get("heightStats");
        assertEquals(75, heightStats.get("max"));
        assertEquals(72, heightStats.get("min"));
        
        Map<String, Integer> weightStats = (Map<String, Integer>) result.get("weightStats");
        assertEquals(210, weightStats.get("max"));
        assertEquals(180, weightStats.get("min"));
        
        verify(repository, times(1)).count();
        verify(repository, times(1)).findAverageAge();
        verify(repository, times(1)).findAverageHeightInches();
        verify(repository, times(1)).findAverageWeightLbs();
    }
}