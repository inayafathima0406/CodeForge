package com.codeforge.backend.dto.response;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInMinutes,
        UserResponse user
) {
}