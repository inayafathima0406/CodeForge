package com.codeforge.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "mentor_feedback")
public class MentorFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;

    @Column(name = "error_type", nullable = false, length = 50)
    private String errorType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String explanation;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String hint;

    @Column(name = "concepts_to_revise", nullable = false, length = 300)
    private String conceptsToRevise;

    @Column(name = "complexity_feedback", columnDefinition = "TEXT")
    private String complexityFeedback;

    // Null until the student explicitly asks for the full solution
    @Column(name = "suggested_solution", columnDefinition = "TEXT")
    private String suggestedSolution;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected MentorFeedback() {
    }

    public MentorFeedback(Submission submission, String errorType, String explanation, String hint,
                          String conceptsToRevise, String complexityFeedback) {
        this.submission = submission;
        this.errorType = errorType;
        this.explanation = explanation;
        this.hint = hint;
        this.conceptsToRevise = conceptsToRevise;
        this.complexityFeedback = complexityFeedback;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getErrorType() { return errorType; }
    public String getExplanation() { return explanation; }
    public String getHint() { return hint; }
    public String getConceptsToRevise() { return conceptsToRevise; }
    public String getComplexityFeedback() { return complexityFeedback; }
    public String getSuggestedSolution() { return suggestedSolution; }
    public void setSuggestedSolution(String suggestedSolution) { this.suggestedSolution = suggestedSolution; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}