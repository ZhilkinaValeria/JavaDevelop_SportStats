package com.example.sportstats.controller;

import com.example.sportstats.service.CsvUploadService;
import com.example.sportstats.util.CsvValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/csv")
public class CsvUploadController {
    
    private final CsvUploadService csvUploadService;
    private final CsvValidator csvValidator;
    private final Environment environment;
    
    @Autowired
    public CsvUploadController(
            CsvUploadService csvUploadService,
            CsvValidator csvValidator,
            Environment environment) {
        this.csvUploadService = csvUploadService;
        this.csvValidator = csvValidator;
        this.environment = environment;
    }
    
    /**
     * Загрузить CSV файл в базу данных
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadCsv(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = csvUploadService.uploadCsvFile(file);
        
        if (result.containsKey("success") && (Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * Проверить структуру CSV файла без загрузки
     */
    @PostMapping("/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> validateCsv(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        boolean isValid = csvValidator.validateCsvStructure(file);
        
        response.put("valid", isValid);
        response.put("message", isValid ? "CSV файл корректен" : "х CSV файл не соответствует шаблону");
        
        if (!isValid) {
            response.put("expectedHeaders", List.of("name", "team", "position", "height_inches", "weight_lbs", "age"));
        }
        
        String activeProfile = environment.getActiveProfiles().length > 0 ? 
                               environment.getActiveProfiles()[0] : "unknown";
        response.put("profile", activeProfile);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Очистить базу данных
     */
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> clearDatabase() {
        Map<String, Object> response = new HashMap<>();
        
        int deleted = csvUploadService.clearDatabase();
        
        response.put("success", true);
        response.put("message", "База данных очищена");
        response.put("deletedRecords", deleted);
        
        String activeProfile = environment.getActiveProfiles().length > 0 ? 
                               environment.getActiveProfiles()[0] : "unknown";
        response.put("profile", activeProfile);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получить информацию о текущем профиле и шаблоне CSV
     */
    @GetMapping("/info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> response = new HashMap<>();
        
        String activeProfile = environment.getActiveProfiles().length > 0 ? 
                               environment.getActiveProfiles()[0] : "unknown";
        
        response.put("activeProfile", activeProfile);
        response.put("dataSource", activeProfile.equals("jpa") ? "JPA (Hibernate)" : "JDBC (прямые SQL запросы)");
        response.put("csvTemplate", Map.of(
            "headers", "name,team,position,height_inches,weight_lbs,age",
            "example", "Adam Donachie,BAL,Catcher,74,180,22.99",
            "description", "CSV файл должен содержать заголовки и данные. Все поля обязательны."
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Скачать пример CSV файла
     */
    @GetMapping("/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> downloadTemplate() {
        String template = "name,team,position,height_inches,weight_lbs,age\n" +
                         "Adam Donachie,BAL,Catcher,74,180,22.99\n" +
                         "Paul Bako,BAL,Catcher,74,215,34.69\n" +
                         "Ramon Hernandez,BAL,Catcher,72,210,30.78\n";
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=players_template.csv")
                .header("Content-Type", "text/csv")
                .body(template);
    }
}