package com.example.earthquakes.controller;

import com.example.earthquakes.config.TestConfig;
import com.example.earthquakes.config.TestSecurityConfig;
import com.example.earthquakes.model.Earthquake;
import com.example.earthquakes.service.EarthquakesService;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EarthquakeController.class)
@ActiveProfiles("test")
@Import({TestConfig.class, TestSecurityConfig.class})
class EarthquakeControllerCsvTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean(name = "earthquakesService")
    private EarthquakesService earthquakesService;
    
    // JPA сервис не мокаем - его нет в контексте
    
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
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("3"))
            .andExpect(jsonPath("$.magnitude").value(4.5));
        
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
        Earthquake earthquake = new Earthquake();
        earthquake.setId("1");
        earthquake.setMagnitude(5.5);
        earthquake.setPlace("Test Location");
        
        when(earthquakesService.getById("1")).thenReturn(earthquake);
        
        mockMvc.perform(get("/api/earthquakes/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.magnitude").value(5.5));
        
        verify(earthquakesService, times(1)).getById("1");
    }
    
    @Test
    void getAvgMagnitude_WithoutAuth_ShouldReturnAverage() throws Exception {
        when(earthquakesService.avgMagnitude()).thenReturn(5.5);
        
        mockMvc.perform(get("/api/earthquakes/stats/avg-magnitude"))
            .andExpect(status().isOk())
            .andExpect(content().string("5.5"));
        
        verify(earthquakesService, times(1)).avgMagnitude();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getAll_ShouldReturnList() throws Exception {
        List<Earthquake> earthquakes = Arrays.asList(
            createEarthquake("1", 5.5),
            createEarthquake("2", 6.5)
        );
        
        when(earthquakesService.getAll()).thenReturn(earthquakes);
        
        mockMvc.perform(get("/api/earthquakes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].magnitude").value(5.5))
            .andExpect(jsonPath("$[1].id").value("2"))
            .andExpect(jsonPath("$[1].magnitude").value(6.5));
        
        verify(earthquakesService, times(1)).getAll();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_WithAdminRole_ShouldDelete() throws Exception {
        doNothing().when(earthquakesService).delete("1");
        
        mockMvc.perform(delete("/api/earthquakes/1")
                .with(csrf()))
            .andExpect(status().isNoContent());
        
        verify(earthquakesService, times(1)).delete("1");
    }
    
    private Earthquake createEarthquake(String id, double magnitude) {
        Earthquake e = new Earthquake();
        e.setId(id);
        e.setMagnitude(magnitude);
        e.setPlace("Test Location " + id);
        return e;
    }
}