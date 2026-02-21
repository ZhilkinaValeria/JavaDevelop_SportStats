package com.example.earthquakes.controller;

import com.example.earthquakes.config.TestConfig;
import com.example.earthquakes.config.TestSecurityConfig;
import com.example.earthquakes.model.Earthquake;
import com.example.earthquakes.service.EarthquakesService;
import com.example.earthquakes.service.EarthquakesJpaService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EarthquakeController.class)
@ActiveProfiles("test")
@Import({TestConfig.class, TestSecurityConfig.class})
class EarthquakeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean(name = "earthquakesService")
    private EarthquakesService earthquakesService;
    
    @MockBean(name = "earthquakesJpaService")
    private EarthquakesJpaService earthquakesJpaService;
    
    private Earthquake newEarthquake;
    
    @BeforeEach
    void setUp() {
        newEarthquake = new Earthquake();
        newEarthquake.setId("3");
        newEarthquake.setMagnitude(4.5);
        newEarthquake.setPlace("Test Location");
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void create_WithAdminRole_ShouldCreateEarthquake() throws Exception {
        when(earthquakesService.create(any(Earthquake.class))).thenReturn(newEarthquake);
        
        mockMvc.perform(post("/api/earthquakes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEarthquake)))
            .andExpect(status().isCreated());
        
        verify(earthquakesService, times(1)).create(any(Earthquake.class));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void create_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/earthquakes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEarthquake)))
            .andExpect(status().isForbidden());
        
        verify(earthquakesService, never()).create(any());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getById_WithUserRole_ShouldReturnEarthquake() throws Exception {
        when(earthquakesService.getById("1")).thenReturn(newEarthquake);
        
        mockMvc.perform(get("/api/earthquakes/1"))
            .andExpect(status().isOk());
    }
    
    @Test
    void getAvgMagnitude_WithoutAuth_ShouldReturnAverage() throws Exception {
        when(earthquakesService.avgMagnitude()).thenReturn(5.5);
        
        mockMvc.perform(get("/api/earthquakes/stats/avg-magnitude"))
            .andExpect(status().isOk())
            .andExpect(content().string("5.5"));
        
        verify(earthquakesService, times(1)).avgMagnitude();
    }
}