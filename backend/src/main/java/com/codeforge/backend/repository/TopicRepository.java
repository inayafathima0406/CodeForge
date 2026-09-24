package com.codeforge.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeforge.backend.entity.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByName(String name);

    List<Topic> findAllByOrderByRoadmapOrderAsc();
}