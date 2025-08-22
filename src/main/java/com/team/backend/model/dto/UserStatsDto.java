package com.team.backend.model.dto;

import java.time.LocalDateTime;

public record UserStatsDto(
        int year,
        int month,
        long leftSwipes,
        long rightSwipes,
        long totalSwipes,
        long matches,
        long matchesWithConversation,
        long matchesWithoutMessages,
        LocalDateTime updatedAt
) {}


