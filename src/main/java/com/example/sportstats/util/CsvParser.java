package com.example.sportstats.util;

import com.example.sportstats.model.Player;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile; 
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvParser {
    
    public List<Player> parseCsv(String filename) {
        List<Player> players = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource(filename).getInputStream()))) {
            
            List<String[]> records = reader.readAll();
            // Пропускаем заголовок
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                if (record.length >= 6) {
                    // Очищаем данные от кавычек
                    String name = cleanQuotes(record[0]);
                    String team = cleanQuotes(record[1]);
                    String position = cleanQuotes(record[2]);
                    Integer height = parseInt(cleanQuotes(record[3]));
                    Integer weight = parseInt(cleanQuotes(record[4]));
                    Double age = parseDouble(cleanQuotes(record[5]));
                    
                    Player player = new Player(name, team, position, height, weight, age);
                    players.add(player);
                }
            }
            System.out.println("Loaded " + players.size() + " players from CSV");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + filename, e);
        }
        
        return players;
    }
    
    private String cleanQuotes(String value) {
        if (value == null) return null;
        return value.replace("\"", "").trim();
    }
    
    private Integer parseInt(String value) {
        try {
            return value != null && !value.isEmpty() ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Double parseDouble(String value) {
        try {
            return value != null && !value.isEmpty() ? Double.parseDouble(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<Player> parseCsvMultipart(MultipartFile file) {
    List<Player> players = new ArrayList<>();
    
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = reader.readAll();
            
            if (records.isEmpty()) {
                return players;
            }
            
            // Пропускаем заголовок
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                if (record.length >= 6) {
                    String name = cleanQuotes(record[0]);
                    String team = cleanQuotes(record[1]);
                    String position = cleanQuotes(record[2]);
                    Integer height = parseInt(cleanQuotes(record[3]));
                    Integer weight = parseInt(cleanQuotes(record[4]));
                    Double age = parseDouble(cleanQuotes(record[5]));
                    
                    Player player = new Player(name, team, position, height, weight, age);
                    players.add(player);
                }
            }
            
            System.out.println("Прочитано " + players.size() + " игроков из загруженного файла");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse uploaded CSV file", e);
        }
        
        return players;
    }
}