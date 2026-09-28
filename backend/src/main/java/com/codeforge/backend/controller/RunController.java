package com.codeforge.backend.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeforge.backend.dto.request.CodeRequest;
import com.codeforge.backend.dto.response.RunResponse;
import com.codeforge.backend.service.RunService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/questions")
public class RunController {

    private final RunService runService;

    public RunController(RunService runService) {
        this.runService = runService;
    }

    @PostMapping("/{id}/run")
    public RunResponse run(@PathVariable Long id, @Valid @RequestBody CodeRequest request) {
        return runService.run(id, request);
    }
}