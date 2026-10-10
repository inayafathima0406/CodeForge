package com.codeforge.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "coding_test_questions")
public class CodingTestQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coding_test_id", nullable = false)
    private CodingTest codingTest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private int points;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    protected CodingTestQuestion() {
    }

    public CodingTestQuestion(CodingTest codingTest, Question question, int points, int displayOrder) {
        this.codingTest = codingTest;
        this.question = question;
        this.points = points;
        this.displayOrder = displayOrder;
    }

    public Long getId() { return id; }
    public CodingTest getCodingTest() { return codingTest; }
    public Question getQuestion() { return question; }
    public int getPoints() { return points; }
    public int getDisplayOrder() { return displayOrder; }
}