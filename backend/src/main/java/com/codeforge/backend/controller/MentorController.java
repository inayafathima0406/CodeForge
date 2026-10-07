package com.codeforge.backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeforge.backend.dto.response.MentorResponse;
import com.codeforge.backend.service.MentorService;

@RestController
public class MentorController {

    private final MentorService mentorService;

    public MentorController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    @PostMapping("/api/submissions/{id}/mentor")
    public MentorResponse mentor(@PathVariable Long id, Authentication auth) {
        return mentorService.getFeedback(id, auth.getName(), false);
    }

    @PostMapping("/api/submissions/{id}/mentor/solution")
    public MentorResponse mentorSolution(@PathVariable Long id, Authentication auth) {
        return mentorService.getFeedback(id, auth.getName(), true);
    }
}