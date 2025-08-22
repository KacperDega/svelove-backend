package com.team.backend.model.mapper;

import com.team.backend.model.UserStatsMonthly;
import com.team.backend.model.dto.AvailableStatsDto;
import com.team.backend.model.dto.UserStatsDto;

import java.time.YearMonth;

public class UserStatsMapper {

    public static UserStatsDto toUserStatsDto(UserStatsMonthly stats) {
        return new UserStatsDto(
                stats.getYear(),
                stats.getMonth(),
                stats.getLeftSwipes(),
                stats.getRightSwipes(),
                stats.getTotalSwipes(),
                stats.getMatches(),
                stats.getMatchesWithConversation(),
                stats.getMatchesWithoutMessages(),
                stats.getUpdatedAt()
        );
    }

    public static AvailableStatsDto toAvailableStatsDto(YearMonth yearMonth) {
        return new AvailableStatsDto(yearMonth.getYear(), yearMonth.getMonthValue());
    }
}

