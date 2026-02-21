package com.example.earthquakes.repository;

import com.example.earthquakes.model.Earthquake;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
@Profile("jdbc")
public class EarthquakeJdbcRepository implements CommonRepository<Earthquake> {
    
    private final JdbcTemplate jdbcTemplate;
    private final EarthquakeRowMapper rowMapper = new EarthquakeRowMapper();
    
    public EarthquakeJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public Earthquake save(Earthquake domain) {
        String sql = "INSERT INTO earthquakes (id, time, latitude, longitude, depth, magnitude, place, magnitude_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "time = VALUES(time), latitude = VALUES(latitude), longitude = VALUES(longitude), " +
                    "depth = VALUES(depth), magnitude = VALUES(magnitude), place = VALUES(place), " +
                    "magnitude_type = VALUES(magnitude_type)";
        
        jdbcTemplate.update(sql,
            domain.getId(),
            domain.getTime(),
            domain.getLatitude(),
            domain.getLongitude(),
            domain.getDepth(),
            domain.getMagnitude(),
            domain.getPlace(),
            domain.getMagType()
        );
        
        return domain;
    }
    
    @Override
    public Iterable<Earthquake> save(Collection<Earthquake> domains) {
        domains.forEach(this::save);
        return domains;
    }
    
    @Override
    public void delete(String id) {
        String sql = "DELETE FROM earthquakes WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    @Override
    public void delete(Earthquake domain) {
        delete(domain.getId());
    }
    
    @Override
    public Earthquake findById(String id) {
        String sql = "SELECT * FROM earthquakes WHERE id = ?";
        List<Earthquake> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public Iterable<Earthquake> findAll() {
        String sql = "SELECT * FROM earthquakes";
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM earthquakes WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM earthquakes";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    private static class EarthquakeRowMapper implements RowMapper<Earthquake> {
        @Override
        public Earthquake mapRow(ResultSet rs, int rowNum) throws SQLException {
            Earthquake eq = new Earthquake();
            eq.setId(rs.getString("id"));
            eq.setTime(rs.getObject("time", LocalDateTime.class));
            eq.setLatitude(rs.getDouble("latitude"));
            eq.setLongitude(rs.getDouble("longitude"));
            eq.setDepth(rs.getDouble("depth"));
            eq.setMagnitude(rs.getDouble("magnitude"));
            eq.setPlace(rs.getString("place"));
            eq.setMagType(rs.getString("magnitude_type"));
            return eq;
        }
    }
}