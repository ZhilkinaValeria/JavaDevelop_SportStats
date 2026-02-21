package com.example.sportstats.controller;

import com.example.sportstats.config.TestConfig;
import com.example.sportstats.config.TestJpaConfig;
import com.example.sportstats.config.TestSecurityConfig;
import com.example.sportstats.model.Player;
import com.example.sportstats.service.PlayerJpaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerController.class)
@ActiveProfiles({"test", "test-jpa"})
@Import({TestConfig.class, TestJpaConfig.class, TestSecurityConfig.class})
class PlayerControllerJpaTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean(name = "playerJpaService")
    private PlayerJpaService playerJpaService;
    
    // CSV сервис не мокаем - его нет в контексте для этого теста
    
    private Player newPlayer;
    private Player testPlayer1;
    private Player testPlayer2;
    private Player testPlayer3;
    
    @BeforeEach
    void setUp() {
        // Создаем тестового игрока для создания
        newPlayer = new Player();
        newPlayer.setId("test-3");
        newPlayer.setName("New Test Player");
        newPlayer.setTeam("BAL");
        newPlayer.setPosition("Outfielder");
        newPlayer.setHeightInches(72);
        newPlayer.setWeightLbs(185);
        newPlayer.setAge(24.5);
        
        // Создаем тестовых игроков для списка
        testPlayer1 = new Player();
        testPlayer1.setId("test-1");
        testPlayer1.setName("Test Player 1");
        testPlayer1.setTeam("BAL");
        testPlayer1.setPosition("Catcher");
        testPlayer1.setHeightInches(74);
        testPlayer1.setWeightLbs(180);
        testPlayer1.setAge(22.99);
        
        testPlayer2 = new Player();
        testPlayer2.setId("test-2");
        testPlayer2.setName("Test Player 2");
        testPlayer2.setTeam("NYY");
        testPlayer2.setPosition("Starting Pitcher");
        testPlayer2.setHeightInches(75);
        testPlayer2.setWeightLbs(210);
        testPlayer2.setAge(28.5);
        
        testPlayer3 = new Player();
        testPlayer3.setId("test-4");
        testPlayer3.setName("Test Player 3");
        testPlayer3.setTeam("BOS");
        testPlayer3.setPosition("Outfielder");
        testPlayer3.setHeightInches(72);
        testPlayer3.setWeightLbs(190);
        testPlayer3.setAge(25.3);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void create_WithAdminRole_ShouldCreatePlayer() throws Exception {
        when(playerJpaService.create(any(Player.class))).thenReturn(newPlayer);
        
        mockMvc.perform(post("/api/players")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPlayer)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("test-3"))
            .andExpect(jsonPath("$.name").value("New Test Player"))
            .andExpect(jsonPath("$.team").value("BAL"))
            .andExpect(jsonPath("$.position").value("Outfielder"))
            .andExpect(jsonPath("$.heightInches").value(72))
            .andExpect(jsonPath("$.weightLbs").value(185))
            .andExpect(jsonPath("$.age").value(24.5));
        
        verify(playerJpaService, times(1)).create(any(Player.class));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void create_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/players")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPlayer)))
            .andExpect(status().isForbidden());
        
        verify(playerJpaService, never()).create(any());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getById_WithUserRole_ShouldReturnPlayer() throws Exception {
        when(playerJpaService.getById("test-1")).thenReturn(testPlayer1);
        
        mockMvc.perform(get("/api/players/test-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("test-1"))
            .andExpect(jsonPath("$.name").value("Test Player 1"))
            .andExpect(jsonPath("$.team").value("BAL"))
            .andExpect(jsonPath("$.position").value("Catcher"))
            .andExpect(jsonPath("$.heightInches").value(74))
            .andExpect(jsonPath("$.weightLbs").value(180))
            .andExpect(jsonPath("$.age").value(22.99));
        
        verify(playerJpaService, times(1)).getById("test-1");
    }
    
    @Test
    void getAverageAge_WithoutAuth_ShouldReturnAverage() throws Exception {
        when(playerJpaService.getAverageAge()).thenReturn(25.5);
        
        mockMvc.perform(get("/api/players/stats/average-age"))
            .andExpect(status().isOk())
            .andExpect(content().string("25.5"));
        
        verify(playerJpaService, times(1)).getAverageAge();
    }
    
    @Test
    void getAverageHeight_WithoutAuth_ShouldReturnAverage() throws Exception {
        when(playerJpaService.getAverageHeight()).thenReturn(73.5);
        
        mockMvc.perform(get("/api/players/stats/average-height"))
            .andExpect(status().isOk())
            .andExpect(content().string("73.5"));
        
        verify(playerJpaService, times(1)).getAverageHeight();
    }
    
    @Test
    void getAverageWeight_WithoutAuth_ShouldReturnAverage() throws Exception {
        when(playerJpaService.getAverageWeight()).thenReturn(195.0);
        
        mockMvc.perform(get("/api/players/stats/average-weight"))
            .andExpect(status().isOk())
            .andExpect(content().string("195.0"));
        
        verify(playerJpaService, times(1)).getAverageWeight();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getAll_ShouldReturnList() throws Exception {
        List<Player> players = Arrays.asList(testPlayer1, testPlayer2, testPlayer3);
        
        when(playerJpaService.getAll()).thenReturn(players);
        
        mockMvc.perform(get("/api/players"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("test-1"))
            .andExpect(jsonPath("$[0].name").value("Test Player 1"))
            .andExpect(jsonPath("$[0].team").value("BAL"))
            .andExpect(jsonPath("$[1].id").value("test-2"))
            .andExpect(jsonPath("$[1].name").value("Test Player 2"))
            .andExpect(jsonPath("$[1].team").value("NYY"))
            .andExpect(jsonPath("$[2].id").value("test-4"))
            .andExpect(jsonPath("$[2].name").value("Test Player 3"))
            .andExpect(jsonPath("$[2].team").value("BOS"));
        
        verify(playerJpaService, times(1)).getAll();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getPlayersByTeam_ShouldReturnTeamPlayers() throws Exception {
        List<Player> balPlayers = Arrays.asList(testPlayer1);
        
        when(playerJpaService.getPlayersByTeam("BAL")).thenReturn(balPlayers);
        
        mockMvc.perform(get("/api/players/team/BAL"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("test-1"))
            .andExpect(jsonPath("$[0].team").value("BAL"));
        
        verify(playerJpaService, times(1)).getPlayersByTeam("BAL");
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getPlayersByPosition_ShouldReturnPositionPlayers() throws Exception {
        List<Player> outfielders = Arrays.asList(testPlayer3);
        
        when(playerJpaService.getPlayersByPosition("Outfielder")).thenReturn(outfielders);
        
        mockMvc.perform(get("/api/players/position/Outfielder"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("test-4"))
            .andExpect(jsonPath("$[0].position").value("Outfielder"));
        
        verify(playerJpaService, times(1)).getPlayersByPosition("Outfielder");
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getPlayersByAgeRange_ShouldReturnFilteredPlayers() throws Exception {
        List<Player> ageRangePlayers = Arrays.asList(testPlayer1, testPlayer3);
        
        when(playerJpaService.getPlayersByAgeRange(20.0, 26.0)).thenReturn(ageRangePlayers);
        
        mockMvc.perform(get("/api/players/age-range?minAge=20.0&maxAge=26.0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("test-1"))
            .andExpect(jsonPath("$[1].id").value("test-4"));
        
        verify(playerJpaService, times(1)).getPlayersByAgeRange(20.0, 26.0);
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getPlayersCountByTeam_ShouldReturnCounts() throws Exception {
        Map<String, Long> counts = new HashMap<>();
        counts.put("BAL", 1L);
        counts.put("NYY", 1L);
        counts.put("BOS", 1L);
        
        when(playerJpaService.getPlayersCountByTeam()).thenReturn(counts);
        
        mockMvc.perform(get("/api/players/stats/teams"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.BAL").value(1))
            .andExpect(jsonPath("$.NYY").value(1))
            .andExpect(jsonPath("$.BOS").value(1));
        
        verify(playerJpaService, times(1)).getPlayersCountByTeam();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getTeamStatistics_ShouldReturnTeamStats() throws Exception {
        Map<String, Object> teamStats = new HashMap<>();
        teamStats.put("team", "BAL");
        teamStats.put("totalPlayers", 1);
        teamStats.put("averageAge", 22.99);
        teamStats.put("averageHeight", 74.0);
        teamStats.put("averageWeight", 180.0);
        
        when(playerJpaService.getTeamStatistics("BAL")).thenReturn(teamStats);
        
        mockMvc.perform(get("/api/players/stats/team-composition/BAL"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.team").value("BAL"))
            .andExpect(jsonPath("$.totalPlayers").value(1))
            .andExpect(jsonPath("$.averageAge").value(22.99));
        
        verify(playerJpaService, times(1)).getTeamStatistics("BAL");
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getTop10Tallest_ShouldReturnList() throws Exception {
        List<Player> tallest = Arrays.asList(testPlayer2, testPlayer1, testPlayer3);
        
        when(playerJpaService.getTop10Tallest()).thenReturn(tallest);
        
        mockMvc.perform(get("/api/players/top10/tallest"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("test-2"))
            .andExpect(jsonPath("$[1].id").value("test-1"))
            .andExpect(jsonPath("$[2].id").value("test-4"));
        
        verify(playerJpaService, times(1)).getTop10Tallest();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_WithAdminRole_ShouldDelete() throws Exception {
        doNothing().when(playerJpaService).delete("test-1");
        
        mockMvc.perform(delete("/api/players/test-1")
                .with(csrf()))
            .andExpect(status().isNoContent());
        
        verify(playerJpaService, times(1)).delete("test-1");
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void delete_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/players/test-1")
                .with(csrf()))
            .andExpect(status().isForbidden());
        
        verify(playerJpaService, never()).delete(any());
    }
    
    private Player createTestPlayer(String id, String name, String team, String position, 
                                    Integer height, Integer weight, Double age) {
        Player p = new Player();
        p.setId(id);
        p.setName(name);
        p.setTeam(team);
        p.setPosition(position);
        p.setHeightInches(height);
        p.setWeightLbs(weight);
        p.setAge(age);
        return p;
    }
}