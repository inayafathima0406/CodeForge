package com.codeforge.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.dto.request.LoginRequest;
import com.codeforge.backend.dto.request.RegisterRequest;
import com.codeforge.backend.dto.response.LoginResponse;
import com.codeforge.backend.dto.response.UserResponse;
import com.codeforge.backend.entity.Role;
import com.codeforge.backend.entity.User;
import com.codeforge.backend.exception.DuplicateResourceException;
import com.codeforge.backend.exception.InvalidCredentialsException;
import com.codeforge.backend.repository.RoleRepository;
import com.codeforge.backend.repository.UserRepository;
import com.codeforge.backend.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // Same message for "no such user" and "wrong password", so attackers can't tell which
        User user = userRepository.findByUsername(request.username().trim())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().getName());
        return new LoginResponse(token, "Bearer", jwtService.getExpirationMinutes(), UserResponse.from(user));
    }
}