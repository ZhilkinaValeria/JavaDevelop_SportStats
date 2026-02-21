package com.example.sportstats.controller;

import com.example.sportstats.config.TestConfig;
import com.example.sportstats.config.TestSecurityConfig;
import com.example.sportstats.model.Player;
import com.example.sportstats.service.PlayerService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerController.class)
@ActiveProfiles("test")
@Import({TestConfig.class, TestSecurityConfig.class})
class PlayerControllerCsvTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean(name = "playerService")
    private PlayerService playerService;
    
    // JPA сервис не мокаем - его нет в контексте
    
    private Player newPlayer;
    private Player testPlayer1;
    private Player testPlayer2;
    
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
        testPlayer2.setPosition("Pitcher");
        testPlayer2.setHeightInches(75);
        testPlayer2.setWeightLbs(210);
        testPlayer2.setAge(28.5);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void create_WithAdminRole_ShouldCreatePlayer() throws Exception {
        when(playerService.create(any(Player.class))).thenReturn(newPlayer);
        
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
        
        verify(playerService, times(1)).create(any(Player.class));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void create_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/players")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPlayer)))
            .andExpect(status().isForbidden());
        
        verify(playerService, never()).create(any());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getById_WithUserRole_ShouldReturnPlayer() throws Exception {
        when(playerService.getById("test-1")).thenReturn(testPlayer1);
        
        mockMvc.perform(get("/api/players/test-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("test-1"))
            .andExpect(jsonPath("$.name").value("Test Player 1"))
            .andExpect(jsonPath("$.team").value("BAL"))
            .andExpect(jsonPath("$.position").value("Catcher"))
            .andExpect(jsonPath("$.heightInches").value(74))
            .andExpect(jsonPath("$.weightLbs").value(180))
            .andExpect(jsonPath("$.age").value(22.99));
        
        verify(playerService, times(1)).getById("test-1");
    }
    
    @Test
    void getAverageAge_WithoutAuth_ShouldReturnAverage() throws Exception {
        when(playerService.getAverageAge()).thenReturn(25.5);
        
        mockMvc.perform(get("/api/players/stats/average-age"))
            .andExpect(status().isOk())
            .andExpect(content().string("25.5"));
        
        verify(playerService, times(1)).getAverageAge();
    }
    
    @Test
    void getAverageHeight_WithoutAuth_ShouldReturnAverage() throws Exception {
        when(playerService.getAverageHeight()).thenReturn(73.5);
        
        mockMvc.perform(get("/api/players/stats/average-height"))
            .andExpect(status().isOk())
            .andExpect(content().string("73.5"));
        
        verify(playerService, times(1)).getAverageHeight();
    }
    
    @Test
    void getAverageWeight_WithoutAuth_ShouldReturnAverage() throws Exception {
        when(playerService.getAverageWeight()).thenReturn(195.0);
        
        mockMvc.perform(get("/api/players/stats/average-weight"))
            .andExpect(status().isOk())
            .andExpect(content().string("195.0"));
        
        verify(playerService, times(1)).getAverageWeight();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getAll_ShouldReturnList() throws Exception {
        List<Player> players = Arrays.asList(testPlayer1, testPlayer2);
        
        when(playerService.getAll()).thenReturn(players);
        
        mockMvc.perform(get("/api/players"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("test-1"))
            .andExpect(jsonPath("$[0].name").value("Test Player 1"))
            .andExpect(jsonPath("$[0].team").value("BAL"))
            .andExpect(jsonPath("$[1].id").value("test-2"))
            .andExpect(jsonPath("$[1].name").value("Test Player 2"))
            .andExpect(jsonPath("$[1].team").value("NYY"));
        
        verify(playerService, times(1)).getAll();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getPlayersByTeam_ShouldReturnTeamPlayers() throws Exception {
        List<Player> balPlayers = Arrays.asList(testPlayer1);
        
        when(playerService.getPlayersByTeam("BAL")).thenReturn(balPlayers);
        
        mockMvc.perform(get("/api/players/team/BAL"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("test-1"))
            .andExpect(jsonPath("$[0].team").value("BAL"));
        
        verify(playerService, times(1)).getPlayersByTeam("BAL");
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getPlayersByPosition_ShouldReturnPositionPlayers() throws Exception {
        List<Player> catchers = Arrays.asList(testPlayer1);
        
        when(playerService.getPlayersByPosition("Catcher")).thenReturn(catchers);
        
        mockMvc.perform(get("/api/players/position/Catcher"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("test-1"))
            .andExpect(jsonPath("$[0].position").value("Catcher"));
        
        verify(playerService, times(1)).getPlayersByPosition("Catcher");
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_WithAdminRole_ShouldDelete() throws Exception {
        doNothing().when(playerService).delete("test-1");
        
        mockMvc.perform(delete("/api/players/test-1")
                .with(csrf()))
            .andExpect(status().isNoContent());
        
        verify(playerService, times(1)).delete("test-1");
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void delete_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/players/test-1")
                .with(csrf()))
            .andExpect(status().isForbidden());
        
        verify(playerService, never()).delete(any());
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