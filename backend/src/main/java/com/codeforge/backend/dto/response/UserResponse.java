package com.codeforge.backend.dto.response;

import java.time.LocalDateTime;

import com.codeforge.backend.entity.User;

public record UserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        String role,
        LocalDateTime createdAt
) {

    // Converts a User entity into a safe response object
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().getName(),
                user.getCreatedAt());
    }
}