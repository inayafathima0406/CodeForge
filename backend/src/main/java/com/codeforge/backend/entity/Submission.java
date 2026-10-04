package com.codeforge.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "submissions", indexes = @Index(name = "idx_sub_user_created", columnList = "user_id, created_at"))
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @Column(name = "source_code", nullable = false, columnDefinition = "TEXT")
    private String sourceCode;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "exec_time_ms")
    private Integer execTimeMs;

    @Column(name = "memory_kb")
    private Integer memoryKb;

    @Column(name = "passed_count", nullable = false)
    private int passedCount;

    @Column(name = "total_count", nullable = false)
    private int totalCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Submission() {
    }

    public Submission(User user, Question question, Language language, String sourceCode,
                      String status, Integer execTimeMs, Integer memoryKb,
                      int passedCount, int totalCount) {
        this.user = user;
        this.question = question;
        this.language = language;
        this.sourceCode = sourceCode;
        this.status = status;
        this.execTimeMs = execTimeMs;
        this.memoryKb = memoryKb;
        this.passedCount = passedCount;
        this.totalCount = totalCount;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Question getQuestion() { return question; }
    public Language getLanguage() { return language; }
    public String getSourceCode() { return sourceCode; }
    public String getStatus() { return status; }
    public Integer getExecTimeMs() { return execTimeMs; }
    public Integer getMemoryKb() { return memoryKb; }
    public int getPassedCount() { return passedCount; }
    public int getTotalCount() { return totalCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}