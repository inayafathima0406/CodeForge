package com.codeforge.backend.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codeforge.backend.entity.Difficulty;
import com.codeforge.backend.entity.Question;
import com.codeforge.backend.entity.TestCase;
import com.codeforge.backend.entity.Topic;
import com.codeforge.backend.repository.QuestionRepository;
import com.codeforge.backend.repository.TestCaseRepository;
import com.codeforge.backend.repository.TopicRepository;

@Configuration
public class QuestionSeeder {

    private static final Logger log = LoggerFactory.getLogger(QuestionSeeder.class);

    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final TestCaseRepository testCaseRepository;

    public QuestionSeeder(TopicRepository topicRepository,
                          QuestionRepository questionRepository,
                          TestCaseRepository testCaseRepository) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.testCaseRepository = testCaseRepository;
    }

    private record Case(String input, String expected, boolean sample, String explanation) {
    }

    @Bean
    CommandLineRunner seedQuestions() {
        return args -> {

            addQuestion("Find Maximum Element", Difficulty.EASY, "Arrays",
                "Given an array of n integers, print the largest element.",
                "The first line contains an integer n.\nThe second line contains n space-separated integers.",
                "Print a single integer: the maximum element of the array.",
                "1 <= n <= 100000\n-1000000000 <= a[i] <= 1000000000",
                List.of(
                    new Case("5\n3 9 2 7 1", "9", true, "9 is the largest of the five numbers."),
                    new Case("3\n-4 -8 -1", "-1", true, "All numbers are negative, and -1 is the largest."),
                    new Case("1\n42", "42", false, null),
                    new Case("6\n5 5 5 5 5 5", "5", false, null),
                    new Case("4\n-1000000000 0 1000000000 999999999", "1000000000", false, null)));

            addQuestion("Reverse a String", Difficulty.EASY, "Strings",
                "Given a string s, print it in reverse order.",
                "A single line containing the string s. It may contain spaces.",
                "Print the reversed string.",
                "1 <= length of s <= 100000",
                List.of(
                    new Case("hello", "olleh", true, "Read from right to left: o, l, l, e, h."),
                    new Case("CodeForge", "egroFedoC", true, "Letter case is preserved."),
                    new Case("a", "a", false, null),
                    new Case("racecar", "racecar", false, null),
                    new Case("hello world", "dlrow olleh", false, null)));

            addQuestion("Binary Search", Difficulty.EASY, "Searching",
                "You are given an array sorted in ascending order and a target value. "
                    + "Print the 0-based index of the target, or -1 if it is not present. "
                    + "Aim for O(log n) time.",
                "The first line contains n.\nThe second line contains n distinct integers in ascending order.\n"
                    + "The third line contains the target integer.",
                "Print the index of the target, or -1 if it does not exist.",
                "1 <= n <= 100000\nAll elements are distinct and sorted in ascending order.",
                List.of(
                    new Case("6\n1 3 5 7 9 11\n7", "3", true, "7 is at index 3."),
                    new Case("5\n2 4 6 8 10\n5", "-1", true, "5 is not in the array."),
                    new Case("1\n10\n10", "0", false, null),
                    new Case("7\n-5 -2 0 3 8 12 20\n-5", "0", false, null),
                    new Case("7\n-5 -2 0 3 8 12 20\n20", "6", false, null),
                    new Case("4\n1 2 3 4\n0", "-1", false, null)));

            addQuestion("Two Sum", Difficulty.EASY, "Hashing",
                "Given an array of integers and a target, find the two different indices i < j "
                    + "such that a[i] + a[j] equals the target. Exactly one such pair exists. "
                    + "Print the two indices (0-based) separated by a space.",
                "The first line contains n.\nThe second line contains n integers.\n"
                    + "The third line contains the target integer.",
                "Print i and j separated by a single space, with i < j.",
                "2 <= n <= 10000\n-1000000000 <= a[i] <= 1000000000\n"
                    + "Sums can exceed the range of a 32-bit int, so use long.\nExactly one valid pair exists.",
                List.of(
                    new Case("4\n2 7 11 15\n9", "0 1", true, "a[0] + a[1] = 2 + 7 = 9."),
                    new Case("3\n3 2 4\n6", "1 2", true, "a[1] + a[2] = 2 + 4 = 6. The same element cannot be used twice."),
                    new Case("2\n3 3\n6", "0 1", false, null),
                    new Case("5\n-1 -2 -3 -4 -5\n-8", "2 4", false, null),
                    new Case("6\n1 5 9 14 20 31\n34", "3 4", false, null)));

            addQuestion("Maximum Subarray", Difficulty.MEDIUM, "Arrays",
                "Given an array of integers, find the contiguous non-empty subarray with the "
                    + "largest sum and print that sum.",
                "The first line contains n.\nThe second line contains n space-separated integers.",
                "Print the maximum possible subarray sum.",
                "1 <= n <= 100000\n-10000 <= a[i] <= 10000",
                List.of(
                    new Case("9\n-2 1 -3 4 -1 2 1 -5 4", "6", true, "The subarray [4, -1, 2, 1] has sum 6."),
                    new Case("1\n5", "5", true, "A single element is a valid subarray."),
                    new Case("5\n-3 -1 -2 -5 -4", "-1", false, null),
                    new Case("5\n1 2 3 4 5", "15", false, null),
                    new Case("8\n5 -9 6 -2 3 1 -8 4", "8", false, null)));

            addQuestion("Trapping Rain Water", Difficulty.HARD, "Arrays",
                "You are given n non-negative integers representing an elevation map where each "
                    + "bar has width 1. Compute how many units of water are trapped after raining.",
                "The first line contains n.\nThe second line contains n non-negative integers, the bar heights.",
                "Print the total units of trapped water.",
                "1 <= n <= 100000\n0 <= height <= 100000",
                List.of(
                    new Case("12\n0 1 0 2 1 0 1 3 2 1 2 1", "6", true, "Six units of water are trapped between the bars."),
                    new Case("6\n4 2 0 3 2 5", "9", true, "Water fills the dip between the bars of height 4 and 5."),
                    new Case("3\n1 2 3", "0", false, null),
                    new Case("5\n5 0 0 0 5", "15", false, null),
                    new Case("1\n7", "0", false, null),
                    new Case("7\n3 0 2 0 4 0 3", "10", false, null)));
        };
    }

    // Safe to run on every start: existing questions and their test cases are not duplicated
    private void addQuestion(String title, Difficulty difficulty, String topicName,
                             String statement, String inputDescription, String outputDescription,
                             String constraints, List<Case> cases) {

        Question question = questionRepository.findByTitle(title).orElse(null);

        if (question == null) {
            Topic topic = topicRepository.findByName(topicName).orElse(null);
            if (topic == null) {
                log.warn("Topic '{}' not found, skipping '{}'. It will be added on the next start.",
                        topicName, title);
                return;
            }
            question = questionRepository.save(new Question(title, statement, inputDescription,
                    outputDescription, constraints, difficulty, topic));
        }

        if (testCaseRepository.countByQuestionId(question.getId()) == 0) {
            int order = 1;
            for (Case c : cases) {
                testCaseRepository.save(new TestCase(question, c.input(), c.expected(),
                        c.sample(), c.explanation(), order++));
            }
        }
    }
}