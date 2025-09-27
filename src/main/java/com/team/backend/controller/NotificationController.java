package com.team.backend.controller;

import com.team.backend.model.User;
import com.team.backend.model.dto.NotificationDto;
import com.team.backend.model.mapper.NotificationMapper;
import com.team.backend.service.NotificationService;
import com.team.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getLatest(Authentication auth) {
        User user = userService.getCurrentUser(auth);
        return ResponseEntity.ok(
                notificationService.getLatestNotifications(user)
                        .stream()
                        .map(NotificationMapper::mapToNotificationDto)
                        .toList()
        );
    }

    @PostMapping("read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        notificationService.markAsRead(id, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication auth) {
        User user = userService.getCurrentUser(auth);
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }
}

