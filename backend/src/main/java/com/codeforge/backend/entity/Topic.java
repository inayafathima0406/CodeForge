package com.codeforge.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "roadmap_order", nullable = false)
    private int roadmapOrder;

    @Column(length = 255)
    private String description;

    protected Topic() {
    }

    public Topic(String name, int roadmapOrder, String description) {
        this.name = name;
        this.roadmapOrder = roadmapOrder;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getRoadmapOrder() { return roadmapOrder; }
    public void setRoadmapOrder(int roadmapOrder) { this.roadmapOrder = roadmapOrder; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}