package com.example.sportstats.util;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class CsvValidator {
    
    // Ожидаемые заголовки для файла players.csv
    private static final Set<String> EXPECTED_HEADERS = new HashSet<>(Arrays.asList(
        "name", "team", "position", "height(inches)", "weight(lbs)", "age" 
    ));
    
    /**
     * Проверяет структуру CSV файла
     * @param file CSV файл для проверки
     * @return true если структура правильная, false если нет
     */
    public boolean validateCsvStructure(MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] headers = reader.readNext();
            
            if (headers == null || headers.length == 0) {
                System.err.println("х CSV файл пуст или не содержит заголовков");
                return false;
            }
            
            // Очищаем заголовки от кавычек и пробелов
            Set<String> actualHeaders = new HashSet<>();
            for (String header : headers) {
                actualHeaders.add(cleanHeader(header));
            }
            
            // Проверяем, что все ожидаемые заголовки присутствуют
            boolean allHeadersPresent = EXPECTED_HEADERS.equals(actualHeaders);
            
            if (!allHeadersPresent) {
                System.err.println("х CSV файл имеет неправильную структуру!");
                System.err.println("   Ожидаемые заголовки: " + EXPECTED_HEADERS);
                System.err.println("   Фактические заголовки: " + actualHeaders);
            }
            
            return allHeadersPresent;
            
        } catch (Exception e) {
            System.err.println("х Ошибка при чтении CSV файла: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Проверяет структуру CSV файла по имени файла из ресурсов
     */
    public boolean validateCsvStructure(String filename) {
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filename)))) {
            
            if (reader.peek() == null) {
                System.err.println("х CSV файл " + filename + " не найден или пуст");
                return false;
            }
            
            String[] headers = reader.readNext();
            
            if (headers == null || headers.length == 0) {
                System.err.println("х CSV файл " + filename + " не содержит заголовков");
                return false;
            }
            
            Set<String> actualHeaders = new HashSet<>();
            for (String header : headers) {
                actualHeaders.add(cleanHeader(header));
            }
            
            boolean allHeadersPresent = EXPECTED_HEADERS.equals(actualHeaders);
            
            if (!allHeadersPresent) {
                System.err.println("х CSV файл " + filename + " имеет неправильную структуру!");
                System.err.println("   Ожидаемые заголовки: " + EXPECTED_HEADERS);
                System.err.println("   Фактические заголовки: " + actualHeaders);
            } else {
                System.out.println(" CSV файл " + filename + " прошел проверку структуры");
            }
            
            return allHeadersPresent;
            
        } catch (Exception e) {
            System.err.println("х Ошибка при чтении CSV файла " + filename + ": " + e.getMessage());
            return false;
        }
    }
    
    private String cleanHeader(String header) {
        return header.replace("\"", "").trim().toLowerCase();
    }
}