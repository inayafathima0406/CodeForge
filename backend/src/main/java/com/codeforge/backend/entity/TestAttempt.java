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

@Entity
@Table(name = "test_attempts")
public class TestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coding_test_id", nullable = false)
    private CodingTest codingTest;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // IN_PROGRESS, SUBMITTED, TIME_UP
    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private int score;

    @Column(name = "total_score", nullable = false)
    private int totalScore;

    protected TestAttempt() {
    }

    public TestAttempt(User user, CodingTest codingTest, int totalScore) {
        this.user = user;
        this.codingTest = codingTest;
        this.status = "IN_PROGRESS";
        this.score = 0;
        this.totalScore = totalScore;
    }

    @PrePersist
    void onCreate() {
        this.startedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public CodingTest getCodingTest() { return codingTest; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getTotalScore() { return totalScore; }
}