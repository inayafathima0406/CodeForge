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
@Table(name = "submission_results")
public class SubmissionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_case_id", nullable = false)
    private TestCase testCase;

    @Column(name = "case_number", nullable = false)
    private int caseNumber;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "exec_time_ms")
    private Integer execTimeMs;

    @Column(name = "memory_kb")
    private Integer memoryKb;

    protected SubmissionResult() {
    }

    public SubmissionResult(Submission submission, TestCase testCase, int caseNumber,
                            String status, Integer execTimeMs, Integer memoryKb) {
        this.submission = submission;
        this.testCase = testCase;
        this.caseNumber = caseNumber;
        this.status = status;
        this.execTimeMs = execTimeMs;
        this.memoryKb = memoryKb;
    }

    public Long getId() { return id; }
    public int getCaseNumber() { return caseNumber; }
    public String getStatus() { return status; }
    public Integer getExecTimeMs() { return execTimeMs; }
    public Integer getMemoryKb() { return memoryKb; }
}