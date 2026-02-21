package com.example.sportstats.controller;

import com.example.sportstats.model.Player;
import com.example.sportstats.service.PlayerService;
import com.example.sportstats.service.PlayerJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    
    private final PlayerService playerService;
    private final PlayerJpaService playerJpaService;
    private final boolean isJpaMode;
    
    @Autowired
    public PlayerController(
            @Autowired(required = false) PlayerService playerService,
            @Autowired(required = false) PlayerJpaService playerJpaService) {
        this.playerService = playerService;
        this.playerJpaService = playerJpaService;
        this.isJpaMode = playerJpaService != null;
    }
    
    // Вспомогательные методы для каждого типа сервиса
    private boolean isJpaMode() {
        return isJpaMode;
    }
    
    // Базовые CRUD операции
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Player> getAllPlayers() {
        if (isJpaMode()) {
            return playerJpaService.getAll();
        }
        return playerService.getAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Player getPlayerById(@PathVariable String id) {
        if (isJpaMode()) {
            return playerJpaService.getById(id);
        }
        return playerService.getById(id);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        Player created;
        if (isJpaMode()) {
            created = playerJpaService.create(player);
        } else {
            created = playerService.create(player);
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Player updatePlayer(@PathVariable String id, @RequestBody Player player) {
        player.setId(id);
        if (isJpaMode()) {
            return playerJpaService.update(player);
        }
        return playerService.update(player);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlayer(@PathVariable String id) {
        if (isJpaMode()) {
            playerJpaService.delete(id);
        } else {
            playerService.delete(id);
        }
        return ResponseEntity.noContent().build();
    }
    
    // Фильтрация по команде
    @GetMapping("/team/{teamCode}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Player> getPlayersByTeam(@PathVariable String teamCode) {
        if (isJpaMode()) {
            return playerJpaService.getPlayersByTeam(teamCode);
        }
        return playerService.getPlayersByTeam(teamCode);
    }
    
    // Фильтрация по позиции
    @GetMapping("/position/{position}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Player> getPlayersByPosition(@PathVariable String position) {
        if (isJpaMode()) {
            return playerJpaService.getPlayersByPosition(position);
        }
        return playerService.getPlayersByPosition(position);
    }
    
    // Статистика - публичные эндпоинты
    @GetMapping("/stats/average-age")
    @PreAuthorize("permitAll()")
    public Double getAverageAge() {
        if (isJpaMode()) {
            return playerJpaService.getAverageAge();
        }
        return playerService.getAverageAge();
    }
    
    @GetMapping("/stats/average-height")
    @PreAuthorize("permitAll()")
    public Double getAverageHeight() {  
        if (isJpaMode()) {
            return playerJpaService.getAverageHeight(); 
        }
        return playerService.getAverageHeight(); 
    }
    
    @GetMapping("/stats/average-weight")
    @PreAuthorize("permitAll()")
    public Double getAverageWeight() {
        if (isJpaMode()) {
            return playerJpaService.getAverageWeight();
        }
        return playerService.getAverageWeight();
    }
    
    // Статистика по командам
    @GetMapping("/stats/teams")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Map<String, Long> getPlayersCountByTeam() {
        if (isJpaMode()) {
            return playerJpaService.getPlayersCountByTeam();
        }
        return playerService.getPlayersCountByTeam();
    }
    
    // Детальная статистика команды
    @GetMapping("/stats/team-composition/{teamCode}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Map<String, Object> getTeamStatistics(@PathVariable String teamCode) {
        if (isJpaMode()) {
            return playerJpaService.getTeamStatistics(teamCode);
        }
        return playerService.getTeamStatistics(teamCode);
    }
    
    // Топ-листы
    @GetMapping("/top10/tallest")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Player> getTop10Tallest() {
        if (isJpaMode()) {
            return playerJpaService.getTop10Tallest();
        }
        return playerService.getTop10Tallest();
    }
    
    @GetMapping("/top10/heaviest")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Player> getTop10Heaviest() {
        if (isJpaMode()) {
            return playerJpaService.getTop10Heaviest();
        }
        return playerService.getTop10Heaviest();
    }
    
    // Поиск по возрасту
    @GetMapping("/age-range")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Player> getPlayersByAgeRange(
            @RequestParam Double minAge, 
            @RequestParam Double maxAge) {
        if (isJpaMode()) {
            return playerJpaService.getPlayersByAgeRange(minAge, maxAge);
        }
        return playerService.getPlayersByAgeRange(minAge, maxAge);
    }
    
    // Поиск по имени
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Player> searchPlayersByName(@RequestParam String name) {
        if (isJpaMode()) {
            return playerJpaService.searchPlayersByName(name);
        }
        return playerService.searchPlayersByName(name);
    }
    
    // Полная статистика
    @GetMapping("/stats/overall")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Map<String, Object> getOverallStatistics() {
        if (isJpaMode()) {
            return playerJpaService.getOverallStatistics();
        }
        return playerService.getOverallStatistics();
    }
}