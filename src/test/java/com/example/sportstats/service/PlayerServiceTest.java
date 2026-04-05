package com.example.sportstats.service;

import com.example.sportstats.model.Player;
import com.example.sportstats.repository.CommonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private CommonRepository<Player> repository;

    @InjectMocks
    private PlayerService playerService;

    private Player testPlayer1;
    private Player testPlayer2;
    private Player testPlayer3;

    @BeforeEach
    void setUp() {
        testPlayer1 = new Player();
        testPlayer1.setName("Test Player 1");
        testPlayer1.setTeam("BAL");
        testPlayer1.setPosition("Catcher");
        testPlayer1.setHeightInches(74);
        testPlayer1.setWeightLbs(180);
        testPlayer1.setAge(22.99);
        testPlayer1.setId("test-1");

        testPlayer2 = new Player();
        testPlayer2.setName("Test Player 2");
        testPlayer2.setTeam("NYY");
        testPlayer2.setPosition("Starting Pitcher");
        testPlayer2.setHeightInches(75);
        testPlayer2.setWeightLbs(210);
        testPlayer2.setAge(28.5);
        testPlayer2.setId("test-2");

        testPlayer3 = new Player();
        testPlayer3.setName("Test Player 3");
        testPlayer3.setTeam("BOS");
        testPlayer3.setPosition("Outfielder");
        testPlayer3.setHeightInches(72);
        testPlayer3.setWeightLbs(190);
        testPlayer3.setAge(25.3);
        testPlayer3.setId("test-3");
    }

    @Test
    void getAll_ShouldReturnAllTestPlayers() {
        List<Player> mockPlayers = List.of(testPlayer1, testPlayer2, testPlayer3);
        when(repository.findAll()).thenReturn(mockPlayers);

        List<Player> result = playerService.getAll();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getById_WhenExists_ShouldReturnPlayer() {
        when(repository.findById("test-1")).thenReturn(testPlayer1);

        Player result = playerService.getById("test-1");

        assertNotNull(result);
        assertEquals("test-1", result.getId());
        assertEquals("Test Player 1", result.getName());
        verify(repository, times(1)).findById("test-1");
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        when(repository.findById("non-existent")).thenReturn(null);

        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> playerService.getById("non-existent"));
    }

    @Test
    void getAverageAge_ShouldCalculateAverage() {
        when(repository.findAll()).thenReturn(List.of(testPlayer1, testPlayer2, testPlayer3));

        Double result = playerService.getAverageAge();

        assertEquals(25.6, result, 0.1);
    }

    @Test
    void create_ShouldAddNewPlayer() {
        Player newPlayer = new Player();
        // Не устанавливай ID вручную - он сгенерируется автоматически
        newPlayer.setName("New Test Player");
        newPlayer.setTeam("CHC");
        newPlayer.setPosition("First Baseman");
        newPlayer.setHeightInches(76);
        newPlayer.setWeightLbs(220);
        newPlayer.setAge(27.5);
        String expectedId = "CHC_New_Test_Player";

        when(repository.exists(expectedId)).thenReturn(false);
        when(repository.save(any(Player.class))).thenAnswer(invocation -> {
            Player saved = invocation.getArgument(0);
            saved.setId(expectedId);
            return saved;
        });

        Player created = playerService.create(newPlayer);

        assertNotNull(created);
        assertEquals(expectedId, created.getId());
        assertEquals("New Test Player", created.getName());
        assertEquals("CHC", created.getTeam());
        verify(repository, times(1)).exists(expectedId);
        verify(repository, times(1)).save(any(Player.class));
    }

    @Test
    void delete_ShouldRemovePlayer() {
        when(repository.exists("test-1")).thenReturn(true);
        doNothing().when(repository).delete("test-1");

        playerService.delete("test-1");

        verify(repository, times(1)).delete("test-1");
    }

    @Test
    void getPlayersByTeam_ShouldReturnTeamPlayers() {
        when(repository.findAll()).thenReturn(List.of(testPlayer1, testPlayer2, testPlayer3));

        List<Player> result = playerService.getPlayersByTeam("BAL");

        assertEquals(1, result.size());
        assertEquals("test-1", result.get(0).getId());
    }

    @Test
    void getPlayersByPosition_ShouldReturnPositionPlayers() {
        when(repository.findAll()).thenReturn(List.of(testPlayer1, testPlayer2, testPlayer3));

        List<Player> result = playerService.getPlayersByPosition("Catcher");

        assertEquals(1, result.size());
        assertEquals("test-1", result.get(0).getId());
    }

    @Test
    void getYoungestPlayers_ShouldReturnYoungest() {
        when(repository.findAll()).thenReturn(List.of(testPlayer1, testPlayer2, testPlayer3));

        List<Player> result = playerService.getYoungestPlayers();

        assertEquals(1, result.size());
        assertEquals("test-1", result.get(0).getId());
    }

    @Test
    void getOldestPlayers_ShouldReturnOldest() {
        when(repository.findAll()).thenReturn(List.of(testPlayer1, testPlayer2, testPlayer3));

        List<Player> result = playerService.getOldestPlayers();

        assertEquals(1, result.size());
        assertEquals("test-2", result.get(0).getId());
    }

    @Test
    void getTop10Tallest_ShouldReturnTallestFirst() {
        when(repository.findAll()).thenReturn(List.of(testPlayer1, testPlayer2, testPlayer3));

        List<Player> result = playerService.getTop10Tallest();

        assertEquals(3, result.size());
        assertEquals("test-2", result.get(0).getId());
        assertEquals("test-1", result.get(1).getId());
        assertEquals("test-3", result.get(2).getId());
    }

    @Test
    void getTeamStatistics_ShouldReturnTeamStats() {
        when(repository.findAll()).thenReturn(List.of(testPlayer1, testPlayer2, testPlayer3));

        var result = playerService.getTeamStatistics("BAL");

        assertEquals(1, result.get("totalPlayers"));
        assertEquals(22.99, (double) result.get("averageAge"), 0.01);
    }

    @Test
    void getPlayersCountByTeam_ShouldReturnCorrectCounts() {
        when(repository.findAll()).thenReturn(List.of(testPlayer1, testPlayer2, testPlayer3));

        var result = playerService.getPlayersCountByTeam();

        assertEquals(1L, result.get("BAL"));
        assertEquals(1L, result.get("NYY"));
        assertEquals(1L, result.get("BOS"));
    }
}