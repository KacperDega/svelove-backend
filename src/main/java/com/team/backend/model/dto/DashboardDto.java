package com.team.backend.model.dto;

import java.util.List;

public record DashboardDto(
        String username,
        String profilePictureUrl,
        int newMatchesCount,
        int newMessagesCount,
        List<NotificationDto> notifications,
        UserStatsDto currentMonthStats
) {
}
