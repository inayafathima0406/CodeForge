package com.codeforge.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.codeforge.backend.dto.request.CodeRequest;
import com.codeforge.backend.dto.response.SubmissionDetailResponse;
import com.codeforge.backend.dto.response.SubmissionSummaryResponse;
import com.codeforge.backend.service.SubmissionService;

import jakarta.validation.Valid;

@RestController
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/api/questions/{id}/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionDetailResponse submit(@PathVariable Long id,
                                           @Valid @RequestBody CodeRequest request,
                                           Authentication auth) {
        return submissionService.submit(id, request, auth);
    }

    @GetMapping("/api/submissions")
    public List<SubmissionSummaryResponse> list(Authentication auth) {
        return submissionService.listForUser(auth.getName());
    }

    @GetMapping("/api/submissions/{id}")
    public SubmissionDetailResponse detail(@PathVariable Long id, Authentication auth) {
        return submissionService.getForUser(id, auth.getName());
    }
}