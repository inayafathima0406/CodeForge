package com.codeforge.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.dto.request.QuestionRequest;
import com.codeforge.backend.dto.request.TestCaseRequest;
import com.codeforge.backend.dto.response.QuestionSummaryResponse;
import com.codeforge.backend.entity.Difficulty;
import com.codeforge.backend.entity.Question;
import com.codeforge.backend.entity.TestCase;
import com.codeforge.backend.entity.Topic;
import com.codeforge.backend.exception.DuplicateResourceException;
import com.codeforge.backend.exception.ResourceNotFoundException;
import com.codeforge.backend.repository.QuestionRepository;
import com.codeforge.backend.repository.TestCaseRepository;
import com.codeforge.backend.repository.TopicRepository;

@Service
public class QuestionAdminService {

    private final QuestionRepository questionRepository;
    private final TestCaseRepository testCaseRepository;
    private final TopicRepository topicRepository;

    public QuestionAdminService(QuestionRepository questionRepository,
                                TestCaseRepository testCaseRepository,
                                TopicRepository topicRepository) {
        this.questionRepository = questionRepository;
        this.testCaseRepository = testCaseRepository;
        this.topicRepository = topicRepository;
    }

    @Transactional
    public QuestionSummaryResponse create(QuestionRequest request) {
        if (questionRepository.findByTitle(request.title().trim()).isPresent()) {
            throw new DuplicateResourceException("A question with this title already exists");
        }

        Topic topic = findTopic(request.topic());
        Difficulty difficulty = parseDifficulty(request.difficulty());

        Question question = questionRepository.save(new Question(
                request.title().trim(), request.statement(), request.inputDescription(),
                request.outputDescription(), request.constraints(), difficulty, topic));

        saveTestCases(question, request.testCases());

        return QuestionSummaryResponse.from(question);
    }

    @Transactional
    public QuestionSummaryResponse update(Long id, QuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        Topic topic = findTopic(request.topic());
        Difficulty difficulty = parseDifficulty(request.difficulty());

        question.setTitle(request.title().trim());
        question.setStatement(request.statement());
        question.setInputDescription(request.inputDescription());
        question.setOutputDescription(request.outputDescription());
        question.setConstraintsText(request.constraints());
        question.setDifficulty(difficulty);
        question.setTopic(topic);

        // Replace the test cases entirely, rather than trying to match old ones to new ones
        testCaseRepository.deleteAll(
                testCaseRepository.findByQuestionIdOrderByDisplayOrderAsc(id));
        saveTestCases(question, request.testCases());

        return QuestionSummaryResponse.from(question);
    }

    @Transactional
    public void delete(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        testCaseRepository.deleteAll(
                testCaseRepository.findByQuestionIdOrderByDisplayOrderAsc(id));
        questionRepository.delete(question);
    }

    private void saveTestCases(Question question, java.util.List<TestCaseRequest> requests) {
        int order = 1;
        for (TestCaseRequest tc : requests) {
            testCaseRepository.save(new TestCase(
                    question, tc.inputData(), tc.expectedOutput(), tc.sample(), tc.explanation(), order++));
        }
    }

    private Topic findTopic(String name) {
        return topicRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Topic '" + name + "' does not exist"));
    }

    private Difficulty parseDifficulty(String value) {
        try {
            return Difficulty.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Difficulty must be EASY, MEDIUM, or HARD");
        }
    }
}