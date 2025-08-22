package com.team.backend.service;

import com.team.backend.model.User;
import com.team.backend.model.UserStatsMonthly;
import com.team.backend.repository.UserStatsMonthlyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserStatsServiceTest {

    @Mock
    private UserStatsMonthlyRepository statsRepository;

    @InjectMocks
    private UserStatsService statsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testUser");
    }

    @Test
    void getAvailableYearMonthsForStats_whenStatsExist_returnsListOfYearMonths() {
        UserStatsMonthly jan = new UserStatsMonthly();
        jan.setYear(2024);
        jan.setMonth(1);
        jan.setUser(testUser);

        UserStatsMonthly feb = new UserStatsMonthly();
        feb.setYear(2024);
        feb.setMonth(2);
        feb.setUser(testUser);

        when(statsRepository.findAllByUser(testUser)).thenReturn(List.of(jan, feb));

        List<YearMonth> result = statsService.getAvailableYearMonthsForStats(testUser);

        assertEquals(2, result.size());
        assertTrue(result.contains(YearMonth.of(2024, 1)));
        assertTrue(result.contains(YearMonth.of(2024, 2)));
    }

    @Test
    void getStatsForMonth_whenStatsExist_returnsStats() {
        UserStatsMonthly stats = new UserStatsMonthly();
        stats.setYear(2024);
        stats.setMonth(3);
        stats.setUser(testUser);

        when(statsRepository.findByUserAndYearAndMonth(testUser, 2024, 3))
                .thenReturn(Optional.of(stats));

        UserStatsMonthly result = statsService.getStatsForMonth(testUser, 2024, 3);

        assertEquals(2024, result.getYear());
        assertEquals(3, result.getMonth());
    }

    @Test
    void getStatsForMonth_whenStatsMissing_throwsException() {
        when(statsRepository.findByUserAndYearAndMonth(testUser, 2024, 5))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> statsService.getStatsForMonth(testUser, 2024, 5));
    }

    @Test
    void recordLeftSwipe_incrementsCounter() {
        UserStatsMonthly stats = new UserStatsMonthly();
        stats.setUser(testUser);
        stats.setYear(YearMonth.now().getYear());
        stats.setMonth(YearMonth.now().getMonthValue());

        when(statsRepository.findByUserAndYearAndMonth(any(), anyInt(), anyInt()))
                .thenReturn(Optional.of(stats));

        statsService.recordLeftSwipe(testUser);

        assertEquals(1, stats.getLeftSwipes());
        verify(statsRepository, times(1)).save(stats);
    }

    @Test
    void recordConversationStarted_whenTwoUsersProvided_incrementsStatsForBothUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setLogin("user2");

        UserStatsMonthly stats1 = new UserStatsMonthly();
        stats1.setUser(user1);

        UserStatsMonthly stats2 = new UserStatsMonthly();
        stats2.setUser(user2);

        when(statsRepository.findByUserAndYearAndMonth(any(), anyInt(), anyInt()))
                .thenReturn(Optional.of(stats1))
                .thenReturn(Optional.of(stats2));

        statsService.recordConversationStarted(user1, user2);

        assertEquals(1, stats1.getMatchesWithConversation());
        assertEquals(1, stats2.getMatchesWithConversation());
        verify(statsRepository, times(2)).save(any(UserStatsMonthly.class));
    }
}
