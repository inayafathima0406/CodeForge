package com.codeforge.backend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.dto.response.BadgeResponse;
import com.codeforge.backend.dto.response.LeaderboardEntryResponse;
import com.codeforge.backend.entity.Badge;
import com.codeforge.backend.entity.User;
import com.codeforge.backend.repository.BadgeRepository;
import com.codeforge.backend.repository.SubmissionRepository;
import com.codeforge.backend.repository.UserBadgeRepository;
import com.codeforge.backend.repository.UserRepository;

@Service
public class LeaderboardService {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public LeaderboardService(UserRepository userRepository,
                              SubmissionRepository submissionRepository,
                              BadgeRepository badgeRepository,
                              UserBadgeRepository userBadgeRepository) {
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getLeaderboard(String type) {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getRole().getName().equals("STUDENT"))
                .toList();

        List<LeaderboardEntryResponse> entries = users.stream()
                .map(u -> {
                    long solved = submissionRepository.findByUsernameWithQuestion(u.getUsername()).stream()
                            .filter(s -> s.getStatus().equals("ACCEPTED"))
                            .map(s -> s.getQuestion().getId())
                            .distinct()
                            .count();
                    return new LeaderboardEntryResponse(0, u.getUsername(), u.getFullName(), u.getXp(), (int) solved);
                })
                .sorted(type.equals("solved")
                        ? Comparator.comparingInt(LeaderboardEntryResponse::problemsSolved).reversed()
                        : Comparator.comparingInt(LeaderboardEntryResponse::xp).reversed())
                .toList();

        List<LeaderboardEntryResponse> ranked = new ArrayList<>();
        int rank = 1;
        for (LeaderboardEntryResponse e : entries) {
            ranked.add(new LeaderboardEntryResponse(rank++, e.username(), e.fullName(), e.xp(), e.problemsSolved()));
        }
        return ranked;
    }

    @Transactional(readOnly = true)
    public List<BadgeResponse> getBadgesForUser(String username) {
        List<Badge> allBadges = badgeRepository.findAll();
        var earned = userBadgeRepository.findByUsernameWithBadge(username);

        return allBadges.stream()
                .map(b -> {
                    var match = earned.stream().filter(ub -> ub.getBadge().getId().equals(b.getId())).findFirst();
                    return new BadgeResponse(b.getCode(), b.getName(), b.getDescription(), b.getIcon(),
                            match.isPresent(), match.map(ub -> ub.getAwardedAt()).orElse(null));
                })
                .toList();
    }
}