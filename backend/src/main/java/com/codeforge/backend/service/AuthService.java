package com.codeforge.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.dto.request.RegisterRequest;
import com.codeforge.backend.dto.response.UserResponse;
import com.codeforge.backend.entity.Role;
import com.codeforge.backend.entity.User;
import com.codeforge.backend.exception.DuplicateResourceException;
import com.codeforge.backend.repository.RoleRepository;
import com.codeforge.backend.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already registered");
        }

        // Every new account is a STUDENT. The client can never choose its own role.
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new IllegalStateException("STUDENT role is missing"));

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(username, email, passwordHash,
                request.fullName().trim(), studentRole);

        return UserResponse.from(userRepository.save(user));
    }
}