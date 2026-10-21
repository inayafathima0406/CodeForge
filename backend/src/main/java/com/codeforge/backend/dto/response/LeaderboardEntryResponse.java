package com.codeforge.backend.dto.response;

public record LeaderboardEntryResponse(
        int rank,
        String username,
        String fullName,
        int xp,
        int problemsSolved
) {
}