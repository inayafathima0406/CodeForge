package com.codeforge.backend.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.entity.Submission;
import com.codeforge.backend.entity.User;
import com.codeforge.backend.entity.UserBadge;
import com.codeforge.backend.repository.BadgeRepository;
import com.codeforge.backend.repository.SubmissionRepository;
import com.codeforge.backend.repository.UserBadgeRepository;
import com.codeforge.backend.repository.UserRepository;

@Service
public class GamificationService {

    private static final int XP_PER_SOLVE = 10;

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public GamificationService(UserRepository userRepository,
                               SubmissionRepository submissionRepository,
                               BadgeRepository badgeRepository,
                               UserBadgeRepository userBadgeRepository) {
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    // Called right after any submission is saved. Cheap no-ops if nothing new is earned.
    @Transactional
    public void onSubmissionSaved(Long userId, String status) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        awardBadgeIfMissing(user, "BEGINNER");

        if (!status.equals("ACCEPTED")) {
            return;
        }

        List<Submission> all = submissionRepository.findByUsernameWithQuestion(user.getUsername());
        long distinctSolved = all.stream()
                .filter(s -> s.getStatus().equals("ACCEPTED"))
                .map(s -> s.getQuestion().getId())
                .distinct()
                .count();

        if (distinctSolved >= 1) {
            awardBadgeIfMissing(user, "FIRST_SOLVE");
        }
        if (distinctSolved >= 25) {
            awardBadgeIfMissing(user, "SOLVED_25");
        }
        if (distinctSolved >= 50) {
            awardBadgeIfMissing(user, "SOLVED_50");
        }
        if (computeStreak(submissionRepository.findDistinctSubmissionDates(user.getUsername())) >= 7) {
            awardBadgeIfMissing(user, "STREAK_7");
        }

        user.setXp((int) distinctSolved * XP_PER_SOLVE);
        userRepository.save(user);
    }

    private void awardBadgeIfMissing(User user, String badgeCode) {
        if (userBadgeRepository.existsByUserIdAndBadgeCode(user.getId(), badgeCode)) return;
        badgeRepository.findByCode(badgeCode).ifPresent(badge ->
                userBadgeRepository.save(new UserBadge(user, badge)));
    }

    private int computeStreak(List<Date> dates) {
        if (dates.isEmpty()) return 0;
        var set = dates.stream().map(Date::toLocalDate).collect(Collectors.toCollection(TreeSet::new));
        LocalDate cursor = LocalDate.now();
        if (!set.contains(cursor)) {
            cursor = cursor.minusDays(1);
            if (!set.contains(cursor)) return 0;
        }
        int streak = 0;
        while (set.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }
}