package com.codeforge.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.codeforge.backend.dto.request.QuestionRequest;
import com.codeforge.backend.dto.response.QuestionSummaryResponse;
import com.codeforge.backend.service.QuestionAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/questions")
public class QuestionAdminController {

    private final QuestionAdminService questionAdminService;

    public QuestionAdminController(QuestionAdminService questionAdminService) {
        this.questionAdminService = questionAdminService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionSummaryResponse create(@Valid @RequestBody QuestionRequest request) {
        return questionAdminService.create(request);
    }

    @PutMapping("/{id}")
    public QuestionSummaryResponse update(@PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
        return questionAdminService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        questionAdminService.delete(id);
    }
}