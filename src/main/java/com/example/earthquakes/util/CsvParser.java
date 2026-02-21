package com.example.earthquakes.util;

import com.example.earthquakes.model.Earthquake;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvParser {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public List<Earthquake> parseCsv(String filename) {
        List<Earthquake> earthquakes = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource(filename).getInputStream()))) {
            
            List<String[]> records = reader.readAll();
            // Пропускаем заголовок
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                if (record.length >= 8) {
                    Earthquake eq = new Earthquake(
                        record[0],
                        LocalDateTime.parse(record[1], FORMATTER),
                        Double.parseDouble(record[2]),
                        Double.parseDouble(record[3]),
                        Double.parseDouble(record[4]),
                        Double.parseDouble(record[5]),
                        record[6],
                        record[7]
                    );
                    earthquakes.add(eq);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + filename, e);
        }
        
        return earthquakes;
    }
}