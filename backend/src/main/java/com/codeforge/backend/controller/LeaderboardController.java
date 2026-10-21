package com.codeforge.backend.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codeforge.backend.dto.response.BadgeResponse;
import com.codeforge.backend.dto.response.LeaderboardEntryResponse;
import com.codeforge.backend.service.LeaderboardService;

@RestController
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/api/leaderboard")
    public List<LeaderboardEntryResponse> leaderboard(@RequestParam(defaultValue = "xp") String type) {
        return leaderboardService.getLeaderboard(type);
    }

    @GetMapping("/api/users/me/badges")
    public List<BadgeResponse> myBadges(Authentication auth) {
        return leaderboardService.getBadgesForUser(auth.getName());
    }
}