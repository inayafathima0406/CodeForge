package com.codeforge.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.dto.response.QuestionDetailResponse;
import com.codeforge.backend.dto.response.QuestionSummaryResponse;
import com.codeforge.backend.entity.Question;
import com.codeforge.backend.entity.TestCase;
import com.codeforge.backend.exception.ResourceNotFoundException;
import com.codeforge.backend.repository.QuestionRepository;
import com.codeforge.backend.repository.TestCaseRepository;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TestCaseRepository testCaseRepository;

    public QuestionService(QuestionRepository questionRepository,
                           TestCaseRepository testCaseRepository) {
        this.questionRepository = questionRepository;
        this.testCaseRepository = testCaseRepository;
    }

    // Every filter is optional. A null or blank value means "do not filter on this".
    @Transactional(readOnly = true)
    public List<QuestionSummaryResponse> list(String topic, String difficulty, String search) {
        return questionRepository.findAllPublishedWithTopic().stream()
                .filter(q -> isBlank(topic) || q.getTopic().getName().equalsIgnoreCase(topic.trim()))
                .filter(q -> isBlank(difficulty) || q.getDifficulty().name().equalsIgnoreCase(difficulty.trim()))
                .filter(q -> isBlank(search) || q.getTitle().toLowerCase().contains(search.trim().toLowerCase()))
                .map(QuestionSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionDetailResponse getDetail(Long id) {
        Question question = questionRepository.findPublishedWithTopicById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Sample cases only. Hidden cases are never loaded for this response.
        List<TestCase> samples =
                testCaseRepository.findByQuestionIdAndSampleTrueOrderByDisplayOrderAsc(id);

        return QuestionDetailResponse.from(question, samples);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}