package com.codeforge.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeforge.backend.entity.Badge;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> findByCode(String code);
}