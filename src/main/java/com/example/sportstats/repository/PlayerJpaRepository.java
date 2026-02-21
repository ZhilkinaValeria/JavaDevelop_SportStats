package com.example.sportstats.repository;

import com.example.sportstats.model.Player;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("jpa")
public interface PlayerJpaRepository extends JpaRepository<Player, String> {
    
    // Поиск игроков по команде
    List<Player> findByTeam(String team);
    
    // Поиск игроков по позиции
    List<Player> findByPosition(String position);
    
    // Поиск игроков по возрасту (старше указанного)
    List<Player> findByAgeGreaterThan(Double age);
    
    // Поиск игроков по возрасту (младше указанного)
    List<Player> findByAgeLessThan(Double age);
    
    // Поиск игроков по диапазону возраста
    List<Player> findByAgeBetween(Double startAge, Double endAge);
    
    // Поиск игроков по росту (выше указанного в дюймах)
    List<Player> findByHeightInchesGreaterThan(Integer height);
    
    // Поиск игроков по весу (тяжелее указанного в фунтах)
    List<Player> findByWeightLbsGreaterThan(Integer weight);
    
    // Поиск игроков по имени (содержит текст)
    List<Player> findByNameContaining(String name);
    
    // Поиск игроков по команде и позиции
    List<Player> findByTeamAndPosition(String team, String position);
    
    // Средний возраст игроков
    @Query("SELECT AVG(p.age) FROM Player p")
    Double findAverageAge();
    
    // Средний рост в дюймах
    @Query("SELECT AVG(p.heightInches) FROM Player p")
    Double findAverageHeightInches();
    
    // Средний вес в фунтах
    @Query("SELECT AVG(p.weightLbs) FROM Player p")
    Double findAverageWeightLbs();
    
    // Количество игроков по командам
    @Query("SELECT p.team, COUNT(p) FROM Player p GROUP BY p.team")
    List<Object[]> countPlayersByTeam();
    
    // Количество игроков по позициям
    @Query("SELECT p.position, COUNT(p) FROM Player p GROUP BY p.position")
    List<Object[]> countPlayersByPosition();
    
    // Максимальный рост
    @Query("SELECT MAX(p.heightInches) FROM Player p")
    Integer findMaxHeight();
    
    // Минимальный рост
    @Query("SELECT MIN(p.heightInches) FROM Player p")
    Integer findMinHeight();
    
    // Максимальный вес
    @Query("SELECT MAX(p.weightLbs) FROM Player p")
    Integer findMaxWeight();
    
    // Минимальный вес
    @Query("SELECT MIN(p.weightLbs) FROM Player p")
    Integer findMinWeight();
    
    // Самый молодой игрок
    @Query("SELECT p FROM Player p WHERE p.age = (SELECT MIN(p2.age) FROM Player p2)")
    List<Player> findYoungestPlayers();
    
    // Самый возрастной игрок
    @Query("SELECT p FROM Player p WHERE p.age = (SELECT MAX(p2.age) FROM Player p2)")
    List<Player> findOldestPlayers();
    
    // Статистика по конкретной команде
    @Query("SELECT AVG(p.age), AVG(p.heightInches), AVG(p.weightLbs), COUNT(p) " +
           "FROM Player p WHERE p.team = :teamCode")
    Object[] getTeamStats(@Param("teamCode") String teamCode);
    
    // Поиск игроков с BMI выше указанного
    @Query("SELECT p FROM Player p WHERE " +
           "(p.weightLbs * 703.0) / (p.heightInches * p.heightInches) > :bmi")
    List<Player> findPlayersWithBmiGreaterThan(@Param("bmi") Double bmi);
    
    // Топ-N самых высоких игроков
    List<Player> findTop10ByOrderByHeightInchesDesc();
    
    // Топ-N самых тяжелых игроков
    List<Player> findTop10ByOrderByWeightLbsDesc();
}