package com.example.sportstats.repository;

import com.example.sportstats.model.Player;
import com.example.sportstats.util.CsvParser;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository("CsvRepository")
@Profile("csv")
public class CsvRepository implements CommonRepository<Player> {
    
    private final Map<String, Player> storage = new ConcurrentHashMap<>();
    private final CsvParser csvParser;
    
    public CsvRepository(CsvParser csvParser) {
        this.csvParser = csvParser;
    }
    
    @PostConstruct
    public void init() {
        List<Player> players = csvParser.parseCsv("players.csv");
        players.forEach(p -> storage.put(p.getId(), p));
        System.out.println("Loaded " + players.size() + " players from CSV");
    }
    
    @Override
    public Player save(Player domain) {
        if (domain == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        storage.put(domain.getId(), domain);
        return domain;
    }
    
    @Override
    public Iterable<Player> save(Collection<Player> domains) {
        domains.forEach(d -> storage.put(d.getId(), d));
        return domains;
    }
    
    @Override
    public void delete(String id) {
        storage.remove(id);
    }
    
    @Override
    public void delete(Player domain) {
        storage.remove(domain.getId());
    }
    
    @Override
    public Player findById(String id) {
        return storage.get(id);
    }
    
    @Override
    public Iterable<Player> findAll() {
        return storage.values();
    }
    
    @Override
    public boolean exists(String id) {
        return storage.containsKey(id);
    }
    
    @Override
    public long count() {
        return storage.size();
    }
}