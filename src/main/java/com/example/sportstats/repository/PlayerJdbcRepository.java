package com.example.sportstats.repository;

import com.example.sportstats.model.Player;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
@Profile("jdbc")
public class PlayerJdbcRepository implements CommonRepository<Player> {
    
    private final JdbcTemplate jdbcTemplate;
    private final PlayerRowMapper rowMapper = new PlayerRowMapper();
    
    public PlayerJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public Player save(Player domain) {
        // Используем MERGE для H2 (аналог INSERT OR REPLACE)
        String sql = "MERGE INTO players KEY(id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
            domain.getId(),
            domain.getName(),
            domain.getTeam(),
            domain.getPosition(),
            domain.getHeightInches(),
            domain.getWeightLbs(),
            domain.getAge()
        );
        
        return domain;
    }
    
    @Override
    public Iterable<Player> save(Collection<Player> domains) {
        domains.forEach(this::save);
        return domains;
    }
    
    @Override
    public void delete(String id) {
        String sql = "DELETE FROM players WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    @Override
    public void delete(Player domain) {
        delete(domain.getId());
    }
    
    @Override
    public Player findById(String id) {
        String sql = "SELECT * FROM players WHERE id = ?";
        List<Player> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public Iterable<Player> findAll() {
        String sql = "SELECT * FROM players";
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM players WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM players";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    private static class PlayerRowMapper implements RowMapper<Player> {
        @Override
        public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
            Player player = new Player();
            player.setId(rs.getString("id"));
            player.setName(rs.getString("name"));
            player.setTeam(rs.getString("team"));
            player.setPosition(rs.getString("position"));
            player.setHeightInches(rs.getInt("height_inches"));
            player.setWeightLbs(rs.getInt("weight_lbs"));
            player.setAge(rs.getDouble("age"));
            return player;
        }
    }
}