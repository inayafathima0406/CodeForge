package com.codeforge.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codeforge.backend.entity.Role;
import com.codeforge.backend.entity.Topic;
import com.codeforge.backend.repository.RoleRepository;
import com.codeforge.backend.repository.TopicRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> {
            for (String name : new String[] { "STUDENT", "ADMIN" }) {
                if (roleRepository.findByName(name).isEmpty()) {
                    roleRepository.save(new Role(name));
                }
            }
        };
    }

    @Bean
    CommandLineRunner seedTopics(TopicRepository topicRepository) {
        return args -> {
            // Stack and Queue share order 8 because the roadmap shows them as one step
            addTopic(topicRepository, "Arrays", 1, "Indexing, traversal, prefix sums, two pointers");
            addTopic(topicRepository, "Strings", 2, "String manipulation, palindromes, anagrams");
            addTopic(topicRepository, "Searching", 3, "Linear and binary search");
            addTopic(topicRepository, "Sorting", 4, "Sorting algorithms and their trade-offs");
            addTopic(topicRepository, "Recursion", 5, "Recursive thinking and backtracking");
            addTopic(topicRepository, "Hashing", 6, "HashMap, HashSet, frequency counting");
            addTopic(topicRepository, "Linked List", 7, "Singly and doubly linked lists");
            addTopic(topicRepository, "Stack", 8, "LIFO problems, monotonic stack");
            addTopic(topicRepository, "Queue", 8, "FIFO problems, BFS building block");
            addTopic(topicRepository, "Trees", 9, "Binary trees, BSTs, traversals");
            addTopic(topicRepository, "Graphs", 10, "BFS, DFS, shortest paths");
            addTopic(topicRepository, "Dynamic Programming", 11, "Overlapping subproblems, memoization, tabulation");
        };
    }

    private void addTopic(TopicRepository repository, String name, int order, String description) {
        if (repository.findByName(name).isEmpty()) {
            repository.save(new Topic(name, order, description));
        }
    }
}