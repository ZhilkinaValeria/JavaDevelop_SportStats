package com.example.sportstats.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@TestConfiguration
@Profile("test")
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class TestSecurityConfig {
    
    @Bean
    @Primary
    public UserDetailsService testUserDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        manager.createUser(User.withUsername("user")
                .password("{noop}password")
                .roles("USER")
                .build());
        
        manager.createUser(User.withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build());
        
        return manager;
    }
    
    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Публичные эндпоинты (доступны без авторизации)
                .requestMatchers(new AntPathRequestMatcher("/api/players/stats/average-age")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/players/stats/average-height")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/players/stats/average-weight")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                
                // Защищенные эндпоинты (требуют авторизации)
                .requestMatchers(new AntPathRequestMatcher("/api/players/**")).authenticated()
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> httpBasic.realmName("Sport Stats API"))
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                .disable()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        
        return http.build();
    }
}