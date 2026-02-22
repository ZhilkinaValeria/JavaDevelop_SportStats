package com.example.sportstats.service;

import com.example.sportstats.model.Player;
import com.example.sportstats.util.CsvParser;
import com.example.sportstats.util.CsvValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class CsvUploadService {
    
    private final JdbcTemplate jdbcTemplate;
    private final CsvParser csvParser;
    private final CsvValidator csvValidator;
    private final PlayerJpaService playerJpaService;
    private final String activeProfile;
    
    @Autowired
    public CsvUploadService(
            JdbcTemplate jdbcTemplate,
            CsvParser csvParser,
            CsvValidator csvValidator,
            @Autowired(required = false) PlayerJpaService playerJpaService,
            @Autowired(required = false) org.springframework.core.env.Environment environment) {
        this.jdbcTemplate = jdbcTemplate;
        this.csvParser = csvParser;
        this.csvValidator = csvValidator;
        this.playerJpaService = playerJpaService;
        
        // Определяем активный профиль
        if (environment != null && environment.getActiveProfiles().length > 0) {
            this.activeProfile = environment.getActiveProfiles()[0];
        } else {
            this.activeProfile = "unknown";
        }
    }
    
    /**
     * Загружает данные из CSV файла в базу данных
     * @param file CSV файл для загрузки
     * @return результат загрузки с статистикой
     */
    public Map<String, Object> uploadCsvFile(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("\n=== ЗАГРУЗКА CSV ФАЙЛА ===");
        System.out.println("Профиль: " + activeProfile);
        System.out.println("Имя файла: " + file.getOriginalFilename());
        System.out.println("Размер: " + file.getSize() + " байт");
        
        // Проверяем структуру CSV
        if (!csvValidator.validateCsvStructure(file)) {
            result.put("success", false);
            result.put("message", "CSV файл не соответствует шаблону");
            result.put("expectedHeaders", List.of("Name", "Team", "Position", "Height(inches)", "Weight(lbs)", "Age"));
            return result;
        }
        
        try {
            // Парсим CSV
            List<Player> players = csvParser.parseCsvMultipart(file);
            
            if (players.isEmpty()) {
                result.put("success", false);
                result.put("message", "CSV файл не содержит данных");
                return result;
            }
            
            System.out.println("📊 Загружаем " + players.size() + " игроков...");
            
            int successCount = 0;
            int duplicateCount = 0;
            int errorCount = 0;
            
            // Загружаем в зависимости от профиля
            if ("jpa".equals(activeProfile) && playerJpaService != null) {
                System.out.println(" Используем JPA режим");
                for (Player player : players) {
                    try {
                        // Проверяем, существует ли уже игрок
                        if (!jpaPlayerExists(player.getId())) {
                            playerJpaService.create(player);
                            successCount++;
                        } else {
                            duplicateCount++;
                        }
                    } catch (Exception e) {
                        errorCount++;
                        System.err.println("   Ошибка при загрузке игрока " + player.getId() + ": " + e.getMessage());
                    }
                }
            } else if ("jdbc".equals(activeProfile)) {
                System.out.println(" Используем JDBC режим");
                for (Player player : players) {
                    try {
                        // Проверяем, существует ли уже игрок
                        if (!jdbcPlayerExists(player.getId())) {
                            String sql = "INSERT INTO players (id, name, team, position, height_inches, weight_lbs, age) " +
                                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
                            int updated = jdbcTemplate.update(sql,
                                player.getId(),
                                player.getName(),
                                player.getTeam(),
                                player.getPosition(),
                                player.getHeightInches(),
                                player.getWeightLbs(),
                                player.getAge()
                            );
                            if (updated > 0) {
                                successCount++;
                            } else {
                                errorCount++;
                            }
                        } else {
                            duplicateCount++;
                        }
                    } catch (Exception e) {
                        errorCount++;
                        System.err.println("   Ошибка при загрузке игрока " + player.getId() + ": " + e.getMessage());
                    }
                }
            } else {
                result.put("success", false);
                result.put("message", "Неподдерживаемый профиль: " + activeProfile);
                return result;
            }
            
            System.out.println(" Успешно загружено: " + successCount);
            System.out.println("! Пропущено (дубликаты): " + duplicateCount);
            if (errorCount > 0) {
                System.out.println("х Ошибок: " + errorCount);
            }
            
            result.put("success", true);
            result.put("message", "Файл успешно загружен");
            result.put("profile", activeProfile);
            result.put("totalPlayers", players.size());
            result.put("imported", successCount);
            result.put("duplicates", duplicateCount);
            result.put("errors", errorCount);
            
            return result;
            
        } catch (Exception e) {
            System.err.println("х Ошибка при обработке файла: " + e.getMessage());
            e.printStackTrace();
            
            result.put("success", false);
            result.put("message", "Ошибка при обработке файла: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * Очищает таблицу players
     * @return количество удаленных записей
     */
    public int clearDatabase() {
        String sql = "DELETE FROM players";
        int deleted = jdbcTemplate.update(sql);
        System.out.println(" Удалено " + deleted + " записей из базы данных");
        return deleted;
    }
    
    private boolean jdbcPlayerExists(String id) {
        try {
            String sql = "SELECT COUNT(*) FROM players WHERE id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean jpaPlayerExists(String id) {
        try {
            return playerJpaService.getById(id) != null;
        } catch (Exception e) {
            return false;
        }
    }
}