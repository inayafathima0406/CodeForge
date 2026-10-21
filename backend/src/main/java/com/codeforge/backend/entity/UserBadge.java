package com.codeforge.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_badges", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "badge_id"}))
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @Column(name = "awarded_at", nullable = false, updatable = false)
    private LocalDateTime awardedAt;

    protected UserBadge() {
    }

    public UserBadge(User user, Badge badge) {
        this.user = user;
        this.badge = badge;
    }

    @PrePersist
    void onCreate() {
        this.awardedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Badge getBadge() { return badge; }
    public LocalDateTime getAwardedAt() { return awardedAt; }
}