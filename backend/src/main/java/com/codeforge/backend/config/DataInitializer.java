package com.codeforge.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codeforge.backend.entity.Role;
import com.codeforge.backend.repository.RoleRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> {
            for (String name : new String[] { "STUDENT", "ADMIN" }) {
                if (roleRepository.findByName(name).isEmpty()) {
                    roleRepository.save(new Role(name));
                }
            }
        };
    }
}