package com.team.backend.service;

import com.team.backend.model.User;
import com.team.backend.model.UserStatsMonthly;
import com.team.backend.repository.UserStatsMonthlyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final UserStatsMonthlyRepository statsRepository;

    private UserStatsMonthly getOrCreateCurrentMonthStats(User user) {
        YearMonth currentMonth = YearMonth.now();
        return statsRepository.findByUserAndYearAndMonth(
                        user, currentMonth.getYear(), currentMonth.getMonthValue()
                )
                .orElseGet(() -> {
                    UserStatsMonthly newStats = new UserStatsMonthly();
                    newStats.setUser(user);
                    newStats.setYear(currentMonth.getYear());
                    newStats.setMonth(currentMonth.getMonthValue());
                    return statsRepository.save(newStats);
                });
    }

    public void recordLeftSwipe(User user) {
        UserStatsMonthly stats = getOrCreateCurrentMonthStats(user);
        stats.setLeftSwipes(stats.getLeftSwipes() + 1);
        statsRepository.save(stats);
    }

    public void recordRightSwipe(User user) {
        UserStatsMonthly stats = getOrCreateCurrentMonthStats(user);
        stats.setRightSwipes(stats.getRightSwipes() + 1);
        statsRepository.save(stats);
    }

    public void recordMatch(User user) {
        UserStatsMonthly stats = getOrCreateCurrentMonthStats(user);
        stats.setMatches(stats.getMatches() + 1);
        statsRepository.save(stats);
    }

    public void recordConversationStarted(User user1, User user2) {
        UserStatsMonthly stats1 = getOrCreateCurrentMonthStats(user1);
        stats1.setMatchesWithConversation(stats1.getMatchesWithConversation() + 1);
        statsRepository.save(stats1);

        UserStatsMonthly stats2 = getOrCreateCurrentMonthStats(user2);
        stats2.setMatchesWithConversation(stats2.getMatchesWithConversation() + 1);
        statsRepository.save(stats2);
    }

}


