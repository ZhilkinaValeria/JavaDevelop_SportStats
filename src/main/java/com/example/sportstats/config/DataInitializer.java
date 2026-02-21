package com.example.sportstats.config;

import com.example.sportstats.model.User;
import com.example.sportstats.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@Profile("!test")
public class DataInitializer {
    
    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("user")) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("password"));
                user.setEnabled(true);
                user.setAuthorities(Set.of("ROLE_USER"));
                userRepository.save(user);
            }
            
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setEnabled(true);
                admin.setAuthorities(Set.of("ROLE_USER", "ROLE_ADMIN")); 
                userRepository.save(admin);
            }
        };
    }
}