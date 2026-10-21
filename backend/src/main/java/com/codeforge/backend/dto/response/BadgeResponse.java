package com.codeforge.backend.dto.response;

import java.time.LocalDateTime;

public record BadgeResponse(
        String code,
        String name,
        String description,
        String icon,
        boolean earned,
        LocalDateTime awardedAt
) {
}