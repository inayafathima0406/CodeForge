package com.codeforge.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codeforge.backend.dto.response.QuestionDetailResponse;
import com.codeforge.backend.dto.response.QuestionSummaryResponse;
import com.codeforge.backend.service.QuestionService;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public List<QuestionSummaryResponse> list(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String search) {
        return questionService.list(topic, difficulty, search);
    }

    @GetMapping("/{id}")
    public QuestionDetailResponse detail(@PathVariable Long id) {
        return questionService.getDetail(id);
    }
}