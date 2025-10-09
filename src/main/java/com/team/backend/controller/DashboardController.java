package com.team.backend.controller;

import com.team.backend.model.Enum.NotificationType;
import com.team.backend.model.Notification;
import com.team.backend.model.User;
import com.team.backend.model.dto.DashboardDto;
import com.team.backend.model.dto.NotificationDto;
import com.team.backend.model.dto.UserStatsDto;
import com.team.backend.model.mapper.NotificationMapper;
import com.team.backend.model.mapper.UserStatsMapper;
import com.team.backend.service.NotificationService;
import com.team.backend.service.UserService;
import com.team.backend.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;
    private final UserStatsService userStatsService;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);

        String username = currentUser.getUsername();
        String profilePictureUrl = currentUser.getPhotoUrls().isEmpty()
                ? null
                : currentUser.getPhotoUrls().getFirst();

        List<Notification> notificationList = notificationService.getLatestNotifications(currentUser);

        int newMatchesCount = (int) notificationList.stream()
                .filter(n -> n.getType() == NotificationType.NEW_MATCH && !n.isRead())
                .count();

        int newMessagesCount = (int) notificationList.stream()
                .filter(n -> n.getType() == NotificationType.NEW_MESSAGE && !n.isRead())
                .count();

        List<NotificationDto> notifications = notificationList
                .stream()
                .limit(3)
                .map(NotificationMapper::mapToNotificationDto)
                .toList();

        YearMonth currentMonth = YearMonth.now();
        UserStatsDto currentMonthStats = UserStatsMapper.toUserStatsDto(
                userStatsService.getStatsForMonth(currentUser, currentMonth.getYear(), currentMonth.getMonthValue())
        );


        DashboardDto dashboardDto = new DashboardDto(username, profilePictureUrl, newMatchesCount, newMessagesCount, notifications, currentMonthStats);
        return ResponseEntity.ok(dashboardDto);
    }
}