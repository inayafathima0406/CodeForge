package com.codeforge.backend.dto.response;

public record TopicProgressResponse(
        String topic,
        int solved,
        int total,
        int percent
) {
}