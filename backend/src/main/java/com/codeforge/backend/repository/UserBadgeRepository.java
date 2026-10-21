package com.codeforge.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codeforge.backend.entity.UserBadge;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    @Query("select ub from UserBadge ub join fetch ub.badge where ub.user.username = :username order by ub.awardedAt desc")
    List<UserBadge> findByUsernameWithBadge(@Param("username") String username);

    boolean existsByUserIdAndBadgeCode(Long userId, String code);
}