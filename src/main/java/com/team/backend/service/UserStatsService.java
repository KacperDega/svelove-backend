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

    public List<YearMonth> getAvailableYearMonthsForStats(User user) {
        return statsRepository.findAllByUser(user).stream()
                .map(s -> YearMonth.of(s.getYear(), s.getMonth()))
                .sorted()
                .toList();
    }

    public UserStatsMonthly getStatsForMonth(User user, int year, int month) {
        return statsRepository.findByUserAndYearAndMonth(user, year, month)
                .orElseThrow(() -> new RuntimeException("No available stats for given period: " + month + "/" + year));
    }

    private UserStatsMonthly aggregateStats(User user, int year, int month, List<UserStatsMonthly> stats) {
        UserStatsMonthly aggregated = new UserStatsMonthly();
        aggregated.setUser(user);
        aggregated.setYear(year);
        aggregated.setMonth(month);

        stats.forEach(s -> {
            aggregated.setLeftSwipes(aggregated.getLeftSwipes() + s.getLeftSwipes());
            aggregated.setRightSwipes(aggregated.getRightSwipes() + s.getRightSwipes());
            aggregated.setMatches(aggregated.getMatches() + s.getMatches());
            aggregated.setMatchesWithConversation(aggregated.getMatchesWithConversation() + s.getMatchesWithConversation());
        });

        return aggregated;
    }

    public UserStatsMonthly getStatsForYear(User user, int year) {
        List<UserStatsMonthly> stats = statsRepository.findAllByUserAndYear(user, year);
        return aggregateStats(user, year, 0, stats);
    }

    public UserStatsMonthly getLifetimeStats(User user) {
        List<UserStatsMonthly> stats = statsRepository.findAllByUser(user);
        return aggregateStats(user, 0, 0, stats);
    }

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


