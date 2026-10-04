package com.codeforge.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "languages")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "judge0_language_id", nullable = false)
    private int judge0LanguageId;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected Language() {
    }

    public Language(String code, String name, int judge0LanguageId) {
        this.code = code;
        this.name = name;
        this.judge0LanguageId = judge0LanguageId;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getJudge0LanguageId() { return judge0LanguageId; }
    public boolean isActive() { return active; }
}